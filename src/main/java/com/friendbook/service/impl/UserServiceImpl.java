package com.friendbook.service.impl;

import com.friendbook.Exception.UserException;
import com.friendbook.entities.Post;
import com.friendbook.entities.UserModel;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.UserService;
import com.friendbook.dto.UserDto;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
	private static Set<Long> membershipNumber = new HashSet<>();

	@Autowired
	private UserRepository userRepository;

//	@Autowired
//	private PasswordEncoder passwordEncoder;

//	@Autowired
//	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private PostRepository postRepository;

	@Override
	public List<UserModel> findAllUser() throws UserException {
		List<UserModel> users = userRepository.findAll();
		if (users.size() == 0) {
			throw new UserException("user not exist");
		}
		return users;
	}

	@Override
	public UserModel registerUser(UserModel user) throws UserException {

		if (user.getEmail() == null || user.getName() == null || user.getPassword() == null) {
			throw new UserException("Email, Username and Password are required");
		}
		Optional<UserModel> isEmailExist = userRepository.findByEmail(user.getEmail());
		if (isEmailExist.isPresent()) {
			throw new UserException("Email Already Exist.");
		}
		user.setUsername(getUsername(user.getName()));
		Optional<UserModel> isUsernameExist = userRepository.findByUsername(user.getUsername());
		if (isUsernameExist.isPresent()) {
			throw new UserException("Username Already Taken");
		}

		//String encodedPassword = passwordEncoder.encode(user.getPassword());

		UserModel newUser = new UserModel();

		newUser.setEmail(user.getEmail());
		//newUser.setPassword(encodedPassword);
		newUser.setPassword(user.getPassword());
		newUser.setUsername(user.getUsername());
		newUser.setName(user.getName());

		return userRepository.save(newUser);
	}
	@Transactional
	@Override
	public UserModel findUserById(Integer userId) throws UserException {
		Optional<UserModel> optionalUser = userRepository.findById(userId);
		if (optionalUser.isPresent()) {
			return optionalUser.get();
		}
		throw new UserException("user not exist with id: " + userId);
	}
	@Override
	public UserModel findUserProfile(String token) throws UserException {
		// Bearer jsjsalssalskalsa
//		token = token.substring(7);
//
//		JwtTokenClaims jwtTokenClaims = jwtTokenProvider.getClaimsFromToken(token);
//
//		String email = jwtTokenClaims.getUsername();
//
//		Optional<UserModel> optionalUser = userRepository.findByEmail(email);
//
//		if (optionalUser.isPresent()) {
//			return optionalUser.get();
//		}
//
//		throw new UserException("Invalid Token...");
		return new UserModel();
	}

	@Override
	public UserModel findUserByUsername(String username) throws UserException {
		Optional<UserModel> optionalUser = userRepository.findByUsername(username);

		if (optionalUser.isPresent()) {
			UserModel user = optionalUser.get();
			return user;
		}
		throw new UserException("user not exist with username " + username);
	}

	@Override
	public String unfollowUser(Integer reqUserId, Integer unfollowUserId) throws UserException {
		return "";
	}

	@Override
	public List<UserModel> findUsersByUserIds(List<Integer> userIds) {
		List<UserModel> users = userRepository.findAllUserByUserIds(userIds);
		return users;
	}

	@Override
	public List<UserModel> searchUser(String query) throws UserException {
		List<UserModel> users = userRepository.findByQuery(query);
		if (users.isEmpty()) {
			throw new UserException("user not exist");
		}
		return users;
	}

	@Override
	public UserModel updateUserDetails(UserModel updatedUser, UserModel existingUser) throws UserException {

		if (updatedUser.getBio() != null) {
			existingUser.setBio(updatedUser.getBio());
		}

		if (updatedUser.getName() != null) {
			existingUser.setName(updatedUser.getName());
		}

		if (updatedUser.getMobile() != null) {
			existingUser.setMobile(updatedUser.getMobile());
		}

		if (updatedUser.getImage() != null) {
			existingUser.setImage(updatedUser.getImage());
		}
		return userRepository.save(existingUser);
	}

	private String getUsername(String name)
	{
		String[] nameParts = name.split(" ");
		String firstName = nameParts[0];
		Random random = new Random();
		int min = 100;
		int max = 99999;
		long randomNumber = random.nextInt(max - min + 1) + min;
		while (membershipNumber.contains(randomNumber))
		{
			randomNumber = random.nextInt(max - min + 1) + min;
		}
		membershipNumber.add(randomNumber);
		return firstName + randomNumber;
	}

}
