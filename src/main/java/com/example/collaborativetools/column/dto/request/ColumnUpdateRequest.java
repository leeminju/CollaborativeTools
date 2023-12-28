package com.example.collaborativetools.column.dto.request;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.column.entitiy.Columns;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ColumnUpdateRequest {
	@NotNull(message = "필수로 입력")
	private final Long boardId;

	@NotBlank(message = "제목을 입력해주세요")
	@Size(max = 500, message = "500자 이하로 입력")
	private final String title;

	@NotNull
	private final Integer sequence;

	public Columns toEntity(Board entity) {
		return Columns.create(title, sequence, entity);
	}
}
