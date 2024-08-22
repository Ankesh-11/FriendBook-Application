//package com.friendbook.controller;
//
//import com.friendbook.Exception.UserException;
//import com.friendbook.entities.Post;
//import com.friendbook.entities.UserModel;
//import com.friendbook.repository.UserRepository;
//import com.friendbook.service.FollowService;
//import com.friendbook.response.MessageResponse;
//import com.friendbook.service.PostService;
//import com.friendbook.service.UserService;
//import com.friendbook.dto.UserDto;
//
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.util.*;
//
//
//@Controller
//@RequestMapping("/api/users")
//public class UserController {
//
//	@Autowired
//	private UserService userService;
//
//	@Autowired
//	private FollowService followService;
//
//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private PostService postService;
//
//	@GetMapping("/all")
//	public ResponseEntity<List<UserModel>> findAllUserHandler() throws UserException {
//		List<UserModel> users = userService.findAllUser();
//		return new ResponseEntity<List<UserModel>>(users, HttpStatus.OK);
//	}
//
//	@GetMapping("id/{id}")
//	public ResponseEntity<UserModel> findUserByIdHandler(@PathVariable Integer id) throws UserException {
//		UserModel user = userService.findUserById(id);
//
//		return new ResponseEntity<UserModel>(user, HttpStatus.OK);
//	}
//
//	@GetMapping("username/{username}")
//	public ResponseEntity<UserModel> findByUsernameHandler(@PathVariable("username") String username)
//			throws UserException {
//		UserModel user = userService.findUserByUsername(username);
//
//		return new ResponseEntity<UserModel>(user, HttpStatus.ACCEPTED);
//	}
//
//	@GetMapping("/m/{userIds}")
//	public ResponseEntity<List<UserModel>> findAllUserByUserIdsHandler(@PathVariable List<Integer> userIds) {
//		List<UserModel> users = userService.findUsersByUserIds(userIds);
//
//		return new ResponseEntity<List<UserModel>>(users, HttpStatus.ACCEPTED);
//	}
//
//	@GetMapping("/search")
//	public ResponseEntity<List<UserModel>> searchUserHandler(@RequestParam("q") String query) {
//		List<UserModel> users = null;
//		try{
//			users = userService.searchUser(query);
//		} catch (UserException e) {
//			System.out.println(e.getMessage());
//        }
//		return new ResponseEntity<List<UserModel>>(users, HttpStatus.OK);
//    }
//	//retrieve current logged in user
//	@GetMapping("/req")
//	public ResponseEntity<UserModel> findUserProfileHandler(@RequestHeader("Authorization") String token)
//			throws UserException {
//
//		UserModel user = userService.findUserProfile(token);
//		return new ResponseEntity<UserModel>(user, HttpStatus.ACCEPTED);
//	}
//
//	@PutMapping("/account/edit")
//	public ResponseEntity<UserModel> updateUserHandler(@RequestBody UserModel user,
//													   @RequestHeader("Authorization") String token) throws UserException {
//
//		UserModel reqUser = userService.findUserProfile(token);
//		UserModel updatedUser = userService.updateUserDetails(user, reqUser);
//
//		return new ResponseEntity<UserModel>(updatedUser, HttpStatus.OK);
//	}
//	@PostMapping("/sendRequest/{toUserId}")
//	public ResponseEntity<String> sendFollowRequest(@PathVariable Integer toUserId,@RequestHeader("Authorization") String token) {
//		try {
//			UserModel user = userService.findUserProfile(token);
//			followService.sendFollowRequest(toUserId,user.getId());
//			return ResponseEntity.ok("Follow request sent");
//		} catch (UserException e) {
//			return ResponseEntity.badRequest().body(e.getMessage());
//		}
//	}
//
//	@PostMapping("/accept/{requesterId}")
//	public ResponseEntity<String> acceptFollowRequest(@RequestHeader("Authorization") String token, @PathVariable Integer requesterId) {
//		try {
//			UserModel user = userService.findUserProfile(token);
//			followService.acceptFollowRequest(user.getId(), requesterId);
//			return ResponseEntity.ok("Follow request accepted");
//		} catch (UserException e) {
//			return ResponseEntity.badRequest().body(e.getMessage());
//		}
//	}
//
//	@PostMapping("/decline/{requesterId}")
//	public ResponseEntity<String> declineFollowRequest( @RequestHeader("Authorization") String token,@PathVariable Integer requesterId) {
//		try {
//			UserModel user = userService.findUserProfile(token);
//			followService.declineFollowRequest(user.getId(), requesterId);
//			return ResponseEntity.ok("Follow request declined");
//		} catch (UserException e) {
//			return ResponseEntity.badRequest().body(e.getMessage());
//		}
//	}
//
//	@PostMapping("/followback/{requesterId}")
//	public ResponseEntity<String> followBack( @RequestHeader("Authorization") String token, @PathVariable Integer requesterId) {
//		try {
//			UserModel user = userService.findUserProfile(token);
//			followService.followBack(user.getId(), requesterId);
//			return ResponseEntity.ok("Followed back");
//		} catch (UserException e) {
//			return ResponseEntity.badRequest().body(e.getMessage());
//		}
//	}
//
//	@GetMapping("/allRequests")
//	public ResponseEntity<Set<UserDto>> getFollowRequests(@RequestHeader("Authorization") String token) {
//		try {
//			UserModel user = userService.findUserProfile(token);
//			Set<UserDto> requests = followService.getFollowRequests(user.getId());
//			return ResponseEntity.ok(requests);
//		} catch (UserException e) {
//			return ResponseEntity.badRequest().build();
//		}
//	}
//
//	@PutMapping("/unfollow/{unfollowUserId}")
//	public ResponseEntity<MessageResponse> unfollowUserHandler(@PathVariable Integer unfollowUserId,
//															   @RequestHeader("Authorization") String token) throws UserException {
//
//		UserModel user = userService.findUserProfile(token);
//		String message = followService.unfollowUser(user.getId(), unfollowUserId);
//		MessageResponse res = new MessageResponse(message);
//
//		return new ResponseEntity<MessageResponse>(res, HttpStatus.OK);
//	}
//
//	@GetMapping("/followers")
//	public ResponseEntity<Set<UserDto>> getFollowers(@RequestHeader("Authorization") String token) {
//		try {
//			UserModel user = userService.findUserProfile(token);
//			Set<UserDto> followers = followService.getFollowers(user.getId());
//			return ResponseEntity.ok(followers);
//		} catch (UserException e) {
//			return ResponseEntity.badRequest().body(Collections.emptySet());
//		}
//	}
//
//	@GetMapping("/following")
//	public ResponseEntity<Set<UserDto>> getFollowing(@RequestHeader("Authorization") String token) {
//		try {
//			UserModel user = userService.findUserProfile(token);
//			Set<UserDto> following = followService.getFollowing(user.getId());
//			return ResponseEntity.ok(following);
//		} catch (UserException e) {
//			return ResponseEntity.badRequest().body(Collections.emptySet());
//		}
//	}
//	@GetMapping("/viewProfile/{username}")
//	public String viewProfile(@PathVariable String username, Model model, HttpSession session) {
//		try {
//			UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
//			UserModel viewedUser = userService.findUserByUsername(username);
//
//			if (viewedUser == null) {
//				return "redirect:/home";
//			}
//			if (currentUser != null) {
//				boolean isFollowing = followService.isFollowing(currentUser.getId(), viewedUser.getId());
//				boolean isFollower = followService.isFollower(currentUser.getId(), viewedUser.getId());
//				boolean isInMyFollowRequests = followService.isInMyFollowRequests(currentUser.getId(), viewedUser.getId());
//				boolean isRequests = followService.isRequests(currentUser.getId(), viewedUser.getId());
//
//				List<Post> posts = new ArrayList<>();
//				if (isFollowing || isFollower) {
//					posts = postService.findPostByUserId(viewedUser.getId());
//				}
//				model.addAttribute("user", viewedUser);
//				model.addAttribute("followersCount", followService.getFollowersCount(viewedUser));
//				model.addAttribute("followingCount", followService.getFollowingCount(viewedUser));
//				model.addAttribute("postCount", postService.getPostCountByUser(viewedUser));
//				model.addAttribute("posts", posts);
//				model.addAttribute("isFollowing", isFollowing);
//				model.addAttribute("isFollower", isFollower);
//				model.addAttribute("isInFollowRequests", isInMyFollowRequests);
//				model.addAttribute("isRequested", isRequests);
//				model.addAttribute("followers", followService.getFollowers(viewedUser.getId()));
//				model.addAttribute("followings", followService.getFollowing(viewedUser.getId()));
//
//				return "viewProfile";
//			}
//		} catch (UserException e) {
//			model.addAttribute("error", e.getMessage());
//			return "viewProfile";
//		}
//		return "redirect:/signin";  // Handle the case where the user is not logged in or viewedUser is null
//	}
//
//
//}
