package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.User;

import java.util.List;

public interface NotificationService {

    void sendMessage(String address, String subject, String body);

    boolean confirm(String token); // per confermare la partecipazione al gruppo

    boolean reject(String token); //per esprimere il proprio diniego a partecipare

    void notifyTeam(TeamDTO dto, List<String> memberIds);

    void notifyUser(User user, String pass);
}
