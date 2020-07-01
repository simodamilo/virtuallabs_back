package it.polito.ai.virtuallabs_back.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.CourseRepository;
import it.polito.ai.virtuallabs_back.repositories.StudentRepository;
import it.polito.ai.virtuallabs_back.repositories.TeacherRepository;
import it.polito.ai.virtuallabs_back.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;


    @Override
    public CourseDTO addCourse(CourseDTO courseDTO) {
        if (courseRepository.existsById(courseDTO.getName()))
            throw new CourseAlreadyExistsException("Course already exists");

        Course course = courseRepository.save(modelMapper.map(courseDTO, Course.class));
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher teacher = teacherRepository.getOne(principal.getUsername().split("@")[0]);
        teacher.addCourse(course);

        return modelMapper.map(course, CourseDTO.class);
    }

    @Override
    public Optional<CourseDTO> getCourse(String courseName) {
        return courseRepository.findById(courseName)
                .map(c -> modelMapper.map(c, CourseDTO.class));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        return courseRepository.getOne(courseName)
                .getStudents()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeamsForCourse(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not Found");

        return courseRepository.getOne(courseName)
                .getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO modifyCourse(CourseDTO courseDTO) {
        if (!courseRepository.existsById(courseDTO.getName()))
            throw new CourseNotFoundException("Course not found");

        if (!isValid(courseDTO.getName()))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        Course c = courseRepository.getOne(courseDTO.getName());
        c.setTag(courseDTO.getTag());
        c.setMin(courseDTO.getMin());
        c.setMax(courseDTO.getMax());
        c.setEnabled(courseDTO.isEnabled());

        return modelMapper.map(c, CourseDTO.class);
    }

    @Override
    public StudentDTO addStudentToCourse(String studentId, String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        if (!studentRepository.existsById(studentId))
            throw new StudentNotFoundException("Student not found");
        Student student = studentRepository.getOne(studentId);

        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        Course course = courseRepository.getOne(courseName);
        if (!course.isEnabled())
            throw new CourseNotEnabledException("Course is not enabled");

        if (course.addStudent(student))
            return modelMapper.map(student, StudentDTO.class);
        else
            return null;
    }

    @Override
    public TeacherDTO addTeacherToCourse(String teacherId, String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        if (!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException("Teacher not found");
        Teacher teacher = teacherRepository.getOne(teacherId);

        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        Course course = courseRepository.getOne(courseName);
        if (course.addTeacher(teacher))
            return modelMapper.map(teacher, TeacherDTO.class);
        else
            return null;
    }

    @Override
    public List<StudentDTO> enrollAll(List<String> studentIds, String courseName) {
        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        List<StudentDTO> result = new ArrayList<>();
        for (String s : studentIds)
            result.add(addStudentToCourse(s, courseName));

        return result;
    }

    @Override
    public List<StudentDTO> enrollCsv(Reader r, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<StudentDTO> students = csvToBean.parse();

        List<String> studentIds = new ArrayList<>();
        for (StudentDTO student : students)
            studentIds.add(student.getSerial());

        return enrollAll(studentIds, courseName);
    }

    @Override
    public void deleteCourse(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        Course c = courseRepository.getOne(courseName);
        List<Teacher> teachers = c.getTeachers();

        List<Teacher> toRemove = new ArrayList<>(teachers);
        toRemove.forEach(c::removeTeacher);

        courseRepository.delete(c);
    }

    @Override
    public void deleteStudentFromCourse(String studentId, String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        Course c = courseRepository.getOne(courseName);
        List<Student> students = c.getStudents();
        Student toRemove = new Student();
        for (Student s : students) {
            if (s.getSerial().equals(studentId))
                toRemove = s;
        }

        c.removeStudent(toRemove);
    }

    @Override
    public List<CourseDTO> getTeacherCourses() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher teacher = teacherRepository.getOne(principal.getUsername());
        return courseRepository.getTeacherCourses(teacher.getSerial())
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }


    /**
     * It is an internal method used to check if the current user, that should be a teacher, is valid.
     * A teacher is valid if he/she is actually a teacher for the passed course.
     */
    private boolean isValid(String courseName) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher teacher = teacherRepository.getOne(principal.getUsername());
        Course course = courseRepository.getOne(courseName);

        return userRepository.findByUsername(principal.getUsername()).getRoles().contains("ROLE_TEACHER") &&
                teacher.getCourses().contains(course);
    }

}
