package com.friendbook.service.impl;


import com.friendbook.Exception.UserException;
import com.friendbook.dto.NotificationDTO;
import com.friendbook.entities.Notification;
import com.friendbook.entities.Post;
import com.friendbook.entities.UserModel;
import com.friendbook.repository.NotificationRepository;
import com.friendbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    public List<NotificationDTO> getNotifications(Integer userId) throws UserException {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
        List<Notification> notifications = user.getNotifications();

        return notifications.stream()
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getFromUser().getUsername(),
                        notification.getToUser().getUsername(),
                        notification.getMessage(),
                        notification.getFromUser().getImage(),
                        notification.getCreatedAt(),
                        notification.getPost()
                ))
                .collect(Collectors.toList());
    }

    public void acceptFollowRequest(Integer userId, Integer requesterId) throws UserException {
        FollowServiceImpl followService = new FollowServiceImpl();
        followService.acceptFollowRequest(userId, requesterId);
    }

    public void declineFollowRequest(Integer userId, Integer requesterId) throws UserException {
        FollowServiceImpl  followService = new FollowServiceImpl ();
        followService.declineFollowRequest(userId, requesterId);
    }
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(UserModel fromUser, UserModel toUser, String message, Post post) {
        Notification notification = new Notification();
        notification.setFromUser(fromUser);
        notification.setToUser(toUser);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setPost(post);
        notificationRepository.save(notification);

    }
}
