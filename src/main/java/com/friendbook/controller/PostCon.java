package com.friendbook.controller;

import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entities.Post;
import com.friendbook.entities.UserModel;
import com.friendbook.repository.UserRepository;
import com.friendbook.response.MessageResponse;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/posts")
public class PostCon {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/create")
    public String getCreatePostPage() {
        return "newPost";
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPostHandler(@RequestBody Post post, @RequestHeader("email") String email, Model model) throws UserException {
        Post createdPost = null;
        try {
            Optional<UserModel> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                createdPost = postService.createPost(post, user.get().getId());
            }
        } catch (UserException e) {
            model.addAttribute("error", e.getMessage());
            return new ResponseEntity<>(createdPost, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Post>> findPostByUserId(@PathVariable Integer userId) throws UserException {
        List<Post> posts = postService.findPostByUserId(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/following/{userIds}")
    public ResponseEntity<List<Post>> findAllPostByUserIdsHandler(@PathVariable List<Integer> userIds) throws PostException, UserException {
        List<Post> posts = postService.findAllPostByUserIds(userIds);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public String findPostByIdHandler(@PathVariable Integer postId, Model model) throws PostException {
        Post post = postService.findPostById(postId);
        model.addAttribute("post", post);
        return "home"; // Return the Thymeleaf template name
    }

//    @PostMapping("/{postId}/like")
//    public ResponseEntity<Map<String, Object>> likePostHandler(@PathVariable Integer postId, HttpSession session) {
//        try {
//            UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
//            Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
//
//            if (user.isPresent()) {
//                Post likedPost = postService.likePost(postId, user.get().getId());
//                Map<String, Object> response = new HashMap<>();
//                response.put("liked", true);
//                response.put("likeCount", likedPost.getLikedByUser().size());
//                response.put("postId", postId);
//                return ResponseEntity.ok(response);
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            }
//        } catch (PostException | UserException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
//        }
//    }
//
//    @PostMapping("/{postId}/unlike")
//    public ResponseEntity<Map<String, Object>> unlikePostHandler(@PathVariable Integer postId, HttpSession session) {
//        try {
//            UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
//            Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
//
//            if (user.isPresent()) {
//                Post unlikedPost = postService.unlikePost(postId, user.get().getId());
//                Map<String, Object> response = new HashMap<>();
//                response.put("liked", false);
//                response.put("likeCount", unlikedPost.getLikedByUser().size());
//                response.put("postId", postId);
//                return ResponseEntity.ok(response);
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            }
//        } catch (PostException | UserException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
//        }
//    }

//    @PostMapping("/{postId}/like")
//    public String likePostHandler(@PathVariable Integer postId, Model model, HttpSession session) throws PostException, UserException {
//        try
//        {
//            UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
//            Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
//            boolean like=false;
//            if (user.isPresent()) {
//                Post likedPost = postService.likePost(postId, user.get().getId());
//                like = postService.isLiked(postId,user.get());
//                model.addAttribute("liked",like);
//                model.addAttribute("post", likedPost);
//                model.addAttribute("likeCount", likedPost.getLikedByUser().size());
//                return "redirect:/home";
//            }else {
//                return "redirect:/signin";
//            }
//        } catch (PostException e) {
//            model.addAttribute("errorPost",e.getMessage());
//            return "home";
//        } catch (UserException e) {
//            model.addAttribute("errorUser",e.getMessage());
//            return "home";
//        }
//    }
//
//    @PostMapping("/{postId}/unlike")
//    public String unlikePostHandler(@PathVariable Integer postId, Model model,HttpSession session){
//        try{
//            UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
//            Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
//            boolean unlike=false;
//            if (user.isPresent()) {
//                Post unlikedPost = postService.unlikePost(postId, user.get().getId());
//                unlike = true;
//                model.addAttribute("unliked",unlike);
//                model.addAttribute("post", unlikedPost);
//                model.addAttribute("likeCount", unlikedPost.getLikedByUser().size());
//            }
//        } catch (PostException e) {
//            model.addAttribute("postError",e.getMessage());
//        } catch (UserException e) {
//            model.addAttribute("error",e.getMessage());
//        }
//        return "home";
//    }


    @GetMapping("/{postId}/likedUser")
    public ResponseEntity<?> getTotalLikes(@PathVariable Integer postId) {
        try {
            Post post = postService.findPostById(postId);
            Set<UserDto> likedUsers = post.getLikedByUser();

            List<UserDto> users = likedUsers.stream()
                    .map(user -> new UserDto(user.getId(), user.getUsername(), user.getImage(),user.getName())) // Assuming UserDto has these fields
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePostHandler(@PathVariable Integer postId, HttpSession session) throws PostException, UserException {
        UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
        Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
        if (user.isPresent()) {
            Post likedPost = postService.likePost(postId, user.get().getId());
            Map<String, Object> response = new HashMap<>();
            response.put("liked", true);
            response.put("posts", likedPost);
            response.put("likeCount", likedPost.getLikedByUser().size());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<Map<String, Object>> unlikePostHandler(@PathVariable Integer postId, HttpSession session) throws PostException, UserException {
        UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
        Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
        if (user.isPresent()) {
            Post unlikedPost = postService.unlikePost(postId, user.get().getId());
            Map<String, Object> response = new HashMap<>();
            response.put("liked", false);
            response.put("post", unlikedPost);
            response.put("likeCount", unlikedPost.getLikedByUser().size());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
