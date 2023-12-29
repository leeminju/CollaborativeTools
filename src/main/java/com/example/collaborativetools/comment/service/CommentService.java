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
	public CommentResponse createComment(CommentCreateRequest request, User loginUser) {
		Card card = cardRepository.findById(request.getCardId())
			.orElseThrow(() -> new ApiException(NOT_FOUND_CARD));

		checkUserPermission(request.getBoardId(), loginUser.getId());

		Comment comment = request.toEntity(card, loginUser);
		comment = commentRepository.save(comment);

		return CommentResponse.from(comment);
	}

	@Transactional
	public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, User loginUser) {
		Comment comment = findComment(commentId);

		checkUserPermission(request.getBoardId(), loginUser.getId());
		checkCommentPermission(comment.getUser().getId(), loginUser.getId());

		comment.update(request);

		return CommentResponse.from(comment);
	}

	@Transactional
	public void deleteComment(Long commentId, User loginUser) {
		Comment comment = findComment(commentId);
		checkCommentPermission(comment.getUser().getId(), loginUser.getId());

		commentRepository.delete(comment);
	}

	private void checkUserPermission(Long boardId, Long userId) {
		if (!userBoardRepository.existsByBoardIdAndUserId(boardId, userId)) {
			throw new ApiException(NO_BOARD_AUTHORITY_EXCEPTION);
		}
	}

	private void checkCommentPermission(Long writerId, Long loginUserId) {
		if (!writerId.equals(loginUserId)) {
			throw new ApiException(NO_COMMENT_AUTHORITY_EXCEPTION);
		}
	}

	public Comment findComment(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new ApiException(NOT_FOUND_COMMENT));
	}
}
