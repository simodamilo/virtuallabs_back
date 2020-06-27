package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.entities.AppUser;
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
        AppUser appUser = userRepository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        List<String> roles = appUser.getRoles();
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(),
                grantedAuthorities);
    }

    @Override
    public AppUser addUser(List<String> roles) {
        AppUser appUser = new AppUser();
        appUser = userRepository.save(appUser);
        String temporaryPassword = RandomStringUtils.random(10, true, true);
        appUser.setPassword(passwordEncoder.encode(temporaryPassword));
        appUser.setRoles(roles);
        if (roles.contains("ROLE_ADMIN"))
            appUser.setUsername(appUser.getId().toString());
        else if (roles.contains("ROLE_TEACHER"))
            appUser.setUsername("d" + appUser.getId().toString());
        else if (roles.contains("ROLE_STUDENT"))
            appUser.setUsername("s" + appUser.getId().toString());
        notificationService.notifyUser(appUser, temporaryPassword);
        return appUser;
    }
}
