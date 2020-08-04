package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.AppUser;
import it.polito.ai.virtuallabs_back.entities.TeamToken;
import it.polito.ai.virtuallabs_back.entities.UserToken;
import it.polito.ai.virtuallabs_back.repositories.TeamTokenRepository;
import it.polito.ai.virtuallabs_back.repositories.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    TemplateEngine templateEngine;

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
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            //message.setTo(address);
            mimeMessageHelper.setTo("ApplicazioniInternet2020@gmail.com");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);
        } catch (MessagingException me) {
            me.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void notifyTeam(TeamDTO teamDTO, List<String> studentSerials) {
        Context context = new Context();
        context.setVariable("teamName", teamDTO.getName());
        studentSerials.forEach(serial -> {
            if (utilityService.getStudent().getSerial().equals(serial)) {
                String address = serial + "@studenti.polito.it";
                String subject = "Team creation";
                String htmlContent = templateEngine.process("teamCreation.html", context);
                sendMessage(address, subject, htmlContent);
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
                String subject = "Team confirmation";
                String htmlContent = templateEngine.process("teamInvite.html", context);
                sendMessage(address, subject, htmlContent);
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

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("surname", surname);
        context.setVariable("token", token.getId());
        String htmlContent = templateEngine.process("user.html", context);
        sendMessage(address, subject, htmlContent);
    }
}
