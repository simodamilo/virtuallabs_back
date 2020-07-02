package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.RegistrationRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    /**
     * Used for the authentication.
     *
     * @param username that needs to be compared.
     * @return if match the username return the userDetails.
     */
    UserDetails loadUserByUsername(String username);

    /**
     * Used for the first part of the registration, generate the user
     * with status set to false.
     * @param registrationRequest contains all the data needed for the registration.
     * @return true if the registration works correctly.
     */
    boolean registration(RegistrationRequest registrationRequest);

    /**
     * Used when the user confirm the registration by email, set the status
     * to true, and add the student/teacher.
     * @param token used to identify the user.
     * @return true if confirm the registration correctly.
     */
    boolean confirmRegistration(String token);

    /**
     * Periodically checks the expired userToken and
     * delete the relative user.
     */
    void clearUser();
}
