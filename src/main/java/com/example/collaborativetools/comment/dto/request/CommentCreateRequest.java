package com.example.collaborativetools.comment.dto.request;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.comment.entitiy.Comment;
import com.example.collaborativetools.user.entitiy.User;
import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentCreateRequest {
	@Size(max = 250)
	private final String comment;

	@JsonCreator
	public CommentCreateRequest(String comment) {
		this.comment = comment;
	}

	public Comment toEntity(Card card, User user) {
		return Comment.create(
			comment,
			card,
			user
		);
	}
}
