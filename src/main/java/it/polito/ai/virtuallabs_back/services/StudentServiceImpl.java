package it.polito.ai.virtuallabs_back.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamTokenDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Solution;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
import java.sql.Timestamp;
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

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamTokenRepository teamTokenRepository;

    @Autowired
    SolutionRepository solutionRepository;

    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        return studentRepository.findById(studentId).map(student -> modelMapper.map(student, StudentDTO.class));
    }

    @Override
    public byte[] getStudentImage(String studentSerial) {
        return utilityService.getStudent().getImage();
    }

    @Override
    public List<StudentDTO> getAllStudents(String courseName) {
        return studentRepository.findAll()
                .stream()
                .filter(student -> !utilityService.getCourse(courseName).getStudents().contains(student))
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
    public StudentDTO getSolutionStudent(Long solutionId) {
        Student student = utilityService.getSolution(solutionId).getStudent();
        return modelMapper.map(student, StudentDTO.class);
    }

    @Override
    public List<StudentDTO> getAvailableStudents(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("The course you are looking for does not exist");

        return courseRepository.getStudentsNotInTeams(courseName)
                .stream()
                .filter(student -> !student.getSerial().equals(utilityService.getStudent().getSerial()))
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
    public TeamTokenDTO getStudentTeamStatus(Long teamId, String studentSerial) {
        if (!studentRepository.existsById(studentSerial))
            throw new StudentNotFoundException("The student you are looking for does not exist");

        if (!teamRepository.existsById(teamId))
            throw new TeamNotFoundException("The team you are looking for does not exist");

        if (!teamTokenRepository.existsByTeamIdAndStudentSerial(teamId, studentSerial))
            return null;
        else
            return modelMapper.map(teamTokenRepository.getByTeamIdAndStudentSerial(teamId, studentSerial), TeamTokenDTO.class);
    }

    @Override
    public StudentDTO addStudentToCourse(String studentSerial, String courseName) {
        if (!studentRepository.existsById(studentSerial))
            throw new StudentNotFoundException("The student you are looking for does not exist");
        Student student = studentRepository.getOne(studentSerial);

        utilityService.courseOwnerValid(courseName);

        Course course = utilityService.getCourse(courseName);
        if (!course.isEnabled())
            throw new CourseNotEnabledException("Course is not active");

        if (!course.addStudent(student))
            throw new StudentAlreadyInCourseException("The student is already enrolled in the course");

        course.getAssignments().forEach(assignment -> {
            Solution solution = new Solution();
            if (assignment.isTerminated()) solution.setModifiable(false);
            else solution.setModifiable(true);
            solution.setStudent(student);
            solution.setAssignment(assignment);
            solution.setState(Solution.State.NULL);
            solution.setDeliveryTs(new Timestamp(System.currentTimeMillis()));
            solutionRepository.save(solution);
        });

        return modelMapper.map(student, StudentDTO.class);
    }

    @Override
    public List<StudentDTO> enrollCsv(Reader reader, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(reader)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        List<StudentDTO> students = csvToBean.parse();

        List<String> studentSerials = new ArrayList<>();
        for (StudentDTO student : students)
            studentSerials.add(student.getSerial());

        return enrollAll(studentSerials, courseName);
    }

    @Override
    public byte[] uploadImage(byte[] image) {
        Student student = utilityService.getStudent();

        student.setImage(image);
        return student.getImage();
    }

    @Override
    public void deleteStudentFromCourse(String studentSerial, String courseName) {
        utilityService.courseOwnerValid(courseName);
        Course course = utilityService.getCourse(courseName);
        if (!studentRepository.existsById(studentSerial))
            throw new StudentNotFoundException("The student you are looking for does not exist");
        if (!course.getStudents().contains(studentRepository.getOne(studentSerial)))
            throw new StudentAlreadyInCourseException("The student is not enrolled in the course");
        Student student = studentRepository.getOne(studentSerial);
        if (student.getTeams().stream().anyMatch(team -> team.getCourse() == course))
            throw new StudentAlreadyInTeamException("Delete the team before to remove the student");

        student.removeCourse(course);
        List<Solution> solutionsToRemove = studentRepository.getOne(studentSerial).getSolutions()
                .stream()
                .filter(solution -> solution.getAssignment().getCourse() == course)
                .collect(Collectors.toList());
        solutionsToRemove.forEach(solution -> solutionRepository.delete(solution));

    }


    private List<StudentDTO> enrollAll(List<String> studentSerials, String courseName) {
        utilityService.courseOwnerValid(courseName);

        List<StudentDTO> result = new ArrayList<>();
        studentSerials.forEach(studentSerial -> result.add(addStudentToCourse(studentSerial, courseName)));

        return result;
    }
}
