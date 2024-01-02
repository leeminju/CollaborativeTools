package com.example.collaborativetools.comment.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CommentUpdateRequest {
	@Size(max = 250)
	private final String comment;

	@JsonCreator
	public CommentUpdateRequest(String comment) {
		this.comment = comment;
	}
}
