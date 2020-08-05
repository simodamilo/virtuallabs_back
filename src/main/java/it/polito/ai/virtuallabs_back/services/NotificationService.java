package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.AppUser;
import it.polito.ai.virtuallabs_back.entities.UserToken;

import java.util.List;

public interface NotificationService {

    /**
     * Used to generate teamToken for the members and
     * to send notification email to everyone.
     *
     * @param teamDTO       to identify the team.
     * @param studentSerial list of the members of the team.
     */
    void notifyTeam(TeamDTO teamDTO, List<String> studentSerials, String studentSerial);

    /**
     * Used to generate userToken and to notify the
     * user of the account creation.
     *
     * @param appUser contains detail of the user fot the registration.
     * @param token   of the user.
     */
    void notifyUser(AppUser appUser, UserToken token);
}
