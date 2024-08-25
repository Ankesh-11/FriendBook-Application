package com.friendbook.repository;


import com.friendbook.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteByPostId(Integer postId);
    List<Notification> findByPostId(Integer postId);
}
