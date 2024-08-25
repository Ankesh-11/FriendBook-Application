package com.friendbook.service.impl;

import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entities.Notification;
import com.friendbook.entities.UserModel;
import com.friendbook.service.FollowService;
import com.friendbook.repository.NotificationRepository;
import com.friendbook.repository.UserRepository;

import com.friendbook.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void sendFollowRequest(Integer toUserId, Integer fromUserId) throws UserException {
        UserModel fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserException("User not found"));
        UserModel toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserException("User not found"));

        UserDto fromUserDto = new UserDto(fromUser.getId(), fromUser.getUsername(), fromUser.getEmail(), fromUser.getName(), fromUser.getImage(),fromUser.getMobile(),fromUser.getBio());

        toUser.getFollowRequests().add(fromUserDto);

        Notification notification = new Notification();
        notification.setFromUser(fromUser);
        notification.setToUser(toUser);
        notification.setMessage(fromUser.getUsername() + " sent you a follow request.");
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        toUser.getNotifications().add(notification);

        userRepository.save(toUser);
    }

    @Override
    public void acceptFollowRequest(Integer userId, Integer requesterId) throws UserException {
        try{
            UserModel user = userService.findUserById(userId);
            UserModel requester = userService.findUserById(requesterId);

            UserDto fromUserDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getName(), user.getImage(),user.getMobile(),user.getBio());
            UserDto toUserDto = new UserDto(requester.getId(), requester.getUsername(), requester.getEmail(), requester.getName(), requester.getImage(),requester.getMobile(),requester.getBio());
            System.out.println("condition :"+user.getFollowRequests().stream().anyMatch(dto -> dto.getId().equals(requester.getId())));

            if (user.getFollowRequests().stream().anyMatch(dto -> dto.getId().equals(requester.getId()))) {

                user.getFollowRequests().removeIf(dto -> dto.getId().equals(toUserDto.getId()));
                user.getFollower().add(toUserDto);
                requester.getFollowing().add(fromUserDto);

                userRepository.save(user);
                userRepository.save(requester);
            }else {
                System.out.println("condition false");
            }
        }catch (Exception e){
            throw new UserException("Follow request not found");
        }
    }

    @Override
    public void declineFollowRequest(Integer userId, Integer requesterId) throws UserException {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        UserModel requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserException("User not found"));

        UserDto toUserDto = new UserDto(requester.getId(), requester.getUsername(), requester.getEmail(), requester.getName(), requester.getImage(),requester.getMobile(),requester.getBio());

        if (user.getFollowRequests().removeIf(dto -> dto.getId().equals(toUserDto.getId()))) {
            userRepository.save(user);
        } else {
            throw new UserException("Follow request not found");
        }
    }
    @Override
    public void cancelFollowRequest(Integer userId, Integer sendToUserId) throws UserException {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        UserModel requester = userRepository.findById(sendToUserId)
                .orElseThrow(() -> new UserException("User not found"));

        UserDto toUserDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getName(), user.getImage(),user.getMobile(),user.getBio());

        if (requester.getFollowRequests().removeIf(dto -> dto.getId().equals(toUserDto.getId()))) {
            userRepository.save(requester);
        } else {
            throw new UserException("Follow request not found");
        }
    }
    @Override
    public void followBack(Integer userId, Integer requesterId) throws UserException {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        UserModel requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserException("User not found"));
        UserDto fromUserDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getName(), user.getImage(),user.getMobile(),user.getBio());
        UserDto toUserDto = new UserDto(requester.getId(), requester.getUsername(), requester.getEmail(), requester.getName(), requester.getImage(),requester.getMobile(),requester.getBio());

        if (!user.getFollowing().stream().anyMatch(dto -> dto.getId().equals(requester.getId()))) {
            user.getFollowing().add(toUserDto);
            requester.getFollower().add(fromUserDto);

            Notification notification = new Notification();
            notification.setMessage("You are now following " + requester.getUsername());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setFromUser(user);
            Notification savedNotification = notificationRepository.save(notification);
            user.getNotifications().add(notification);

            userRepository.save(user);
            userRepository.save(requester);
        } else {
            throw new UserException("Already following");
        }
    }

    @Override
    public String unfollowUser(Integer reqUserId, Integer unfollowUserId) throws UserException {
        UserModel reqUser = userRepository.findById(reqUserId)
                .orElseThrow(() -> new UserException("User not found"));
        UserModel unfollowUser = userRepository.findById(unfollowUserId)
                .orElseThrow(() -> new UserException("User not found"));

        UserDto currUserDto = new UserDto(reqUser.getId(), reqUser.getUsername(), reqUser.getEmail(), reqUser.getName(), reqUser.getImage(), reqUser.getMobile(), reqUser.getBio());
        UserDto otherUserDto = new UserDto(unfollowUser.getId(), unfollowUser.getUsername(), unfollowUser.getEmail(), unfollowUser.getName(), unfollowUser.getImage(), unfollowUser.getMobile(), unfollowUser.getBio());

        if (reqUser.getFollowing().contains(otherUserDto)) {
            reqUser.getFollowing().remove(otherUserDto);
        } else {
            throw new UserException("You are not following this user");
        }

        if (unfollowUser.getFollower().contains(currUserDto)) {
            unfollowUser.getFollower().remove(currUserDto);
        } else {
            throw new UserException("User is not in followers list");
        }
        userRepository.save(reqUser);
        userRepository.save(unfollowUser);
        return "You have unfollowed " + unfollowUser.getUsername();
    }

    @Override
    public Object getFollowersCount(UserModel viewedUser) {
        return viewedUser.getFollower().size();
    }

    @Override
    public Object getFollowingCount(UserModel viewedUser) {
        return viewedUser.getFollowing().size();
    }

    @Override
    public boolean isFollowing(Integer currentUserId, Integer viewedUserId) {
        UserModel currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null || currentUser.getFollowing() == null) {
            return false;
        }

        return currentUser.getFollowing().stream()
                .anyMatch(userDto -> userDto.getId().equals(viewedUserId));
    }

    @Override
    public boolean isFollower(Integer currentUserId, Integer viewedUserId) {
        UserModel currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null || currentUser.getFollowing() == null) {
            return false;
        }

        return currentUser.getFollower().stream()
                .anyMatch(userDto -> userDto.getId().equals(viewedUserId));
    }

    @Override
    public boolean isRequests(Integer currentUserId, Integer targetUserId) {

        UserModel targetUser = userRepository.findById(targetUserId).orElse(null);
        if (targetUser == null || targetUser.getFollowRequests() == null) {
            return false;
        }
        return targetUser.getFollowRequests().stream()
                .anyMatch(request -> request.getId().equals(currentUserId));
    }


    @Override
    public boolean isInMyFollowRequests(Integer currentUserId, Integer targetUserId) {
        UserModel currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null || currentUser.getFollowRequests() == null) {
            return false;
        }
        return currentUser.getFollowRequests().stream()
                .anyMatch(request -> request.getId().equals(targetUserId));
    }

    @Override
    public Set<UserDto> getFollowRequests(Integer userId) throws UserException {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        return user.getFollowRequests();
    }

    @Override
    public Set<UserDto> getFollowers(Integer userId) throws UserException {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        return user.getFollower();
    }

    @Override
    public Set<UserDto> getFollowing(Integer userId) throws UserException {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        return user.getFollowing();
    }
}
