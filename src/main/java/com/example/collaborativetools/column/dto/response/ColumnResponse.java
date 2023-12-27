package com.example.collaborativetools.column.dto.response;

import com.example.collaborativetools.column.entitiy.Columns;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ColumnResponse {
	private final Long id;
	private final Long boardId;
	private final String title;
	private final Integer sequence;

	public static ColumnResponse of(Columns entity) {
		return new ColumnResponse(
			entity.getId(),
			entity.getBoard().getId(),
			entity.getTitle(),
			entity.getSequence()
		);
	}
}
