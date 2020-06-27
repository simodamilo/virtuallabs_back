package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.AppUser;
import it.polito.ai.virtuallabs_back.entities.Token;
import it.polito.ai.virtuallabs_back.entities.UserToken;
import it.polito.ai.virtuallabs_back.repositories.TokenRepository;
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
    TokenRepository tokenRepository;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    TeamService teamService;

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
    public void notifyUser(AppUser appUser, String name, String surname) {
        String address = appUser.getUsername();
        String subject = "Account creation";
        UserToken token = UserToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(appUser.getId())
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
