package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.exception.CourseChangeNotValidException;
import it.polito.ai.virtuallabs_back.exception.CourseNotFoundException;
import it.polito.ai.virtuallabs_back.exception.TeacherNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.CourseRepository;
import it.polito.ai.virtuallabs_back.repositories.TeacherRepository;
import it.polito.ai.virtuallabs_back.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    UserService userService;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public Optional<TeacherDTO> getTeacher(String teacherId) {
        return teacherRepository.findById(teacherId)
                .map(t -> modelMapper.map(t, TeacherDTO.class));
    }

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
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
    public TeacherDTO uploadImage(byte[] image) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher teacher = teacherRepository.getOne(principal.getUsername().split("@")[0]);

        teacher.setImage(/*compressBytes(image)*/image);
        return modelMapper.map(teacher, TeacherDTO.class);
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


    /*private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
        }

        return outputStream.toByteArray();
    }*/

}
