package com.example.collaborativetools.column.dto.response;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.column.entitiy.Columns;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ColumnResponse {
	private final Long id;
	private final Long boardId;
	private final String title;
	private final Double sequence;
	private final Set<String> cards;

	public static ColumnResponse from(Columns entity) {
		return new ColumnResponse(
			entity.getId(),
			entity.getBoard().getId(),
			entity.getTitle(),
			entity.getSequence(),
			entity.getCards().stream()
				.map(Card::getTitle)
				.collect(Collectors.toUnmodifiableSet())
		);
	}
}
