package it.polito.ai.virtuallabs_back.controllers;

import it.polito.ai.virtuallabs_back.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{token}")
    public String confirm(@PathVariable String token) {
        if (notificationService.confirm(token)) return "ConfirmPageTrue";
        else return "ConfirmPageFalse";
    }

    @GetMapping("/reject/{token}")
    public String reject(@PathVariable String token) {
        if (notificationService.reject(token)) return "rejectPageTrue";
        return "rejectPageFalse";
    }
}
