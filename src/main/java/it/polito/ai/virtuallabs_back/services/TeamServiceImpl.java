package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.*;
import it.polito.ai.virtuallabs_back.exception.*;
import it.polito.ai.virtuallabs_back.repositories.CourseRepository;
import it.polito.ai.virtuallabs_back.repositories.StudentRepository;
import it.polito.ai.virtuallabs_back.repositories.TeamRepository;
import it.polito.ai.virtuallabs_back.repositories.TeamTokenRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<TeamDTO> getStudentTeamByCourse(String courseName) {
        return utilityService.getStudent()
                .getTeams()
                .stream()
                .filter(team -> team.getCourse().getName().equals(courseName))
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .findAny();
    }

    @Override
    public List<TeamDTO> getStudentTeams() {
        return utilityService.getStudent()
                .getTeams()
                .stream()
                .filter(team -> team.getStatus() == 1)
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getStudentPendingTeams() {
        return utilityService.getStudent()
                .getTeams()
                .stream()
                .filter(team -> team.getStatus() == 0)
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
            throw new UserRequestNotValidException("You are not part of the team");

        Team team = new Team();
        studentSerials.forEach(studentSerial -> {
            if (!studentRepository.existsById(studentSerial))
                throw new StudentNotFoundException("Student not found");

            Student student = studentRepository.getOne(studentSerial);
            if (!course.getStudents().contains(student))
                throw new StudentNotEnrolledException("One or more student are not enrolled in the course");

            if (courseRepository.getStudentsInTeams(courseName).contains(student))
                throw new StudentAlreadyInTeamException("One or more student are already in team");

            if (!team.addMember(student))
                throw new StudentDuplicatedException("Team contains duplicate");
        });

        team.setName(teamName);
        team.setCourse(course);
        TeamDTO teamDTO = modelMapper.map(teamRepository.save(team), TeamDTO.class);
        teamDTO.setDuration(timeout);
        notificationService.notifyTeam(teamDTO, studentSerials);

        return teamDTO;
    }

    @Override
    public TeamDTO setTeamParams(TeamDTO teamDTO) {
        Team team = utilityService.getTeam(teamDTO.getId());

        if (team.getStatus() == 0)
            throw new TeamNotFoundException("Team is no active");

        Teacher teacher = utilityService.getTeacher();
        if (!teacher.getCourses().contains(team.getCourse()))
            throw new CourseNotValidException("You are not allowed to change this team");

        //utilityService.constraintsCheck(new VMDTO(), teamDTO.getId()); //TODO questo non va bene
        constraintCheck(teamDTO, team);

        team.setActiveInstance(teamDTO.getActiveInstance());
        team.setVcpu(teamDTO.getVcpu());
        team.setDisk(teamDTO.getDisk());
        team.setMaxInstance(teamDTO.getMaxInstance());
        team.setRam(teamDTO.getRam());

        return modelMapper.map(team, TeamDTO.class);
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
        Student student = utilityService.getStudent();
        if (!teamTokenRepository.existsByTeamIdAndStudentSerial(teamId, student.getSerial()))
            throw new TeamTokenNotFoundException("token not found");

        TeamToken teamToken = teamTokenRepository.getByTeamIdAndStudentSerial(teamId, student.getSerial());
        if (teamToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis())))
            throw new TeamTokenExpiredException("Token already expired");

        return utilityService.getTeam(teamId);
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
