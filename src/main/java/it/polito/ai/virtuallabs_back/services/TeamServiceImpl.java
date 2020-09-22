package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.dtos.TeamTokenDTO;
import it.polito.ai.virtuallabs_back.entities.*;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    UtilityService utilityService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamTokenRepository teamTokenRepository;

    @Autowired
    VMRepository vmRepository;

    @Override
    public Optional<TeamDTO> getStudentTeamByCourse(String courseName) {
        return utilityService.getStudent()
                .getTeams()
                .stream()
                .filter(team -> team.getCourse().getName().equals(courseName) && team.getStatus() == 1)
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .findAny();
    }

    @Override
    public Optional<TeamDTO> getStudentTeamByCourseAndSerial(String courseName, String studentSerial) {
        if (!studentRepository.existsById(studentSerial))
            throw new StudentNotFoundException("The student you are looking for does not exist");
        return studentRepository.getOne(studentSerial)
                .getTeams()
                .stream()
                .filter(team -> team.getCourse().getName().equals(courseName) && team.getStatus() == 1)
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .findAny();
    }

    @Override
    public List<TeamDTO> getStudentPendingTeams(String courseName) {
        return utilityService.getStudent()
                .getTeams()
                .stream()
                .filter(team -> team.getStatus() == 0 && team.getCourse().getName().equals(courseName))
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getCourseTeams(String courseName) {
        return utilityService.getCourse(courseName)
                .getTeams()
                .stream()
                .filter(team -> team.getStatus() == 1)
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO proposeTeam(String courseName, String teamName, int timeout, List<String> studentSerials) {
        Course course = utilityService.getCourse(courseName);

        if (!course.isEnabled())
            throw new CourseNotEnabledException("Course is not active");

        if (studentSerials.size() < course.getMin() || studentSerials.size() > course.getMax())
            throw new CourseSizeException("Team does not respect the size of the course");

        if (!studentSerials.contains(utilityService.getStudent().getSerial()))
            throw new UserRequestNotValidException("You must be part of the team");

        if (course.getTeams().stream().anyMatch(team -> team.getName().equals(teamName)))
            throw new TeamNameAlreadyExistException("Name is already used");

        Team team = new Team();

        team.setName(teamName);
        team.setCourse(course);
        TeamDTO teamDTO = modelMapper.map(teamRepository.save(team), TeamDTO.class);
        teamDTO.setDuration(timeout);
        studentSerials.forEach(studentSerial -> {
            if (!studentRepository.existsById(studentSerial))
                throw new StudentNotFoundException("The student you are looking for does not exist");

            Student student = studentRepository.getOne(studentSerial);
            if (!course.getStudents().contains(student))
                throw new StudentNotEnrolledException("One or more student are not enrolled in the course");

            if (courseRepository.getStudentsInTeams(courseName).contains(student))
                throw new StudentAlreadyInTeamException("One or more student are already in a team");

            if (!team.addMember(student))
                throw new StudentDuplicatedException("One or more student are duplicated in the team");

            if (!utilityService.getStudent().getSerial().equals(studentSerial)) {
                TeamToken teamToken = TeamToken.builder()
                        .id(UUID.randomUUID().toString())
                        .studentSerial(studentSerial)
                        .teamId(team.getId())
                        .expiryDate(new Timestamp(System.currentTimeMillis() + timeout * 1000 * 3600))
                        .status(0)
                        .build();
                teamTokenRepository.save(teamToken);
            }
        });

        notificationService.notifyTeam(teamDTO, studentSerials, utilityService.getStudent().getSerial());

        return teamDTO;
    }

    @Override
    public TeamDTO setTeamParams(TeamDTO teamDTO) {
        Team team = utilityService.getTeam(teamDTO.getId());

        if (team.getStatus() == 0)
            throw new TeamChangeNotValidException("Team is no active");

        Teacher teacher = utilityService.getTeacher();
        if (!teacher.getCourses().contains(team.getCourse()))
            throw new CourseNotValidException("You are not allowed to change this team");

        constraintCheck(teamDTO, team);

        team.setActiveInstance(teamDTO.getActiveInstance());
        team.setVcpu(teamDTO.getVcpu());
        team.setDisk(teamDTO.getDisk());
        team.setMaxInstance(teamDTO.getMaxInstance());
        team.setRam(teamDTO.getRam());

        return modelMapper.map(team, TeamDTO.class);
    }

    @Override
    public TeamDTO acceptTeam(TeamTokenDTO teamTokenDTO) {
        Team team = isValid(teamTokenDTO);
        TeamToken teamToken = teamTokenRepository.getOne(teamTokenDTO.getId());
        teamToken.setStatus(1);

        boolean isNotCompleted = teamTokenRepository.findAllByTeamId(teamTokenDTO.getTeamId())
                .stream()
                .anyMatch(token -> token.getStatus() == 0);

        if (!isNotCompleted) {
            team.setStatus(1);
            teamTokenRepository.findAllByTeamId(teamTokenDTO.getTeamId()).forEach(teamToken1 -> teamTokenRepository.delete(teamToken1));

            team.getMembers().forEach(student -> {
                List<Team> teamsToRemove = student.getTeams()
                        .stream()
                        .filter(t -> t.getStatus() == 0 && t.getCourse().getName().equals(team.getCourse().getName()))
                        .collect(Collectors.toList());

                teamsToRemove.forEach(t -> {
                    team.getMembers().forEach(s -> s.removeTeam(t));
                    teamRepository.delete(t);
                });
            });
        }

        return modelMapper.map(team, TeamDTO.class);
    }

    @Override
    public void rejectTeam(TeamTokenDTO teamTokenDTO) {
        teamRepository.delete(isValid(teamTokenDTO));
        teamTokenRepository.findAllByTeamId(teamTokenDTO.getTeamId()).forEach(teamToken -> teamTokenRepository.delete(teamToken));
    }

    @Override
    public void deleteTeam(Long teamId) {
        Team team = utilityService.getTeam(teamId);
        team.setCourse(null);
        List<Student> studentsToRemove = team.getMembers();
        studentsToRemove.forEach(student -> student.removeTeam(team));
        teamRepository.delete(team);
    }

    @Override
    @Scheduled(fixedRate = 1000 * 3600 * 12)
    public void clearTeamToken() {
        teamTokenRepository.findAllByExpiryDateBefore(new Timestamp(System.currentTimeMillis()))
                .forEach(teamToken -> {
                    if (teamRepository.existsById(teamToken.getTeamId())) {
                        teamRepository.delete(teamRepository.getOne(teamToken.getTeamId()));
                    }
                    teamTokenRepository.delete(teamToken);
                });
    }

    private Team isValid(TeamTokenDTO teamTokenDTO) {
        if (!teamTokenRepository.existsById(teamTokenDTO.getId()))
            throw new TeamTokenNotFoundException("The token you are looking for does not exist");

        TeamToken teamToken = teamTokenRepository.getOne(teamTokenDTO.getId());
        if (teamToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis())))
            throw new TeamTokenExpiredException("The token is already expired");

        return utilityService.getTeam(teamTokenDTO.getTeamId());
    }

    private void constraintCheck(TeamDTO teamDTO, Team team) {
        AtomicInteger ram = new AtomicInteger();
        AtomicInteger vcpu = new AtomicInteger();
        AtomicInteger disk = new AtomicInteger();
        AtomicInteger maxInstance = new AtomicInteger();
        AtomicInteger activeInstance = new AtomicInteger();

        team.getVms().forEach(vm -> {
            ram.addAndGet(vm.getRam());
            vcpu.addAndGet(vm.getVcpu());
            disk.addAndGet(vm.getDisk());
            maxInstance.addAndGet(1);
            if (vm.isActive())
                activeInstance.addAndGet(1);
        });

        if (teamDTO.getDisk() < disk.get()
                || teamDTO.getVcpu() < vcpu.get()
                || teamDTO.getRam() < ram.get()
                || teamDTO.getMaxInstance() < maxInstance.get()
                || teamDTO.getActiveInstance() < activeInstance.get())
            throw new TeamChangeNotValidException("It is not possible to modify the team constraints");
    }
}
