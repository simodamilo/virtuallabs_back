package it.polito.ai.virtuallabs_back.services;


import it.polito.ai.virtuallabs_back.dtos.RegistrationRequest;
import it.polito.ai.virtuallabs_back.entities.AppUser;
import it.polito.ai.virtuallabs_back.entities.Student;
import it.polito.ai.virtuallabs_back.entities.Teacher;
import it.polito.ai.virtuallabs_back.entities.UserToken;
import it.polito.ai.virtuallabs_back.repositories.StudentRepository;
import it.polito.ai.virtuallabs_back.repositories.TeacherRepository;
import it.polito.ai.virtuallabs_back.repositories.UserRepository;
import it.polito.ai.virtuallabs_back.repositories.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    @Lazy
    NotificationService notificationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username);
        if (appUser == null || !appUser.isStatus())
            throw new UsernameNotFoundException("Username or password not valid");
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        List<String> roles = appUser.getRoles();
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(),
                grantedAuthorities);
    }

    @Override
    public boolean registration(RegistrationRequest registrationRequest) {
        if (!registrationRequest.getEmail().endsWith("studenti.polito.it") ||
                !registrationRequest.getEmail().endsWith("polito.it"))
            return false;

        String serial = registrationRequest.getEmail().split("@")[0];
        if ((userRepository.findByUsername(registrationRequest.getEmail()) != null)
                || !serial.equals(registrationRequest.getSerial()))
            return false;
        AppUser user = AppUser.builder()
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .username(registrationRequest.getEmail())
                .status(false)
                .build();
        userRepository.save(user);

        UserToken token = UserToken.builder()
                .id(UUID.randomUUID().toString())
                .appUserId(user.getId())
                .name(registrationRequest.getName())
                .surname(registrationRequest.getSurname())
                .expiryDate(new Timestamp(System.currentTimeMillis() + /*3600000*24*/120000)) //TODO
                .build();
        userTokenRepository.save(token);

        notificationService.notifyUser(user, token);
        return true;
    }

    @Override
    public boolean confirmRegistration(String token) {
        if (!userTokenRepository.existsById(token))
            return false;
        UserToken token1 = userTokenRepository.getOne(token);
        if (token1.getExpiryDate().before(new Timestamp(System.currentTimeMillis())))
            return false;

        AppUser user = userRepository.getOne(token1.getAppUserId());
        List<String> roles = new ArrayList<>();
        if (user.getUsername().endsWith("studenti.polito.it")) {
            roles.add("ROLE_STUDENT");
            addStudent(token1, user.getUsername());
        } else if (user.getUsername().endsWith("polito.it")) {
            roles.add("ROLE_TEACHER");
            addTeacher(token1, user.getUsername());
        } else
            return false;

        user.setStatus(true);
        user.setRoles(roles);
        userTokenRepository.delete(token1);
        return true;
    }

    @Override
    @Scheduled(fixedRate = 10000)
    public void clearUser() {
        userTokenRepository.findAllByExpiryDateBefore(new Timestamp(System.currentTimeMillis()))
                .forEach(t -> {
                    if (!userRepository.findById(t.getAppUserId()).isPresent())
                        return;
                    AppUser user = userRepository.findById(t.getAppUserId()).get();
                    userRepository.delete(user);
                    userTokenRepository.delete(t);
                });
    }


    private void addStudent(UserToken token, String email) {
        String serial = email.split("@")[0];
        Student student = Student.builder()
                .serial(serial)
                .email(email)
                .name(token.getName())
                .surname(token.getSurname())
                .build();
        studentRepository.save(student);
    }

    private void addTeacher(UserToken token, String email) {
        String serial = email.split("@")[0];
        Teacher teacher = Teacher.builder()
                .serial(serial)
                .email(email)
                .name(token.getName())
                .surname(token.getSurname())
                .build();
        teacherRepository.save(teacher);
    }
}
