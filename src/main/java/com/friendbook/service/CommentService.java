package com.friendbook.service;


import com.friendbook.Exception.CommentException;
import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(Comment comment, Integer postId, Integer userId) throws PostException, UserException;

    Comment findCommentById(Integer commentId) throws CommentException;

	Comment likeComment(Integer commentId, Integer userId) throws CommentException, UserException;

	Comment unlikeComment(Integer commentId, Integer userId) throws CommentException, UserException;

    void deleteComment(Integer commentId, Integer currentUserId) throws PostException, UserException;

    List<Comment> getCommentsByPostId(int postId) throws CommentException, UserException, PostException;
}
