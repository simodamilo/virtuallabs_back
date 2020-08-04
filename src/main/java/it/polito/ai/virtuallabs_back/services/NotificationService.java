package it.polito.ai.virtuallabs_back.services;

import it.polito.ai.virtuallabs_back.dtos.TeamDTO;
import it.polito.ai.virtuallabs_back.entities.AppUser;

import javax.mail.MessagingException;
import java.util.List;

public interface NotificationService {

    /**
     * Used to send email.
     *
     * @param address where to send the email.
     * @param subject of the email.
     * @param body    of the email.
     */
    void sendMessage(String address, String subject, String body) throws MessagingException;

    /**
     * Used to generate teamToken for the members and
     * to send notification email to everyone.
     *
     * @param teamDTO       to identify the team.
     * @param studentSerial list of the members of the team.
     */
    void notifyTeam(TeamDTO teamDTO, List<String> studentSerial);

    /**
     * Used to generate userToken and to notify the
     * user of the account creation.
     *
     * @param appUser contains detail of the user fot the registration.
     * @param name    of the user.
     * @param surname of the user.
     */
    void notifyUser(AppUser appUser, String name, String surname);
}
