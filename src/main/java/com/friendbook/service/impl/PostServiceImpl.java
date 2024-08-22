package com.friendbook.service.impl;

import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entities.Post;
import com.friendbook.entities.UserModel;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.hibernate.query.results.PositionalSelectionsNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

//	@Override
//	public Post createPost(Post post, Integer userId) throws UserException {
//		UserModel user = userRepository.findById(userId)
//				.orElseThrow(() -> new UserException("User not found"));
//
//		Post newPost = new Post();
//		newPost.setCaption(post.getCaption());
//		newPost.setImagePost(post.getImagePost());
//		newPost.setLocation(post.getLocation());
//		newPost.setCreatedAt(LocalDateTime.now());
//
//		UserDto userDto = new UserDto();
//		userDto.setEmail(user.getEmail());
//		userDto.setId(user.getId());
//		userDto.setName(user.getName());
//		userDto.setUsername(user.getUsername());
//
//		newPost.setUser(userDto);
//
//		// Save the new post
//		Post savedPost = postRepository.save(newPost);
//		// Optionally update user's posts
//		//user.getPosts().add(savedPost);
//		userRepository.save(user);
//
//		return savedPost;
//	}

	@Override
	public Post createPost(Post post, Integer userId) throws UserException {
		UserModel user = userService.findUserById(userId);

		Post newPost = new Post();
		newPost.setCaption(post.getCaption());
		newPost.setImagePost(post.getImagePost());
		newPost.setLocation(post.getLocation());
		newPost.setCreatedAt(LocalDateTime.now());

		UserDto userDto = new UserDto();
		userDto.setEmail(user.getEmail());
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setImage(user.getImage());
		userDto.setUsername(user.getUsername());

		newPost.setUser(userDto);
		Post createdPost = postRepository.save(newPost);

		return createdPost;
	}

	@Override
	public List<Post> findAllPost(Integer userId) throws PostException {
		return List.of();
	}


	@Override
	public List<Post> findAllPost() throws PostException {
		List<Post> posts = postRepository.findAll();
		if (posts.size() > 0) {
			return posts;
		}
		throw new PostException("Post Not Exist");
	}

	@Override
	public String deletePost(Integer postId, Integer userId) throws UserException, PostException {
		Post post = findPostById(postId);
		UserModel user = userService.findUserById(userId);

		if (post.getUser().getId().equals(user.getId())) {
			postRepository.deleteById(post.getId());
			return "Post Deleted Successfully";
		}

		throw new PostException("You can't delete othere user's post");
	}

	@Override
	public List<Post> findPostByUserId(Integer userId) throws UserException {
		List<Post> posts = postRepository.findByUserId(userId);

		if (posts.size() > 0) {
			return posts;
		}
		throw new UserException("This user don't have any post");
	}

	@Override
	public Post findPostById(Integer postId) throws PostException {
		Optional<Post> optionalPost = postRepository.findById(postId);

		if (optionalPost.isPresent()) {
			return optionalPost.get();
		}

		throw new PostException("Post not found with id " + postId);
	}

	@Override
	public List<Post> findAllPostByUserIds(List<Integer> userIds) throws PostException, UserException {
		List<Post> posts = postRepository.findAllPostByUserIds(userIds);

		if (posts.isEmpty()) {
			throw new PostException("No post avalable.");
		}
		return posts;
	}

	@Override
	public String savePost(Integer postId, Integer userId) throws PostException, UserException {

//		Post post = findPostById(postId);
//		UserModel user = userService.findUserById(userId);
//
//		if (!user.getSavedPost().contains(post)) {
//			user.getSavedPost().add(post);
//			userRepository.save(user);
//		}

		return "Post Saved Successfully";
	}

	@Override
	public String unsavePost(Integer postId, Integer userId) throws PostException, UserException {

//		Post post = findPostById(postId);
//		UserModel user = userService.findUserById(userId);
//
//		if (user.getSavedPost().contains(post)) {
//			user.getSavedPost().remove(post);
//			userRepository.save(user);
//		}

		return "Post Remove Successfully";
	}

	@Override
	public Post likePost(Integer postId, Integer userId) throws PostException, UserException {
		Post post = findPostById(postId);
		UserModel user = userService.findUserById(userId);

		UserDto userDto = new UserDto();
		userDto.setEmail(user.getEmail());
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setUsername(user.getUsername());
		userDto.setImage(user.getImage());

		post.getLikedByUser().add(userDto);

		return postRepository.save(post);
	}

	@Override
	public Post unlikePost(Integer postId, Integer userId) throws PostException, UserException {

		Post post = findPostById(postId);
		UserModel user = userService.findUserById(userId);

		UserDto userDto = new UserDto();
		userDto.setEmail(user.getEmail());
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setUsername(user.getUsername());
		userDto.setImage(user.getImage());

		post.getLikedByUser().remove(userDto);
		return postRepository.save(post);

	}


	@Override
	public Integer getPostCountByUser(UserModel viewedUser) {
		int postCount=0;
		try {
			postCount= findPostByUserId(viewedUser.getId()).size();
		} catch (UserException e) {
			System.out.println(e.getMessage());
        }
        return postCount;
	}

	@Override
	public boolean isLiked(Integer postId, UserModel userModel) {
		try {
			Post post = findPostById(postId);
			return post.getLikedByUser().stream()
					.anyMatch(dto -> dto.getId().equals(userModel.getId()));
		} catch (PostException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean isPostLikedByUser(Integer postId, Integer id) throws PostException {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostException("Post not found"));
		UserDto userDtoToCheck = new UserDto();
		userDtoToCheck.setId(id);
		return post.getLikedByUser().contains(userDtoToCheck);
	}
}
