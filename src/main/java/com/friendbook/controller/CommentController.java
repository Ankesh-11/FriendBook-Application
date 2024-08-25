package com.friendbook.controller;


import com.friendbook.Exception.CommentException;
import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entities.Comment;
import com.friendbook.entities.UserModel;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.CommentService;
import com.friendbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

	private CommentService commentService;
	private UserService userService;
	private UserRepository userRepository;
	@PostMapping("/{postId}/comment")
	public ResponseEntity<String> addCommentToPost(
			@PathVariable Integer postId,
			@RequestBody Comment comment,
			HttpSession session) {
		try {
			UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
			Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());

			if (user.isPresent()) {
				commentService.createComment(comment, postId, user.get().getId());
				return ResponseEntity.ok("Comment added successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
			}
		} catch (PostException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding comment: " + e.getMessage());
		} catch (UserException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
		}
	}

	@GetMapping("getCommentsByPostId/{postId}")
	public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable int postId) {
		List<Comment> comments = null;
		try {
			comments = commentService.getCommentsByPostId(postId);
			return new ResponseEntity<>(comments, HttpStatus.OK);
		} catch (PostException | UserException | CommentException e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/like/{commentId}")
	public ResponseEntity<Comment> likeCommentHandler(@RequestHeader("Authorization") String token,
			@PathVariable Integer commentId) throws UserException, CommentException {

		UserModel user = userService.findUserProfile(token);
		Comment likeComment = commentService.likeComment(commentId, user.getId());

		return new ResponseEntity<Comment>(likeComment, HttpStatus.OK);
	}
	@PutMapping("/unlike/{commentId}")
	public ResponseEntity<Comment> unlikeCommentHandler(@PathVariable Integer commentId,
			@RequestHeader("Authorization") String token) throws UserException, CommentException {
		UserModel user = userService.findUserProfile(token);
		Comment unlikeComment = commentService.unlikeComment(commentId, user.getId());
		return new ResponseEntity<Comment>(unlikeComment, HttpStatus.OK);
	}

}
