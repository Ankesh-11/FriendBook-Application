package com.friendbook.service;



import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entities.Post;
import com.friendbook.entities.UserModel;

import java.util.List;

public interface PostService {

	public Post createPost(Post post, Integer userId) throws UserException;

	public List<Post> findAllPost(Integer userId) throws PostException;

	List<Post> findAllPost() throws PostException;

	public void deletePost(Integer postId, Integer userId) throws UserException, PostException;

	public List<Post> findPostByUserId(Integer userId) throws UserException;

	public Post findPostById(Integer postId) throws PostException;

	public List<Post> findAllPostByUserIds(List<Integer> userIds) throws PostException, UserException;

	public String savePost(Integer postId, Integer userId) throws PostException, UserException;

	public String unsavePost(Integer postId, Integer userId) throws PostException, UserException;

	public Post likePost(Integer postId, UserModel userId, UserModel currentUser) throws PostException, UserException;

	public Post unlikePost(Integer postId, Integer userId) throws PostException, UserException;

	Integer getPostCountByUser(UserModel viewedUser);

	boolean isLiked(Integer postId, UserModel userModel);

	boolean isPostLikedByUser(Integer postId, Integer id) throws PostException;
}
