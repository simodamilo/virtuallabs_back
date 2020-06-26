package it.polito.ai.virtuallabs_back;

import it.polito.ai.virtuallabs_back.services.TeamService;
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
    CommandLineRunner runner(TeamService teamService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                /*AppUser appUser = new AppUser();
                appUser.setUsername("a");
                appUser.setPassword(bCryptEncoder.encode("aaa"));
                appUser.setRoles(Arrays.asList("ROLE_TEACHER"));
                userRepository.save(appUser);*/
                /*Teacher teacher = new Teacher();
                teacher.setName("Ahi");
                List<String> roles = new ArrayList<>();
                roles.add("ROLE_TEACHER");
                AppUser appUser = userService.addUser(roles);
                teacher.setSerial(appUser.getUsername());
                teacherRepository.save(modelMapper.map(teacher, Teacher.class));*/
                /*TeacherDTO teacherDTO = new TeacherDTO();
                teacherDTO.setName("Antonio");
                teacherService.addTeacher(teacherDTO);*/
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(VirtuallabsBackApplication.class, args);
    }

}
