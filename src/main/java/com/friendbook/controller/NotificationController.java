package com.friendbook.controller;


import com.friendbook.Exception.UserException;
import com.friendbook.dto.NotificationDTO;
import com.friendbook.entities.Notification;
import com.friendbook.entities.UserModel;
import com.friendbook.service.UserService;
import com.friendbook.service.impl.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(HttpSession session){
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            List<NotificationDTO> notifications = notificationService.getNotifications(currentUser.getId());
            return ResponseEntity.ok(notifications);
        } catch (UserException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accept/{requesterId}")
    public ResponseEntity<String> acceptFollowRequest(@RequestHeader("Authorization") String token, @PathVariable Integer requesterId) {
        try {
            UserModel user = userService.findUserProfile(token);
            notificationService.acceptFollowRequest(user.getId(), requesterId);
            return ResponseEntity.ok("Follow request accepted");
        } catch (UserException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/decline/{userId}/{requesterId}")
    public ResponseEntity<String> declineFollowRequest(@PathVariable Integer userId, @PathVariable Integer requesterId) {
        try {
            notificationService.declineFollowRequest(userId, requesterId);
            return ResponseEntity.ok("Follow request declined");
        } catch (UserException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
