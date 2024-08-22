package com.friendbook.service;



import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entities.UserModel;

import java.util.List;
import java.util.Set;

public interface UserService {

	public UserModel registerUser(UserModel user) throws UserException;

	public List<UserModel> findAllUser() throws UserException;

	public UserModel findUserById(Integer userId) throws UserException;

	public UserModel findUserProfile(String token) throws UserException;

	public UserModel findUserByUsername(String username) throws UserException;

	//public String followUser(Integer reqUserId, Integer followUserId) throws UserException;

	public String unfollowUser(Integer reqUserId, Integer unfollowUserId) throws UserException;

	public List<UserModel> findUsersByUserIds(List<Integer> userIds);

	public List<UserModel> searchUser(String query) throws UserException;

	public UserModel updateUserDetails(UserModel updatedUser, UserModel existingUser) throws UserException;
}
