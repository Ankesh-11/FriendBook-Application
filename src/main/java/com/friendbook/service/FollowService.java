package com.friendbook.service;

import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entities.UserModel;


import java.util.Optional;
import java.util.Set;

public interface FollowService {

    boolean isInMyFollowRequests(Integer currentUserId, Integer targetUserId);

    Set<UserDto> getFollowRequests(Integer userId) throws UserException;

    Set<UserDto> getFollowers(Integer userId) throws UserException;

    Set<UserDto> getFollowing(Integer userId) throws UserException;

    void sendFollowRequest(Integer toUserId, Integer id) throws UserException;

    void acceptFollowRequest(Integer id, Integer requesterId) throws UserException;

    void declineFollowRequest(Integer id, Integer requesterId) throws UserException;

    void followBack(Integer id, Integer requesterId) throws UserException;

    String unfollowUser(Integer reqUserId, Integer unfollowUserId) throws UserException;


    Object getFollowersCount(UserModel viewedUser);

    Object getFollowingCount(UserModel viewedUser);

    boolean isFollowing(Integer currentUSerId, Integer viewedUserId);

    boolean isFollower(Integer currentUSerId, Integer viewedUserId);

    boolean isRequests(Integer currentUserId, Integer viewedUserId);

    void cancelFollowRequest(Integer id, Integer sendToUserId) throws UserException;
}
