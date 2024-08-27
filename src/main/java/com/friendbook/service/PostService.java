package com.friendbook.service;



import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entity.Post;
import com.friendbook.entity.UserModel;

import java.util.List;

public interface PostService {

	public Post createPost(Post post, Integer userId) throws UserException;


	public void deletePost(Integer postId, Integer userId) throws UserException, PostException;

	public List<Post> findPostByUserId(Integer userId) throws UserException;

	public Post findPostById(Integer postId) throws PostException;

	public List<Post> findAllPostByUserIds(List<Integer> userIds) throws PostException;

	public Post likePost(Integer postId, UserModel currentUser) throws PostException, UserException;

	public Post unlikePost(Integer postId, Integer userId) throws PostException, UserException;

	Integer getPostCountByUser(UserModel viewedUser);

	boolean isLiked(Integer postId, UserModel userModel);

	boolean isPostLikedByUser(Integer postId, Integer id) throws PostException;

	public List<Post> findAllPostsLikedByUser(Integer userId);
}
