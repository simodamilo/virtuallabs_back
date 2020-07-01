package it.polito.ai.virtuallabs_back.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.*;
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
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        return studentRepository.findById(studentId)
                .map(s -> modelMapper.map(s, StudentDTO.class));
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
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
    public List<StudentDTO> getTeamStudents(Long teamId) {
        if (!teamRepository.existsById(teamId)) throw new TeamNotFoundException("Team not found");
        return teamRepository.getOne(teamId)
                .getMembers()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
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
    public List<StudentDTO> getEngagedStudents(String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not Found");
        return courseRepository.getStudentsInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getAvailableStudents(String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not Found");
        return courseRepository.getStudentsNotInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
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
    public StudentDTO uploadImage(byte[] image) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername().split("@")[0]);

        student.setImage(/*compressBytes(image)*/image);
        return modelMapper.map(student, StudentDTO.class);
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
