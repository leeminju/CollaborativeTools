package com.example.collaborativetools.comment.dto.request;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.comment.entitiy.Comment;
import com.example.collaborativetools.user.entitiy.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentCreateRequest {
	@NotNull
	private final Long boardId;
	@NotNull
	private final Long cardId;
	@Size(max = 250)
	private final String comment;


	public Comment toEntity(Card card, User user) {
		return Comment.create(
			comment,
			card,
			user
		);
	}
}
