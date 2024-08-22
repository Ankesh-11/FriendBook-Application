package com.friendbook.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.friendbook.dto.UserDto;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Commments")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "user_id")),
			@AttributeOverride(name = "email", column = @Column(name = "user_email")) })
	private UserDto user;

	@NonNull
	private String content;
	private LocalDateTime createdAt;

	@Embedded
	@ElementCollection
	private Set<UserDto> likedByUser = new HashSet<UserDto>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "post_id")
	@JsonBackReference
	private Post post;

}
