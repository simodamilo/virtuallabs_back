package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.Token;
import it.polito.ai.virtuallabs_back.entities.User;
import it.polito.ai.virtuallabs_back.repositories.TokenRepository;
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
    public JavaMailSender javaMailSender;

    @Autowired
    public TokenRepository tokenRepository;

    @Autowired
    public TeamService teamService;

    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setTo(address);
        message.setTo("gianmarcoliaci@hotmail.it");
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Override
    public boolean confirm(String token) {
        if (!tokenRepository.existsById(token)) return false;
        Token token1 = tokenRepository.getOne(token);
        if (token1.getExpiryDate()
                .before(new Timestamp(System.currentTimeMillis()))) return false;
        tokenRepository.delete(token1);
        if (tokenRepository.findAllByTeamId(token1.getTeamId()).isEmpty()) {
            teamService.enableTeam(token1.getTeamId());
            return true;
        }
        return false;
    }

    @Override
    public boolean reject(String token) {
        if (!tokenRepository.existsById(token)) return false;
        Token token1 = tokenRepository.getOne(token);
        if (token1.getExpiryDate()
                .before(new Timestamp(System.currentTimeMillis()))) return false;
        tokenRepository.findAllByTeamId(token1.getTeamId()).forEach(t -> tokenRepository.delete(t));
        teamService.evictTeam(token1.getTeamId());
        return true;
    }

    @Override
    public void notifyTeam(TeamDTO dto, List<String> memberIds) {
        for (String id :
                memberIds) {
            Token token = Token.builder()
                    .id(UUID.randomUUID().toString())
                    .teamId(dto.getId())
                    .expiryDate(new Timestamp(System.currentTimeMillis() + 3600000))
                    .build();
            tokenRepository.save(token);
            String address = id + "@studenti.polito.it";
            System.out.println(address + " team");
            String body = "Hello \r\n" +
                    "a team has been created, confirm or refuse participation via the following links. \r\n" +
                    "Confirm : http://localhost:8080/notification/confirm/" + token.getId() + " \r\n" +
                    "Reject : http://localhost:8080/notification/reject/" + token.getId();
            String subject = "Team confirmation";
            sendMessage(address, subject, body);
        }
    }

    @Override
    public void notifyUser(User user, String pass) {
        String address = user.getUsername() + "@studenti.polito.it";
        System.out.println(address + " user");
        String subject = "Account creation";
        String body = "Hello \r\n" +
                "an account has been created for you, to access the application use the following credentials. \r\n" +
                "Username: " + user.getUsername() + "\r\n" +
                "Password: " + pass;
        sendMessage(address, subject, body);
    }
}
