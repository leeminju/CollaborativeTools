package com.example.collaborativetools.column.service;

import org.springframework.stereotype.Service;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.board.repository.BoardRepository;
import com.example.collaborativetools.column.dto.request.ColumnCreateRequest;
import com.example.collaborativetools.column.dto.response.ColumnResponse;
import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.column.repository.ColumnRepository;
import com.example.collaborativetools.global.constant.ErrorCode;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.entitiy.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ColumnService {
	private final BoardRepository boardRepository;
	private final ColumnRepository columnRepository;

	public ColumnResponse createColumn(ColumnCreateRequest request, User loginUser) {
		// TODO: 12/27/23 로그인 유저정보로 UserBoard에 해당 유저 존재하는지 검증 구현 필요

		Board board = boardRepository.findById(request.getBoardId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_BOARD));

		Columns column = columnRepository.save(request.toEntity(board));

		return ColumnResponse.of(column);
	}


}
