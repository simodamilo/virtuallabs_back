package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.AppUser;
import it.polito.ai.virtuallabs_back.entities.TeamToken;
import it.polito.ai.virtuallabs_back.entities.UserToken;
import it.polito.ai.virtuallabs_back.repositories.TeamTokenRepository;
import it.polito.ai.virtuallabs_back.repositories.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TeamTokenRepository teamTokenRepository;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    TeamService teamService;

    @Autowired
    UtilityService utilityService;

    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setTo(address);
        message.setTo("ApplicazioniInternet2020@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }


    @Override
    public void notifyTeam(TeamDTO teamDTO, List<String> studentSerials) {
        studentSerials.forEach(serial -> {
            if (utilityService.getStudent().getSerial().equals(serial)) {
                String address = serial + "@studenti.polito.it";
                String body = "Hello \r\n" +
                        "your team is correctly created, wait for other members";
                String subject = "Team creation";
                sendMessage(address, subject, body);
            } else {
                TeamToken teamToken = TeamToken.builder()
                        .id(UUID.randomUUID().toString())
                        .studentSerial(serial)
                        .teamId(teamDTO.getId())
                        .expiryDate(new Timestamp(System.currentTimeMillis() + teamDTO.getDuration() * 1000 * 60))
                        .status(0)
                        .build();
                teamTokenRepository.save(teamToken);
                String address = serial + "@studenti.polito.it";
                String body = "Hello \r\n" +
                        "a team has been created, confirm or refuse participation https://localhost:4200/home?doLogin=true";
                String subject = "Team confirmation";
                sendMessage(address, subject, body);
            }
        });
    }

    @Override
    public void notifyUser(AppUser appUser, String name, String surname) {
        String address = appUser.getUsername();
        String subject = "Account creation";
        UserToken token = UserToken.builder()
                .id(UUID.randomUUID().toString())
                .appUserId(appUser.getId())
                .name(name)
                .surname(surname)
                .expiryDate(new Timestamp(System.currentTimeMillis() + /*3600000*24*/120000))
                .build();
        userTokenRepository.save(token);
        String body = "Hello \r\n" +
                "an account has been created for you, to confirm click the following link: \r\n" +
                "Confirm : http://localhost:8080/confirm/" + token.getId() + " \r\n";
        //TODO sistemare email con link al login
        sendMessage(address, subject, body);
    }
}
