package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.StudentDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.Course;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.entities.Team;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    UserService userService;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    NotificationService notificationService;

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
    public TeamDTO acceptTeam(TeamDTO teamDTO) {
        return null;
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
            throw new CourseNotValidException("Tou are not allowed to change this team");
        team.setActiveInstance(teamDTO.getActiveInstance());
        team.setVcpu(teamDTO.getVcpu());
        team.setDisk(teamDTO.getDisk());
        team.setMaxInstance(teamDTO.getMaxInstance());
        team.setRam(teamDTO.getRam());
        return modelMapper.map(team, TeamDTO.class);
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

//    @Override
//    @Scheduled(fixedRate = 600000)
//    public void clearToken() {
//        tokenRepository.findAllByExpiryDateBefore(new Timestamp(System.currentTimeMillis()))
//                .forEach(t -> {
//                    evictTeam(t.getTeamId());
//                    tokenRepository.delete(t);
//                });
//    }

}
