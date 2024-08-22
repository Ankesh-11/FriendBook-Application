package com.friendbook.service;


import com.friendbook.Exception.CommentException;
import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entities.Comment;

public interface CommentService {
    Comment createComment(Comment comment, Integer postId, Integer userId) throws PostException, UserException;

    Comment findCommentById(Integer commentId) throws CommentException;

	Comment likeComment(Integer commentId, Integer userId) throws CommentException, UserException;

	Comment unlikeComment(Integer commentId, Integer userId) throws CommentException, UserException;
}
