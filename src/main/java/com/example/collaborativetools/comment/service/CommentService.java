package com.example.collaborativetools.comment.service;

import static com.example.collaborativetools.global.constant.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.card.repository.CardRepository;
import com.example.collaborativetools.comment.dto.request.CommentCreateRequest;
import com.example.collaborativetools.comment.dto.request.CommentUpdateRequest;
import com.example.collaborativetools.comment.dto.response.CommentResponse;
import com.example.collaborativetools.comment.entitiy.Comment;
import com.example.collaborativetools.comment.repository.CommentRepository;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.userboard.repository.UserBoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final UserBoardRepository userBoardRepository;
	private final CardRepository cardRepository;

	@Transactional
	public CommentResponse createComment(
		Long boardId,
		Long columnId,
		Long cardId,
		CommentCreateRequest request,
		User loginUser
	) {
		Card card = findCard(cardId);

		validateColumn(columnId, card);
		checkUserPermission(boardId, loginUser.getId());

		Comment comment = request.toEntity(card, loginUser);
		comment = commentRepository.save(comment);

		return CommentResponse.from(comment);
	}

	@Transactional
	public CommentResponse updateComment(
		Long boardId,
		Long columnId,
		Long cardId,
		Long commentId,
		CommentUpdateRequest request,
		User loginUser
	) {
		Card card = findCard(cardId);
		Comment comment = findComment(commentId);

		validateColumn(columnId, card);
		checkUserPermission(boardId, loginUser.getId());
		checkCommentPermission(comment.getUser().getId(), loginUser.getId());

		comment.update(request);

		return CommentResponse.from(comment);
	}

	@Transactional
	public void deleteComment(
		Long boardId,
		Long columnId,
		Long cardId,
		Long commentId,
		User loginUser
	) {
		Card card = findCard(cardId);
		Comment comment = findComment(commentId);

		validateColumn(columnId, card);
		checkUserPermission(boardId, loginUser.getId());
		checkCommentPermission(comment.getUser().getId(), loginUser.getId());

		commentRepository.delete(comment);
	}

	private void checkUserPermission(Long boardId, Long userId) {
		if (!userBoardRepository.existsByBoardIdAndUserId(boardId, userId)) {
			throw new ApiException(NO_BOARD_AUTHORITY_EXCEPTION);
		}
	}



	public Card findCard(Long cardId) {
		return cardRepository.findById(cardId)
			.orElseThrow(() -> new ApiException(NOT_FOUND_CARD));
	}

	public Comment findComment(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new ApiException(NOT_FOUND_COMMENT));
	}
	
	private static void checkCommentPermission(Long writerId, Long loginUserId) {
		if (!writerId.equals(loginUserId)) {
			throw new ApiException(NO_COMMENT_AUTHORITY_EXCEPTION);
		}
	}

	private static void validateColumn(Long columnId, Card card) {
		if (!columnId.equals(card.getColumn().getId())) {
			throw new ApiException(INVALID_VALUE);
		}
	}
}
