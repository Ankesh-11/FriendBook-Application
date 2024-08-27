package com.friendbook.service.impl;

import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entity.Notification;
import com.friendbook.entity.Post;
import com.friendbook.entity.UserModel;
import com.friendbook.repository.CommentRepository;
import com.friendbook.repository.NotificationRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;

import jakarta.transaction.Transactional;
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

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	CommentRepository commentRepository;

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
	@Transactional
	public void deletePost(Integer postId, Integer userId) throws UserException, PostException {
		Post post = findPostById(postId);
		UserModel user = userService.findUserById(userId);

		if (post.getUser().getId().equals(user.getId())) {

			notificationRepository.deleteByPostId(postId);
			commentRepository.deleteByPostId(postId);
			postRepository.deleteById(post.getId());
		} else {
			throw new UserException("You do not have permission to delete this post");
		}
		return;
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
	public List<Post> findAllPostByUserIds(List<Integer> userIds) throws PostException {
		List<Post> posts = postRepository.findAllPostByUserIds(userIds);
		if (posts.isEmpty()) {
			throw new PostException("No post avalable.");
		}
		return posts;
	}

	@Override
	@Transactional
	public Post likePost(Integer postId, UserModel currUser) throws PostException, UserException {
		Post post = findPostById(postId);
		UserModel toUser = userService.findUserById(post.getUser().getId());
		UserDto userDto = new UserDto();
		userDto.setEmail(currUser.getEmail());
		userDto.setId(currUser.getId());
		userDto.setName(currUser.getName());
		userDto.setUsername(currUser.getUsername());
		userDto.setImage(currUser.getImage());

		post.getLikedByUser().add(userDto);
		boolean x =  !toUser.equals(currUser);
		System.out.println(x);
		if(x)
		{
			Notification notification = new Notification();
			notification.setFromUser(currUser);
			notification.setToUser(toUser);
			notification.setMessage(currUser.getUsername() + " liked your post.");
			notification.setCreatedAt(LocalDateTime.now());
			notification.setPost(post);

			notificationRepository.save(notification);
			toUser.getNotifications().add(notification);

		}
		userRepository.save(toUser);

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

	@Override
	public List<Post> findAllPostsLikedByUser(Integer userId) {
		return postRepository.findAllPostsLikedByUser(userId);
	}
}
