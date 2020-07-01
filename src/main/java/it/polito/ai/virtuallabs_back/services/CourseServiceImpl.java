package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.CourseAlreadyExistsException;
import it.polito.ai.virtuallabs_back.exception.CourseChangeNotValidException;
import it.polito.ai.virtuallabs_back.exception.CourseNotFoundException;
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
    public List<CourseDTO> getStudentCourses() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentRepository.getOne(principal.getUsername().split("@")[0])
                .getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
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
