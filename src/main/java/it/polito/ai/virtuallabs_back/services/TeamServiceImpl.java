package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.*;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    VMRepository vmRepository;

    @Autowired
    UserService userService;

    @Autowired
    TeamTokenRepository teamTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    NotificationService notificationService;

    @Override
    public List<TeamDTO> getStudentTeams() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentRepository.getOne(principal.getUsername().split("@")[0])
                .getTeams()
                .stream()
                .filter(team -> team.getStatus() == 1)
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getStudentPendingTeams() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentRepository.getOne(principal.getUsername().split("@")[0])
                .getTeams()
                .stream()
                .filter(team -> team.getStatus() == 0)
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getCourseTeams(String courseName) {
        if (!courseRepository.existsById(courseName))
            throw new CourseNotFoundException("Course not Found");
        return courseRepository.getOne(courseName)
                .getTeams()
                .stream()
                .filter(team -> team.getStatus() == 1)
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO proposeTeam(String courseName, String teamName, List<String> studentSerials) {
        if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("Course not found");
        Course c = courseRepository.getOne(courseName);
        if (!c.isEnabled()) throw new CourseNotEnabledException("Course is not active");
        if (studentSerials.size() < c.getMin() || studentSerials.size() > c.getMax())
            throw new CourseSizeException("Team does not respect the size of the course");
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!studentSerials.contains(user.getUsername()))
            throw new UserRequestNotValidException("You are not part of the team");
        Team team = new Team();
        for (String serial : studentSerials) {
            if (!studentRepository.existsById(serial)) throw new StudentNotFoundException("Student not found");
            Student student = studentRepository.getOne(serial);
            if (!c.getStudents().contains(student))
                throw new StudentNotEnrolledException("One or more student are not enrolled in the course");
            if (courseRepository.getStudentsInTeams(courseName).contains(student))
                throw new StudentAlreadyInTeamException("One or more student are already in team");
            if (!team.addMember(student)) throw new StudentDuplicatedException("Team contains duplicate");
        }
        team.setName(teamName);
        team.setCourse(c);
        TeamDTO teamDTO = modelMapper.map(teamRepository.save(team), TeamDTO.class);
        notificationService.notifyTeam(teamDTO, studentSerials);
        return teamDTO;
    }

    @Override
    public TeamDTO acceptTeam(Long teamId) {
        Team team = isValid(teamId);
        if (teamTokenRepository.findAllByTeamId(teamId).isEmpty())
            team.setStatus(1);
        return modelMapper.map(team, TeamDTO.class);
    }

    @Override
    public void rejectTeam(Long teamId) {
        teamRepository.delete(isValid(teamId));
    }

    @Override
    public TeamDTO setTeamParams(TeamDTO teamDTO) {
        if (!teamRepository.existsById(teamDTO.getId()))
            throw new TeamNotFoundException("Team not found");
        Team team = teamRepository.getOne(teamDTO.getId());
        if (team.getStatus() == 0)
            throw new TeamNotFoundException("Team is no active");
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Teacher t = teacherRepository.getOne(principal.getUsername().split("@")[0]);
        if (!t.getCourses().contains(team.getCourse()))
            throw new CourseNotValidException("You are not allowed to change this team");

        team.getVms().forEach(vm -> {
            int totalDisk = +vm.getDisk();
            int totalRam = +vm.getRam();
            int totalVcpu = +vm.getVcpu();
            if (totalDisk > teamDTO.getDisk() || totalRam > teamDTO.getRam() || totalVcpu > teamDTO.getVcpu())
                throw new TeamChangeNotValidException("New parameters do not respect current values");
        });
        team.setActiveInstance(teamDTO.getActiveInstance());
        team.setVcpu(teamDTO.getVcpu());
        team.setDisk(teamDTO.getDisk());
        team.setMaxInstance(teamDTO.getMaxInstance());
        team.setRam(teamDTO.getRam());
        return modelMapper.map(team, TeamDTO.class);
    }

    @Override
    @Scheduled(fixedRate = 600000)
    public void clearTeamToken() {
        teamTokenRepository.findAllByExpiryDateBefore(new Timestamp(System.currentTimeMillis()))
                .forEach(teamToken -> {
                    if (teamRepository.existsById(teamToken.getTeamId())) {
                        teamRepository.delete(teamRepository.getOne(teamToken.getTeamId()));
                    }
                    teamTokenRepository.delete(teamToken);
                });
    }

    private Team isValid(Long teamId) {
        if (!teamRepository.existsById(teamId))
            throw new TeamNotFoundException("Team not found");
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.getOne(principal.getUsername().split("@")[0]);
        if (!teamTokenRepository.existsByTeamIdAndStudentSerial(teamId, student.getSerial()))
            throw new TeamTokenNotFoundException("token not found");
        TeamToken teamToken = teamTokenRepository.getByTeamIdAndStudentSerial(teamId, student.getSerial());
        if (teamToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis())))
            throw new TeamTokenExpiredException("Token already expired");
        return teamRepository.getOne(teamId);
    }
}
