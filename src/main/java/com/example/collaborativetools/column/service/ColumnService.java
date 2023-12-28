package com.example.collaborativetools.column.service;

import static com.example.collaborativetools.global.constant.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.board.repository.BoardRepository;
import com.example.collaborativetools.column.dto.request.ColumnCreateRequest;
import com.example.collaborativetools.column.dto.request.ColumnUpdateRequest;
import com.example.collaborativetools.column.dto.response.ColumnResponse;
import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.column.repository.ColumnRepository;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.entitiy.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ColumnService {
	private final BoardRepository boardRepository;
	private final ColumnRepository columnRepository;

	@Transactional
	public ColumnResponse createColumn(ColumnCreateRequest request, User loginUser) {
		// TODO: 12/27/23 로그인 유저정보로 UserBoard에 해당 유저 존재하는지 검증 구현 필요

		Board board = boardRepository.findById(request.getBoardId())
			.orElseThrow(() -> new ApiException(NOT_FOUND_BOARD));

		Columns column = columnRepository.save(request.toEntity(board));

		return ColumnResponse.from(column);
	}

	@Transactional
	public ColumnResponse updateColumn(Long columnId, ColumnUpdateRequest request, User loginUser) {
		Board board = boardRepository.findById(request.getBoardId())
			.orElseThrow(() -> new ApiException(NOT_FOUND_BOARD));

		Columns column = columnRepository.findById(columnId)
			.orElseThrow(() -> new ApiException(NOT_FOUND_COLUMN));

		// TODO: 12/27/23 로그인 유저정보로 UserBoard에 해당 유저 존재하는지 검증 구현 필요

		column.update(
			request.getTitle(),
			request.getSequence(),
			board
		);
		return ColumnResponse.from(column);
	}

	@Transactional
	public void deleteColumn(Long columnId, User loginUser) {
		Columns column = columnRepository.findById(columnId)
			.orElseThrow(() -> new ApiException(NOT_FOUND_COLUMN));

		// TODO: 12/27/23 로그인 유저정보로 UserBoard에 해당 유저 존재하는지 검증 구현 필요

		columnRepository.delete(column);
	}
}
