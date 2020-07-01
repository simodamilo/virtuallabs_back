package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.AppUser;

import java.util.List;

public interface NotificationService {

    void sendMessage(String address, String subject, String body);

    void notifyTeam(TeamDTO dto, List<String> memberIds);

    void notifyUser(AppUser appUser, String name, String surname);
}
