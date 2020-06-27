package it.polito.ai.virtuallabs_back;

import it.polito.ai.virtuallabs_back.services.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VirtuallabsBackApplication {
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    CommandLineRunner runner(StudentService studentService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) {
                System.out.println(studentService.getAllStudents());
                System.out.println(studentService.getStudent("s267333"));
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(VirtuallabsBackApplication.class, args);
    }

}
