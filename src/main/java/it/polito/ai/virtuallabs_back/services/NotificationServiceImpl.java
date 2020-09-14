package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.AppUser;
import it.polito.ai.virtuallabs_back.entities.UserToken;
import it.polito.ai.virtuallabs_back.repositories.TeamTokenRepository;
import it.polito.ai.virtuallabs_back.repositories.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
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
    @Async
    public void notifyTeam(TeamDTO teamDTO, List<String> studentSerials, String studentSerial) {
        Context context = new Context();
        context.setVariable("teamName", teamDTO.getName());
        studentSerials.forEach(serial -> {
            if (studentSerial.equals(serial)) {
                String address = serial + "@studenti.polito.it";
                String subject = "Team creation";
                String htmlContent = templateEngine.process("teamCreation.html", context);
                sendMessage(address, subject, htmlContent);
            } else {
                String address = serial + "@studenti.polito.it";
                String subject = "Team confirmation";
                String htmlContent = templateEngine.process("teamInvite.html", context);
                sendMessage(address, subject, htmlContent);
            }
        });
    }

    @Override
    @Async
    public void notifyUser(AppUser appUser, UserToken token) {
        String address = appUser.getUsername();
        String subject = "Account creation";

        Context context = new Context();
        context.setVariable("name", token.getName());
        context.setVariable("surname", token.getSurname());
        context.setVariable("token", token.getId());
        String htmlContent = templateEngine.process("user.html", context);

        sendMessage(address, subject, htmlContent);
    }

    public void sendMessage(String address, String subject, String body) {
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            //message.setTo(address);
            mimeMessageHelper.setTo("applicazioniinternet2020@gmail.com");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);
        } catch (MessagingException me) {
            me.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }
}
