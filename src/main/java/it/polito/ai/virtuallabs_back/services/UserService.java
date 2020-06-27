package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.RegistrationRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDetails loadUserByUsername(String username);

    boolean registration(RegistrationRequest registrationRequest);

    boolean confirmRegistration(String token);

    void clearUser();
}
