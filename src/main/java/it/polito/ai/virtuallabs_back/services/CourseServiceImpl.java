package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.CourseAlreadyExistsException;
import it.polito.ai.virtuallabs_back.repositories.CourseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    UtilityService utilityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CourseRepository courseRepository;

    @Override
    public Optional<CourseDTO> getCourse(String courseName) {
        return courseRepository.findById(courseName).map(course -> modelMapper.map(course, CourseDTO.class));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getTeacherCourses() {
        Teacher teacher = utilityService.getTeacher();
        return courseRepository.getTeacherCourses(teacher.getSerial())
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getStudentCourses() {
        return utilityService.getStudent()
                .getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CourseDTO addCourse(CourseDTO courseDTO) {
        if (courseRepository.existsById(courseDTO.getName()))
            throw new CourseAlreadyExistsException("Course already exists");

        Course course = courseRepository.save(modelMapper.map(courseDTO, Course.class));
        Teacher teacher = utilityService.getTeacher();
        teacher.addCourse(course);

        return modelMapper.map(course, CourseDTO.class);
    }

    @Override
    public CourseDTO modifyCourse(CourseDTO courseDTO) {
        utilityService.courseOwnerValid(courseDTO.getName());

        Course course = utilityService.getCourse(courseDTO.getName());
        course.setTag(courseDTO.getTag());
        course.setMin(courseDTO.getMin());
        course.setMax(courseDTO.getMax());
        course.setEnabled(courseDTO.isEnabled());

        return modelMapper.map(course, CourseDTO.class);
    }

    @Override
    public void deleteCourse(String courseName) {
        utilityService.courseOwnerValid(courseName);

        Course course = utilityService.getCourse(courseName);
        course.getTeachers().forEach(teacher -> teacher.removeCourse(course));

        courseRepository.delete(course);
    }
}
