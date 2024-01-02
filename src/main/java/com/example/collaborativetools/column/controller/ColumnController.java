package com.example.collaborativetools.column.controller;

import static com.example.collaborativetools.global.constant.ResponseCode.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.collaborativetools.column.dto.request.ColumnCreateRequest;
import com.example.collaborativetools.column.dto.request.ColumnUpdateRequest;
import com.example.collaborativetools.column.dto.response.ColumnResponse;
import com.example.collaborativetools.column.service.ColumnService;
import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import com.example.collaborativetools.user.entitiy.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/columns")
@RestController
public class ColumnController {
	private final ColumnService columnService;

	//컬럼생성
	@PostMapping
	public ResponseEntity<BaseResponse<ColumnResponse>> createColumn(
		@PathVariable Long boardId,
		@Valid @RequestBody ColumnCreateRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		User user = userDetails.getUser();
		ColumnResponse response = columnService.createColumn(boardId, request, user);
		return ResponseEntity.status(CREATED_COLUMNS.getHttpStatus())
			.body(
				BaseResponse.of(
					CREATED_COLUMNS,
					response
				)
			);
	}

	//컬럼수정
	@PutMapping("/{columnId}")
	public ResponseEntity<BaseResponse<ColumnResponse>> updateColumn(
		@PathVariable Long boardId,
		@PathVariable Long columnId,
		@Valid @RequestBody ColumnUpdateRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		User user = userDetails.getUser();
		ColumnResponse response = columnService.updateColumn(boardId, columnId, request, user);
		return ResponseEntity.status(UPDATED_COLUMNS.getHttpStatus())
			.body(
				BaseResponse.of(
					UPDATED_COLUMNS,
					response
				)
			);
	}

	//컬럼삭제
	@DeleteMapping("/{columnId}")
	public ResponseEntity<BaseResponse<Void>> deleteColumn(
		@PathVariable Long columnId
	) {
		columnService.deleteColumn(columnId);

		return ResponseEntity.status(DELETED_COLUMNS.getHttpStatus())
			.body(
				BaseResponse.of(
					DELETED_COLUMNS,
					null
				)
			);
	}
}
