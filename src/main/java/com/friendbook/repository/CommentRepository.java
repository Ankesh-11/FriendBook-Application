package com.friendbook.repository;


import com.friendbook.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    void deleteByPostId(Integer postId);
    List<Comment> findByPostId(Integer postId);
}
