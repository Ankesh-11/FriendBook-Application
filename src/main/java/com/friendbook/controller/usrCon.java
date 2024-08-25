package com.friendbook.controller;

import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entities.Post;
import com.friendbook.entities.UserModel;
import com.friendbook.repository.UserRepository;
import com.friendbook.response.MessageResponse;
import com.friendbook.service.FollowService;
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


@Controller
@RequestMapping("/api/users")
public class usrCon {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @GetMapping("/all")
    public ResponseEntity<List<UserModel>> findAllUserHandler() throws UserException {
        List<UserModel> users = userService.findAllUser();
        return new ResponseEntity<List<UserModel>>(users, HttpStatus.OK);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<UserModel> findUserByIdHandler(@PathVariable Integer id) throws UserException {
        UserModel user = userService.findUserById(id);

        return new ResponseEntity<UserModel>(user, HttpStatus.OK);
    }

    @GetMapping("username/{username}")
    public ResponseEntity<UserModel> findByUsernameHandler(@PathVariable("username") String username)
            throws UserException {
        UserModel user = userService.findUserByUsername(username);

        return new ResponseEntity<UserModel>(user, HttpStatus.ACCEPTED);
    }

    @GetMapping("/m/{userIds}")
    public ResponseEntity<List<UserModel>> findAllUserByUserIdsHandler(@PathVariable List<Integer> userIds) {
        List<UserModel> users = userService.findUsersByUserIds(userIds);

        return new ResponseEntity<List<UserModel>>(users, HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserModel>> searchUserHandler(@RequestParam("q") String query) {
        List<UserModel> users = null;
        try{
            users = userService.searchUser(query);
        } catch (UserException e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<List<UserModel>>(users, HttpStatus.OK);
    }

    @GetMapping("/updateProfile")
    public String updateProfile(HttpSession session, Model model) {
        UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
        model.addAttribute("user", currentUser);
        return "editProfile";
    }

    @PostMapping("/updateProfile")
    public String updateUserHandler(@ModelAttribute UserModel updatedUser, HttpSession session) throws UserException {
        UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
        UserModel userWithUpdates = userService.updateUserDetails(updatedUser,currentUser);
        session.setAttribute("loggedInUser", userWithUpdates);
        return "redirect:/api/users/profile"; // Redirect to the profile page after update
    }

    @PostMapping("/sendRequest/{toUserId}")
    public String sendFollowRequest(@PathVariable Integer toUserId, HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            followService.sendFollowRequest(toUserId, currentUser.getId());
            UserModel targetUser = userService.findUserById(toUserId);
            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();

        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @PostMapping("/accept/{requesterId}")
    public String acceptFollowRequest(@PathVariable Integer requesterId,HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            UserModel requesterUser = userService.findUserById(requesterId);
            if (currentUser == null) {
                return "redirect:/signin";
            }
            followService.acceptFollowRequest(currentUser.getId(), requesterId);
            UserModel targetUser = userService.findUserById(requesterId);
            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @PostMapping("/decline/{requesterId}")
    public String declineFollowRequest(@PathVariable Integer requesterId,HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            followService.declineFollowRequest(currentUser.getId(), requesterId);
            UserModel targetUser = userService.findUserById(requesterId);

            boolean isFollowing = followService.isFollowing(currentUser.getId(), targetUser.getId());
            boolean isFollower = followService.isFollower(currentUser.getId(), targetUser.getId());
            boolean isInMyFollowRequests = followService.isInMyFollowRequests(currentUser.getId(),targetUser.getId());
            boolean isRequests = followService.isRequests(currentUser.getId(),targetUser.getId());
            List<Post> posts = new ArrayList<>();
            if (isFollowing || isFollower) {
                posts = postService.findPostByUserId(requesterId);
            }
            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }
    @PostMapping("/cancelRequest/{userId}")
    public String cancelRequest(@PathVariable Integer userId,HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            followService.cancelFollowRequest(currentUser.getId(), userId);
            UserModel targetUser = userService.findUserById(userId);
            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @PostMapping("/followback/{requesterId}")
    public String followBack( @PathVariable Integer requesterId,HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";			}
            followService.followBack(currentUser.getId(), requesterId);
            UserModel targetUser = userService.findUserById(requesterId);

            boolean isFollowing = followService.isFollowing(currentUser.getId(), requesterId);
            boolean isFollower = followService.isFollower(currentUser.getId(), requesterId);
            boolean isInMyFollowRequests = followService.isInMyFollowRequests(currentUser.getId(),requesterId);
            boolean isRequests = followService.isRequests(currentUser.getId(),requesterId);

            model.addAttribute("isFollowing", isFollowing);
            model.addAttribute("isFollower", isFollower);
            model.addAttribute("isInFollowRequests", isInMyFollowRequests);
            model.addAttribute("isRequested", isRequests);
            model.addAttribute("user", targetUser);
            System.out.println(targetUser.getUsername());
            return "viewProfile";
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @GetMapping("/allRequests")
    public String getFollowRequests(HttpSession session,Model model) {
        UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
        try {
            if(currentUser== null){
                return "redirect:/signin";
            }
            Set<UserDto> requests = followService.getFollowRequests(currentUser.getId());
            model.addAttribute("allRequest",requests);
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
        }
        return "redirect:/api/users/viewProfile/"+currentUser.getUsername();
    }

    @PostMapping("/unfollow/{unfollowUserId}")
    public String unfollowUserHandler(@PathVariable Integer unfollowUserId,
                                                               HttpSession session) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            UserModel unfollwUser = userService.findUserById(unfollowUserId);
            String message = followService.unfollowUser(currentUser.getId(), unfollowUserId);
            return "redirect:/api/users/viewProfile/"+unfollwUser.getUsername();
        } catch (UserException e) {
            return "viewProfile";
        }
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Set<UserDto>> viewFollowers(@PathVariable Integer userId) {
        try {
            Set<UserDto> followers = followService.getFollowers(userId);
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<Set<UserDto>> viewFollowing(@PathVariable Integer userId) {
        try {
            Set<UserDto> following = followService.getFollowing(userId);
            return new ResponseEntity<>(following, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/viewProfile/{username}")
    public String viewProfile(@PathVariable String username, Model model,HttpSession session) {
        try{
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            UserModel viewedUser = userService.findUserByUsername(username);
            Map<Integer, Boolean> likedPostsMap = new HashMap<>();

            if(Objects.equals(currentUser.getUsername(), username))
            {
                return "redirect:/api/users/profile";
            }

            if (viewedUser == null || currentUser == null) {
                return "redirect:/home";
            }
            if(currentUser != null){

                boolean isFollowing = followService.isFollowing(currentUser.getId(), viewedUser.getId());
                boolean isFollower = followService.isFollower(currentUser.getId(), viewedUser.getId());
                boolean isInMyFollowRequests = followService.isInMyFollowRequests(currentUser.getId(),viewedUser.getId());
                boolean isRequests = followService.isRequests(currentUser.getId(),viewedUser.getId());

                List<Post> posts = new ArrayList<>();
                if (isFollowing || isFollower) {
                    try {
                        posts = postService.findPostByUserId(viewedUser.getId());
                        model.addAttribute("postCount", postService.getPostCountByUser(viewedUser));
                    } catch (UserException e) {
                        model.addAttribute("postError",e.getMessage());
                        model.addAttribute("postCount",0);
                    }
                }
                Set<UserDto> requests = followService.getFollowRequests(currentUser.getId());
                model.addAttribute("allRequests", requests);
                System.out.println(viewedUser.getUsername());
                model.addAttribute("user", viewedUser);
                model.addAttribute("currUser", currentUser);
                model.addAttribute("username",viewedUser.getUsername());
                model.addAttribute("profileImage",viewedUser.getImage());
                model.addAttribute("followersCount", followService.getFollowersCount(viewedUser));
                model.addAttribute("followingCount", followService.getFollowingCount(viewedUser));
                model.addAttribute("posts", posts);
                model.addAttribute("isFollowing", isFollowing);
                model.addAttribute("isFollower", isFollower);
                model.addAttribute("isInFollowRequests", isInMyFollowRequests);
                model.addAttribute("isRequested", isRequests);
                model.addAttribute("followers",followService.getFollowers(viewedUser.getId()));
                model.addAttribute("followings",followService.getFollowing(viewedUser.getId()));
                return "viewProfile";
            }
        } catch (UserException e) {
            System.out.println(e.getMessage());
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
        return "redirect:/search";
    }

    @GetMapping("/profile")
    public String currentUserProfile( Model model,HttpSession session) {
        try{
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");

            if (currentUser == null) {
                return "redirect:/signin";
            }
            else {
                List<Post> posts = new ArrayList<>();
                try{
                    posts = postService.findPostByUserId(currentUser.getId());
                } catch (UserException e) {
                    model.addAttribute("error",e.getMessage());
                }
                Set<UserDto> requests = followService.getFollowRequests(currentUser.getId());
                model.addAttribute("allRequests", requests);
                model.addAttribute("user", currentUser);
                model.addAttribute("post", posts);
                model.addAttribute("followers",followService.getFollowers(currentUser.getId()));
                model.addAttribute("followings",followService.getFollowing(currentUser.getId()));
                return "profile";
            }
        } catch (UserException e) {
            System.out.println(e.getMessage());
            model.addAttribute("error",e.getMessage());
            return "profile";
        }
    }

}
