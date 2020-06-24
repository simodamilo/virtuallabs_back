package it.polito.ai.virtuallabs_back.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.virtuallabs_back.dtos.CourseDTO;
import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeacherDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.*;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
public class TeamServiceImpl implements TeamService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserService userService;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    NotificationService notificationService;

    @Override
    public boolean addCourse(CourseDTO course) {
        if (courseRepository.existsById(course.getName())) return false;
        courseRepository.save(modelMapper.map(course, Course.class));
        return true;
    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {
        return courseRepository.findById(name)
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
    public boolean addStudent(StudentDTO student) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_STUDENT");
        User user = userService.addUser(roles);
        student.setId(user.getUsername());
        studentRepository.save(modelMapper.map(student, Student.class));
        return true;
    }

    @Override
    public boolean addTeacher(TeacherDTO teacher) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_TEACHER");
        User user = userService.addUser(roles);
        teacher.setId(user.getUsername());
        teacherRepository.save(modelMapper.map(teacher, Teacher.class));
        return true;
    }

    @Override
    public Optional<TeacherDTO> getTeacher(String teacherId) {
        return teacherRepository.findById(teacherId)
                .map(t -> modelMapper.map(t, TeacherDTO.class));
    }

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
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not found");
        return courseRepository.getOne(courseName)
                .getStudents()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not found");
        if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException("Student not found");
        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");
        Course c = courseRepository.getOne(courseName);
        if (!c.isEnabled()) return false;
        return c.addStudent(studentRepository.getOne(studentId));
    }

    @Override
    public boolean addTeacherToCourse(String teacherId, String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not found");
        if (!teacherRepository.existsById(teacherId)) throw new TeacherNotFoundException("Teacher not found");
        Course c = courseRepository.getOne(courseName);
        return c.addTeacher(teacherRepository.getOne(teacherId));
    }

    @Override
    public void enableCourse(String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not found");
        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");
        courseRepository.getOne(courseName).setEnabled(true);
    }

    @Override
    public void disableCourse(String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not found");
        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");
        courseRepository.getOne(courseName).setEnabled(false);
    }

    @Override
    public List<Boolean> addAll(List<StudentDTO> students) {
        List<Boolean> result = new ArrayList<>();
        for (StudentDTO s : students) result.add(addStudent(s));
        return result;
    }

    @Override
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");
        List<Boolean> result = new ArrayList<>();
        for (String s : studentIds) result.add(addStudentToCourse(s, courseName));
        return result;
    }

    @Override
    public List<Boolean> addAndEnroll(Reader r, String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not found");
        Course c = courseRepository.getOne(courseName);
        if (!c.isEnabled()) throw new CourseNotEnabledException("Course not enabled");
        ;
        if (!isValid(courseName))
            throw new CourseChangeNotValidException("You have no permission to change this course");
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        List<StudentDTO> studentDTOList = csvToBean.parse();
        List<Boolean> result = new ArrayList<>();
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_STUDENT");
        studentDTOList.forEach(studentDTO -> {
            User user = userService.addUser(roles);
            studentDTO.setId(user.getUsername());
            Student student = modelMapper.map(studentDTO, Student.class);
            student.addCourse(c);
            studentRepository.save(student);
            result.add(true);
            //per l'implementazione i nuovi utenti verranno sempre aggiunti per questo il compito è lasciato all'admin
        });
        return result;
    }

    private boolean isValid(String courseName) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher teacher = teacherRepository.getOne(principal.getUsername());
        Course course = courseRepository.getOne(courseName);
        return userRepository.findByUsername(principal.getUsername()).getRoles().contains("ROLE_ADMIN") ||
                teacher.getCourses().contains(course);
    }

    @Override
    public List<CourseDTO> getCourses() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentRepository.getOne(principal.getUsername())
                .getCourses()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeamsForStudent() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentRepository.getOne(principal.getUsername())
                .getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) {
        if (!teamRepository.existsById(teamId)) throw new TeamNotFoundException("Team not found");
        return teamRepository.getOne(teamId)
                .getMembers()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO proposeTeam(String courseName, String name, List<String> memberIds) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not found");
        Course c = courseRepository.getOne(courseName);
        if (!c.isEnabled()) throw new CourseNotEnabledException("Course is not active");
        if (memberIds.size() < c.getMin() || memberIds.size() > c.getMax())
            throw new CourseSizeException("Team does not respect the size of the course");
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!memberIds.contains(user.getUsername()))
            throw new UserRequestNotValidException("You are not part of the team");
        Team team = new Team();
        for (String s : memberIds) {
            if (!studentRepository.existsById(s)) throw new StudentNotFoundException("Student not found");
            Student student = studentRepository.getOne(s);
            if (!c.getStudents().contains(student))
                throw new StudentNotEnrolledException("One or more student are not enrolled in the course");
            if (courseRepository.getStudentsInTeams(courseName).contains(student))
                throw new StudentAlreadyInTeamException("One or more student are already in team");
            if (!team.addMember(student)) throw new StudentDuplicatedException("Team contains duplicate");
        }
        team.setName(name);
        team.setCourse(c);
        TeamDTO teamDTO = modelMapper.map(teamRepository.save(team), TeamDTO.class);
        notificationService.notifyTeam(teamDTO, memberIds);
        return teamDTO;
    }

    @Override
    public List<TeamDTO> getTeamForCourse(String courseName) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not Found");
        return courseRepository.getOne(courseName)
                .getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsInTeams(String courseName) {
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
    public void enableTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) throw new TeamNotFoundException("Team not found");
        teamRepository.getOne(teamId).setStatus(1);
    }

    @Override
    public void evictTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) throw new TeamNotFoundException("Team not found");
        teamRepository.deleteById(teamId);
    }

    @Override
    @Scheduled(fixedRate = 600000)
    public void clearToken() {
        tokenRepository.findAllByExpiryDateBefore(new Timestamp(System.currentTimeMillis()))
                .forEach(t -> {
                    evictTeam(t.getTeamId());
                    tokenRepository.delete(t);
                });
    }
}
