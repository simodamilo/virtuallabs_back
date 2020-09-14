package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.CourseAlreadyExistsException;
import it.polito.ai.virtuallabs_back.exception.CourseSizeException;
import it.polito.ai.virtuallabs_back.repositories.CourseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new CourseAlreadyExistsException("The course already exists");
        if (courseDTO.getMax() < courseDTO.getMin())
            throw new CourseSizeException("Team size is not acceptable");

        Course course = courseRepository.save(modelMapper.map(courseDTO, Course.class));
        Teacher teacher = utilityService.getTeacher();
        teacher.addCourse(course);

        return modelMapper.map(course, CourseDTO.class);
    }

    @Override
    public CourseDTO modifyCourse(CourseDTO courseDTO) {
        if (courseDTO.getMax() < courseDTO.getMin())
            throw new CourseSizeException("Team size is not acceptable");
        utilityService.courseOwnerValid(courseDTO.getName());

        Course course = utilityService.getCourse(courseDTO.getName());

        if (course.getTeams().stream().anyMatch(team -> team.getMembers().size() < courseDTO.getMin() ||
                team.getMembers().size() > courseDTO.getMax())) {
            throw new CourseSizeException("Team size is not acceptable");
        }

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
        List<Teacher> teachers = new ArrayList<>(course.getTeachers());
        teachers.forEach(teacher -> teacher.removeCourse(course));
        List<Student> students = new ArrayList<>(course.getStudents());
        students.forEach(student -> student.removeCourse(course));

        courseRepository.delete(course);
    }
}
