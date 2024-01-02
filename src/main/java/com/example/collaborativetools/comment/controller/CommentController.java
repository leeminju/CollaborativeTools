package com.example.collaborativetools.comment.controller;

import static com.example.collaborativetools.global.constant.ResponseCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.collaborativetools.comment.dto.request.CommentUpdateRequest;
import com.example.collaborativetools.comment.dto.request.CommentCreateRequest;
import com.example.collaborativetools.comment.dto.response.CommentResponse;
import com.example.collaborativetools.comment.service.CommentService;
import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import com.example.collaborativetools.user.entitiy.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/columns/{columnId}/cards/{cardId}/comments")
@RestController
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<BaseResponse<CommentResponse>> createComment(
		@PathVariable Long boardId,
		@PathVariable Long columnId,
		@PathVariable Long cardId,
		@Valid @RequestBody CommentCreateRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		User user = userDetails.getUser();
		CommentResponse response = commentService.createComment(boardId, columnId, cardId, request, user);

		return ResponseEntity.status(CREATED_COMMENT.getHttpStatus())
			.body(
				BaseResponse.of(
					CREATED_COMMENT,
					response
				)
			);
	}

	@PutMapping("/{commentId}")
	public ResponseEntity<BaseResponse<CommentResponse>> updateComment(
		@PathVariable Long boardId,
		@PathVariable Long columnId,
		@PathVariable Long cardId,
		@PathVariable Long commentId,
		@Valid @RequestBody CommentUpdateRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		User user = userDetails.getUser();
		CommentResponse response = commentService.updateComment(boardId, columnId, cardId, commentId, request, user);

		return ResponseEntity.status(UPDATED_COMMENT.getHttpStatus())
			.body(
				BaseResponse.of(
					UPDATED_COMMENT,
					response
				)
			);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<BaseResponse<Void>> deleteComment(
		@PathVariable Long boardId,
		@PathVariable Long columnId,
		@PathVariable Long cardId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		User user = userDetails.getUser();
		commentService.deleteComment(boardId, columnId, cardId, commentId, user);

		return ResponseEntity.status(DELETED_COMMENT.getHttpStatus())
			.body(
				BaseResponse.of(
					DELETED_COMMENT,
					null
				)
			);
	}
}
