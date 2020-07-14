package it.polito.ai.virtuallabs_back.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.exception.CourseNotEnabledException;
import it.polito.ai.virtuallabs_back.exception.CourseNotFoundException;
import it.polito.ai.virtuallabs_back.exception.StudentAlreadyInCourseException;
import it.polito.ai.virtuallabs_back.exception.StudentNotFoundException;
import it.polito.ai.virtuallabs_back.repositories.CourseRepository;
import it.polito.ai.virtuallabs_back.repositories.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    UtilityService utilityService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        return studentRepository.findById(studentId).map(student -> modelMapper.map(student, StudentDTO.class));
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
        return utilityService.getCourse(courseName)
                .getStudents()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getAvailableStudents(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not Found");

        return courseRepository.getStudentsNotInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getEngagedStudents(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not Found");

        return courseRepository.getStudentsInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getTeamStudents(Long teamId) {
        return utilityService.getTeam(teamId)
                .getMembers()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getVmOwners(Long vmId) {
        return utilityService.getVm(vmId)
                .getOwners()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO addStudentToCourse(String studentId, String courseName) {
        if (!studentRepository.existsById(studentId))
            throw new StudentNotFoundException("Student not found");
        Student student = studentRepository.getOne(studentId);

        utilityService.courseOwnerValid(courseName);

        Course course = utilityService.getCourse(courseName);
        if (!course.isEnabled())
            throw new CourseNotEnabledException("Course is not enabled");

        if (!course.addStudent(student))
            throw new StudentAlreadyInCourseException("Student already enrolled");

        return modelMapper.map(student, StudentDTO.class);
    }

    @Override
    public List<StudentDTO> enrollAll(List<String> studentSerials, String courseName) {
        utilityService.courseOwnerValid(courseName);

        List<StudentDTO> result = new ArrayList<>();
        studentSerials.forEach(studentSerial -> result.add(addStudentToCourse(studentSerial, courseName)));

        return result;
    }

    @Override
    public List<StudentDTO> enrollCsv(Reader reader, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(reader)
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
    public StudentDTO uploadImage(byte[] image) {
        Student student = utilityService.getStudent();

        student.setImage(/*compressBytes(image)*/image);
        return modelMapper.map(student, StudentDTO.class);
    }

    @Override
    public void deleteStudentFromCourse(String studentSerial, String courseName) {
        utilityService.courseOwnerValid(courseName);
        Course course = utilityService.getCourse(courseName);

        course.getStudents().forEach(student -> {
            if (student.getSerial().equals(studentSerial))
                student.removeCourse(course);
        });
    }
}
