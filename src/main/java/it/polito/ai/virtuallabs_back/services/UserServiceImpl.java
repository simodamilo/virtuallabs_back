package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.entities.User;
import it.polito.ai.virtuallabs_back.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        List<String> roles = user.getRoles();
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                grantedAuthorities);
    }

    @Override
    public User addUser(List<String> roles) {
        User user = new User();
        user = userRepository.save(user);
        String temporaryPassword = RandomStringUtils.random(10, true, true);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setRoles(roles);
        /*if (roles.contains("ROLE_ADMIN"))
            user.setUsername(user.getId().toString());*/
        /*else */
        if (roles.contains("ROLE_TEACHER"))
            user.setUsername("d" + user.getId().toString());
        else if (roles.contains("ROLE_STUDENT"))
            user.setUsername("s" + user.getId().toString());
        notificationService.notifyUser(user, temporaryPassword);
        return user;
    }
}
