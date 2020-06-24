package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDetails loadUserByUsername(String username);

    User addUser(List<String> roles);
}
