package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.RegistrationRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    /**
     * @param username
     * @return
     */
    UserDetails loadUserByUsername(String username);

    /**
     * @param registrationRequest
     * @return
     */
    boolean registration(RegistrationRequest registrationRequest);

    /**
     * @param token
     * @return
     */
    boolean confirmRegistration(String token);

    /**
     *
     */
    // void clearUser();
}
