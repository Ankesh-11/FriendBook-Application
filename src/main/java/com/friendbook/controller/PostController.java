//package com.friendbook.controller;
//
//import com.friendbook.Exception.PostException;
//import com.friendbook.Exception.UserException;
//import com.friendbook.entities.Post;
//import com.friendbook.entities.UserModel;
//import com.friendbook.repository.UserRepository;
//import com.friendbook.response.MessageResponse;
//import com.friendbook.service.PostService;
//import com.friendbook.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/api/posts")
//public class PostController {
//    @Autowired
//    private PostService postService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
////    @PostMapping("/create")
////    public ResponseEntity<Post> createPostHndler(@RequestBody Post post, @RequestHeader("Authorization") String token)
////            throws UserException {
////
////        UserModel user = userService.findUserProfile(token);
////        System.out.println(user.getId());
////        Post createdPost = postService.createPost(post, user.getId());
////
////        return new ResponseEntity<Post>(createdPost, HttpStatus.CREATED);
////    }
//
//    @GetMapping("/create")
//    public String getCreatePostPage(){
//
//        return "newPost";
//    }
//    @PostMapping("/create")
//    public ResponseEntity<Post> createPostHandler(@RequestBody Post post, @RequestHeader("email") String email,Model model) throws UserException {
//        Post createdPost=null;
//        try {
//            Optional<UserModel> user = userRepository.findByEmail(email);
//            if (user.isPresent()) {
//               createdPost = postService.createPost(post, user.get().getId());
//            }
//        }catch (UserException e){
//            model.addAttribute("error",e.getMessage());
//            return new ResponseEntity<>(createdPost, HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
//    }
//
//
//    @GetMapping("/all/{userId}")
//    public ResponseEntity<List<Post>> findPostByUserId(@PathVariable Integer userId) throws UserException {
//        List<Post> posts = postService.findPostByUserId(userId);
//        return new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
//    }
//
//    @GetMapping("/following/{userIds}")
//    public ResponseEntity<List<Post>> findAllPostByUserIdsHandler(@PathVariable List<Integer> userIds)
//            throws PostException, UserException {
//        List<Post> posts = postService.findAllPostByUserIds(userIds);
//        return new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
//    }
//
//    @GetMapping("/{postId}")
//    public ResponseEntity<Post> findPostByIdHandler(@PathVariable Integer postId) throws PostException {
//        Post post = postService.findPostById(postId);
//
//        return new ResponseEntity<Post>(post, HttpStatus.OK);
//    }
//
//    @PutMapping("/like/{postId}")
//    public ResponseEntity<?> likePostHandler(@PathVariable Integer postId, @RequestHeader("email") String email) throws PostException, UserException {
//        Optional<UserModel> user = userRepository.findByEmail(email);
//        if (user.isPresent()) {
//            Post likedPost = postService.likePost(postId, user.get().getId());
//            int totalLikes = likedPost.getLikedByUser().size();
//            return new ResponseEntity<>(Collections.singletonMap("totalLikes", totalLikes), HttpStatus.OK);
//        } else {
//            throw new UserException("User not found");
//        }
//    }
//
//
//    @PutMapping("/unlike/{postId}")
//    public ResponseEntity<Post> unlikePostHandler(@PathVariable Integer postId,
//                                                  @RequestHeader("Authorization") String token) throws PostException, UserException {
//
//        UserModel user = userService.findUserProfile(token);
//        Post unlikePost = postService.unlikePost(postId, user.getId());
//
//        return new ResponseEntity<Post>(unlikePost, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/delete/{postId}")
//    public ResponseEntity<MessageResponse> deletePostHandler(@PathVariable Integer postId,
//                                                             @RequestHeader("Authorization") String token) throws UserException, PostException {
//
//        UserModel user = userService.findUserProfile(token);
//        String message = postService.deletePost(postId, user.getId());
//
//        MessageResponse res = new MessageResponse(message);
//
//        return new ResponseEntity<MessageResponse>(res, HttpStatus.ACCEPTED);
//    }
//
//    @PutMapping("/save-post/{postId}")
//    public ResponseEntity<MessageResponse> savedPostHandler(@PathVariable Integer postId,
//                                                            @RequestHeader("Authorization") String token) throws PostException, UserException {
//        UserModel user = userService.findUserProfile(token);
//        String savedPost = postService.savePost(postId, user.getId());
//
//        MessageResponse res = new MessageResponse(savedPost);
//
//        return new ResponseEntity<MessageResponse>(res, HttpStatus.ACCEPTED);
//    }
//
//    @PutMapping("/unsave-post/{postId}")
//    public ResponseEntity<MessageResponse> unsavedPostHandler(@PathVariable Integer postId,
//                                                              @RequestHeader("Authorization") String token) throws UserException, PostException {
//        UserModel user = userService.findUserProfile(token);
//        String unsavedPost = postService.unsavePost(postId, user.getId());
//
//        MessageResponse res = new MessageResponse(unsavedPost);
//
//        return new ResponseEntity<MessageResponse>(res, HttpStatus.ACCEPTED);
//    }
//
//}
