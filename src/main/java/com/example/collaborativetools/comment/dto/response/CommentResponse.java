package com.example.collaborativetools.comment.dto.response;

import java.time.LocalDateTime;

import com.example.collaborativetools.comment.entitiy.Comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentResponse {
	private final Long id;
	private final String comment;
	private final String writer;
	private final LocalDateTime createdAt;

	public static CommentResponse from(Comment entity) {
		return new CommentResponse(
			entity.getId(),
			entity.getComment(),
			entity.getUser().getUsername(),
			entity.getCreatedAt()
		);
	}
}
