package com.friendbook.controller;

import com.friendbook.Exception.UserException;
import com.friendbook.entities.Comment;
import com.friendbook.entities.Post;
import com.friendbook.entities.UserModel;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.CommentService;
import com.friendbook.service.FollowService;
import com.friendbook.service.PostService;
import com.friendbook.dto.UserDto;
import com.friendbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.*;
@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private PostService postService;

    @Autowired
    private FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String homePagePost(HttpSession session, Model model) {
        UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            try {
                Set<UserDto> followers = followService.getFollowers(loggedInUser.getId());
                Set<UserDto> following = followService.getFollowing(loggedInUser.getId());

                Set<Integer> userIds = new HashSet<>();
                Set<Post> allPosts = new HashSet<>();
                Map<Integer, Boolean> likedPostsMap = new HashMap<>();

                try {
                    List<Post> posts = postService.findPostByUserId(loggedInUser.getId());
                    allPosts.addAll(posts);
                } catch (UserException e) {
                    System.out.println(e.getMessage());
                }

                for (UserDto follower : followers) {
                    if (!follower.getId().equals(loggedInUser.getId())) {
                        userIds.add(follower.getId());
                    }
                }
                for (UserDto followings : following) {
                    if (!followings.getId().equals(loggedInUser.getId())) {
                        userIds.add(followings.getId());
                    }
                }

                for (Integer userId : userIds) {
                    try {
                        List<Post> posts = postService.findPostByUserId(userId);
                        allPosts.addAll(posts);
                    } catch (UserException e) {
                        System.out.println(e.getMessage());
                    }
                }

                for (Post post : allPosts) {
                    boolean likedByCurrentUser = post.getLikedByUser().stream()
                            .anyMatch(user -> user.getId().equals(loggedInUser.getId()));
                    likedPostsMap.put(post.getId(), likedByCurrentUser);
                }

                Set<UserDto> requests = followService.getFollowRequests(loggedInUser.getId());
                model.addAttribute("allRequests", requests);
                model.addAttribute("user", loggedInUser);
                model.addAttribute("profileImage",loggedInUser.getImage());
                model.addAttribute("allPosts", allPosts);
                model.addAttribute("likedPostsMap", likedPostsMap);
                return "home";
            } catch (UserException e) {
                e.printStackTrace();
                model.addAttribute("error", e.getMessage());
                return "home";
            }
        }
        return "redirect:/signin";
    }

    @GetMapping("/search")
    public String searchPosts() {
        return "search";
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserModel>> searchUserHandler(@RequestParam("q") String query, Model model) {
        List<UserModel> users = null;
        try{
            users = userService.searchUser(query);
        } catch (UserException e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<List<UserModel>>(users, HttpStatus.OK);
    }

    @PutMapping("/posts/{postId}/like")
    public ResponseEntity<Post> likePost(@PathVariable Integer postId, @RequestParam("email") String email,HttpSession session) {
        try {
            UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
            Optional<UserModel> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                Post likedPost = postService.likePost(postId, user.get(),loggedInUser);
                return new ResponseEntity<>(likedPost, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/posts/{postId}/unlike")
    public ResponseEntity<Post> unlikePost(@PathVariable Integer postId, @RequestParam("email") String email) {
        try {
            Optional<UserModel> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                Post unlikedPost = postService.unlikePost(postId, user.get().getId());
                return new ResponseEntity<>(unlikedPost, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
