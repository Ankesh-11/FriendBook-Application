package com.friendbook.repository;

import com.friendbook.entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

	public Optional<UserModel> findByEmail(String email);

	public Optional<UserModel> findByUsername(String username);

	@Query("SELECT u FROM UserModel u WHERE u.id IN :users")
	public List<UserModel> findAllUserByUserIds(@Param("users") List<Integer> userIds);

	@Query("SELECT DISTINCT u FROM UserModel u WHERE u.username LIKE %:query% OR u.email LIKE %:query%")
	public List<UserModel> findByQuery(@Param("query") String query);

}
