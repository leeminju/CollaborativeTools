package com.example.collaborativetools.comment.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentUpdateRequest {
	@Size(max = 250)
	private final String comment;
}
