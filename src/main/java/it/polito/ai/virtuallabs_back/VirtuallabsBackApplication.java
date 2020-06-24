package it.polito.ai.virtuallabs_back;

import it.polito.ai.virtuallabs_back.entities.User;
import it.polito.ai.virtuallabs_back.repositories.UserRepository;
import it.polito.ai.virtuallabs_back.services.TeamService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
public class VirtuallabsBackApplication {
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder bCryptEncoder;

    @Bean
    CommandLineRunner runner(TeamService teamService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                User user = new User();
                user.setUsername("a");
                user.setPassword(bCryptEncoder.encode("aaa"));
                user.setRoles(Arrays.asList("ROLE_ADMIN"));
                userRepository.save(user);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(VirtuallabsBackApplication.class, args);
    }

}
