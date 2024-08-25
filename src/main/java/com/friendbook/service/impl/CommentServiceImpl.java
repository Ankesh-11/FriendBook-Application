package com.friendbook.service.impl;

import com.friendbook.Exception.CommentException;
import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entities.Comment;
import com.friendbook.entities.Post;
import com.friendbook.entities.UserModel;
import com.friendbook.repository.CommentRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.service.CommentService;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;
import com.friendbook.dto.UserDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PostService postService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private NotificationService notificationService;

	@Override
	public Comment createComment(Comment comment, Integer postId, Integer userId) throws PostException, UserException {
		UserModel user = userService.findUserById(userId);
		Post post = postService.findPostById(postId);

		UserDto userDto = new UserDto(user.getId(),user.getUsername(),user.getEmail(), user.getName(), user.getImage(),user.getMobile(),user.getBio());
		comment.setUser(userDto);
		comment.setCreatedAt(LocalDateTime.now());

		Comment newComment = commentRepository.save(comment);
		post.getComments().add(newComment);
		postRepository.save(post);

		UserModel postUser = new UserModel();
		postUser.setId(post.getUser().getId());
		postUser.setEmail(post.getUser().getEmail());
		postUser.setName(post.getUser().getName());
		postUser.setUsername(post.getUser().getUsername());
		postUser.setImage(post.getUser().getImage());

//		notificationService.sendNotification(user, postUser, "commented on your post", post);

		return newComment;
	}

	@Override
	public Comment findCommentById(Integer commentId) throws CommentException {
		return commentRepository.findById(commentId)
				.orElseThrow(() -> new CommentException("Comment not exist with id: " + commentId));
	}



	@Override
	public Comment likeComment(Integer commentId, Integer userId) throws CommentException, UserException {
		UserModel user = userService.findUserById(userId);
		Comment comment = findCommentById(commentId);

		UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName(), user.getUsername(), user.getImage(), user.getMobile(), user.getBio());

		comment.getLikedByUser().add(userDto);

		Comment likedComment = commentRepository.save(comment);

		//notificationService.sendNotification(user, comment.getUser().toUserModel(), "liked your comment",);

		return likedComment;
	}

	@Override
	public Comment unlikeComment(Integer commentId, Integer userId) throws CommentException, UserException {
		UserModel user = userService.findUserById(userId);
		Comment comment = findCommentById(commentId);

		UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName(), user.getUsername(), user.getImage(), user.getMobile(), user.getBio());

		comment.getLikedByUser().remove(userDto);

		return commentRepository.save(comment);
	}

	@Override
	public List<Comment> getCommentsByPostId(int postId) throws CommentException, UserException, PostException {
		Post post = postService.findPostById(postId);
		List<Comment> comments = post.getComments();
		return comments;
	}

}
