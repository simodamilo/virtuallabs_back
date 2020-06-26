package it.polito.ai.virtuallabs_back.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.CourseChangeNotValidException;
import it.polito.ai.virtuallabs_back.exception.CourseNotFoundException;
import it.polito.ai.virtuallabs_back.exception.StudentNotFoundException;
import it.polito.ai.virtuallabs_back.exception.TeacherNotFoundException;
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


    /**
     * With the addCourse method a new course is added and the teacher who insert it is considered
     * the principal teacher. He/She has the possibility to add other teachers by using the
     * addTeacherToCourse method.
     */
    @Override
    public boolean addCourse(CourseDTO course) {
        if (!courseRepository.existsById(course.getName())) {
            Course c = courseRepository.save(modelMapper.map(course, Course.class));
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Teacher teacher = teacherRepository.getOne(principal.getUsername());
            teacher.addCourse(c);
            return true;
        }
        return false;
    }

    /**
     * With the getCourse method all details about the specific course are returned
     */
    @Override
    public Optional<CourseDTO> getCourse(String courseName) {
        return courseRepository.findById(courseName)
                .map(c -> modelMapper.map(c, CourseDTO.class));
    }

    /**
     * With the getAllCourses method all courses with all details are returned
     */
    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * With the getEnrolledStudents method all students enrolled to the specific course are returned
     */
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

    /**
     * With the getTeamsForCourse method all teams of the specific course are returned
     */
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

    /**
     * If the course exists and the teacher has the permissions, the course is modified with data
     * received from the client. All fields are updated, even if they are not changed
     */
    @Override
    public boolean modifyCourse(CourseDTO courseDTO) {
        if (!courseRepository.existsById(courseDTO.getName()))
            throw new CourseNotFoundException("Course not found");

        if (!isValid(courseDTO.getName()))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        Course c = courseRepository.getOne(courseDTO.getName());
        c.setTag(courseDTO.getTag());
        c.setMin(courseDTO.getMin());
        c.setMax(courseDTO.getMax());
        c.setEnabled(courseDTO.isEnabled());

        return true;
    }

    /*
     * With the enableCourse method the specific course is enabled
     *//*
    @Override
    public void enableCourse(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        courseRepository.getOne(courseName).setEnabled(true);
    }

    *//*
     * With the disableCourse method the specific course is disabled
     *//*
    @Override
    public void disableCourse(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        courseRepository.getOne(courseName).setEnabled(false);
    }*/

    /**
     * With the addStudentToCourse method a student is enrolled to a specific course
     */
    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        if (!studentRepository.existsById(studentId))
            throw new StudentNotFoundException("Student not found");

        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        Course c = courseRepository.getOne(courseName);
        if (!c.isEnabled())
            return false;
        return c.addStudent(studentRepository.getOne(studentId));
    }

    /**
     * With the addTeacherToCourse method a teacher is added to a specific course by the chief teacher
     */
    @Override
    public boolean addTeacherToCourse(String teacherId, String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not found");

        if (!teacherRepository.existsById(teacherId))
            throw new TeacherNotFoundException("Teacher not found");

        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        Course c = courseRepository.getOne(courseName);
        return c.addTeacher(teacherRepository.getOne(teacherId));
    }

    /**
     * With the enrollAll method all students are enrolled to the specific course,
     * it invokes the addStudentToCourse method for each student in the list
     */
    @Override
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");

        List<Boolean> result = new ArrayList<>();
        for (String s : studentIds)
            result.add(addStudentToCourse(s, courseName));

        return result;
    }

    /**
     * With the enrollAll method all students are enrolled to the specific course,
     * it invokes the enrollAll by passing a list of student serials and the course name
     */
    @Override
    public List<Boolean> enrollCsv(Reader r, String courseName) {
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

    /**
     * With the deleteCourse method the passed course is removed if exists and if the current teacher is one
     * of the teachers of the courses. All the relationship are also deleted, for this reason a copy of the
     * teachers list is taken in order to delete them from the course.
     */
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

    /**
     * If the course exists and the teacher has the permissions, the student is deleted
     * from the course.
     */
    @Override
    public boolean deleteStudentFromCourse(String studentId, String courseName) {
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

        return c.removeStudent(toRemove);
    }

    /**
     * This method is used to get all the courses of the authenticated teacher. In order to perform it a
     * custom query was created in courseRepository.
     */
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
        /*return userRepository.findByUsername(principal.getUsername()).getRoles().contains("ROLE_ADMIN") ||
                teacher.getCourses().contains(course);*/
        return userRepository.findByUsername(principal.getUsername()).getRoles().contains("ROLE_TEACHER") &&
                teacher.getCourses().contains(course);
    }

}
