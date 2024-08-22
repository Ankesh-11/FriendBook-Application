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

import java.util.Optional;

@Controller
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

	private CommentService commentService;
	private UserService userService;
	private UserRepository userRepository;
	@PostMapping("/{postId}/comment")
	public String addCommentToPost(
			@PathVariable Integer postId,
			@ModelAttribute Comment comment,
			Model model,
			HttpSession session) throws PostException, UserException {
		try {
			UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
			Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());

			if (user.isPresent()) {
				commentService.createComment(comment, postId, user.get().getId());
			}
		} catch (PostException e) {
			model.addAttribute("postError", e.getMessage());
		} catch (UserException e) {
			model.addAttribute("error", e.getMessage());
		}
		return "redirect:/home";
	}

	//	@PostMapping("/create/{postId}")
//	public ResponseEntity<Comment> createCommentHandler(@RequestBody Comment comment, @PathVariable Integer postId,
//														@RequestParam String email) throws UserException, PostException {
//		Optional<UserModel> user = userRepository.findByEmail(email);
//		Comment createdComment = commentService.createComment(comment, postId, user.get().getId());
//		return new ResponseEntity<Comment>(createdComment, HttpStatus.CREATED);
//	}
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
