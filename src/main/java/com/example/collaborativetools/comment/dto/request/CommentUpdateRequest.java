package com.example.collaborativetools.comment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentUpdateRequest {
	@NotNull
	private final Long boardId;
	@NotNull
	private final Long cardId;
	@Size(max = 250)
	private final String comment;
}
