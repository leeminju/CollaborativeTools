package com.example.collaborativetools.column.service;

import static com.example.collaborativetools.global.constant.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Sort;
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
import com.example.collaborativetools.userboard.repository.UserBoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ColumnService {
	private static final Sort SORT_BY_SEQUENCE_ASC = Sort.by(Sort.Direction.ASC, "sequence");
	private final BoardRepository boardRepository;
	private final ColumnRepository columnRepository;
	private final UserBoardRepository userBoardRepository;

	@Transactional
	public ColumnResponse createColumn(ColumnCreateRequest request, User loginUser) {
		Board board = boardRepository.findById(request.getBoardId())
			.orElseThrow(() -> new ApiException(NOT_FOUND_BOARD));

		checkUserPermission(board.getId(), loginUser.getId());

		Columns column = columnRepository.save(request.toEntity(board));

		return ColumnResponse.from(column);
	}

	@Transactional
	public ColumnResponse updateColumn(Long columnId, ColumnUpdateRequest request, User loginUser) {
		Board board = boardRepository.findById(request.getBoardId())
			.orElseThrow(() -> new ApiException(NOT_FOUND_BOARD));

		checkUserPermission(board.getId(), loginUser.getId());
		checkUserPermission(board.getId(), loginUser.getId());

		Columns column = findColumn(columnId);
		column.update(request);

		return ColumnResponse.from(column);
	}

	@Transactional
	public void deleteColumn(Long columnId) {
		Columns column = findColumn(columnId);
		columnRepository.delete(column);
	}

	public List<ColumnResponse> getColumns() {
		return columnRepository.findAll(SORT_BY_SEQUENCE_ASC)
			.stream()
			.map(ColumnResponse::from)
			.toList();
	}

	public Columns findColumn(Long columnId) {
		return columnRepository.findById(columnId)
			.orElseThrow(() -> new ApiException(NOT_FOUND_COLUMN));
	}

	public void checkUserPermission(Long boardId, Long userId) {
		if (!userBoardRepository.existsByBoardIdAndUserId(boardId, userId)) {
			throw new ApiException(NO_BOARD_AUTHORITY_EXCEPTION);
		}
	}
}
