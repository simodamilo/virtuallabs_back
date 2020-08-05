package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.config.JwtTokenUtil;
import it.polito.ai.virtuallabs_back.dtos.JwtRequest;
import it.polito.ai.virtuallabs_back.dtos.JwtResponse;
import it.polito.ai.virtuallabs_back.dtos.RegistrationRequest;
import it.polito.ai.virtuallabs_back.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    @ResponseBody
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) throws Exception {
        System.out.println(authenticationRequest.getUsername() + " " + authenticationRequest.getPassword());
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.OK, reason = "User registered")
    public void registration(@RequestBody @Valid RegistrationRequest registrationRequest) {
        if (!userService.registration(registrationRequest))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "It is not possible to register");
    }

    @GetMapping("/confirm/{token}")
    public String confirmRegistration(@PathVariable String token) {
        if (userService.confirmRegistration(token)) return "confirmPage";
        else return "rejectPage";
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}