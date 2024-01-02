package com.example.collaborativetools.card.service;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.board.repository.BoardRepository;
import com.example.collaborativetools.board.service.BoardService;
import com.example.collaborativetools.card.dto.*;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.card.repository.CardRepository;
import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.column.repository.ColumnRepository;
import com.example.collaborativetools.global.constant.ErrorCode;
import com.example.collaborativetools.global.constant.ResponseCode;
import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.user.repository.UserRepository;
import com.example.collaborativetools.userboard.entity.UserBoard;
import com.example.collaborativetools.userboard.repository.UserBoardRepository;
import com.example.collaborativetools.usercard.entity.UserCard;
import com.example.collaborativetools.usercard.repository.UserCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.collaborativetools.global.constant.ErrorCode.NOT_CARD_MEMBER;
import static com.example.collaborativetools.global.constant.ErrorCode.NO_BOARD_AUTHORITY_EXCEPTION;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;
    private final ColumnRepository columnsRepository;
    private final UserRepository userRepository;
    private final UserBoardRepository userBoardRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    public BaseResponse<Card> addCard(Long boardId, Long columnId, CardRequestDto cardRequestDto, User user) {

        Board boards = checkBoard(boardId);
        Columns columns = checkColumn(columnId);

        //보드 유저 확인
        validateUserInvitation(boards.getId(), user.getId());

        Integer lastSequence = cardRepository.findLastSequenceInColumn(columns.getId());
        if (lastSequence == null) lastSequence = 0;

        Card card = new Card(cardRequestDto);
        card.setColumn(columns);
        card.setSequence(lastSequence + 1);

        cardRepository.save(card);

        UserCard userCard = new UserCard(user, card);
        userCardRepository.save(userCard);

        return BaseResponse.of("카드가 추가되었습니다", HttpStatus.OK.value(), card);
    }

    public BaseResponse<Card> joinToCardMember(Long boardId, Long columnId, Long cardId, InviteDto inviteDto, User user) {
        User userToInvite = userRepository.findByUsername(inviteDto.getUsername()).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        Board boards = checkBoard(boardId);
        checkColumn(columnId);
        //보드 유저 확인
        validateUserInvitation(boards.getId(), user.getId());

        Optional<UserBoard> userBoardOptional = userBoardRepository.findByBoardAndUser(boards, userToInvite);
        if (userCardRepository.existsByCardAndUser(card, userToInvite)) {
            return BaseResponse.of("이미 카드 멤버에 존재합니다", HttpStatus.OK.value(), null);
        } else if (userBoardOptional.isPresent()) {
            UserBoard userBoard = userBoardOptional.get();
            User invitedUser = userBoard.getUser();


            UserCard userCard = new UserCard(invitedUser, card);
            userCardRepository.save(userCard);

            return BaseResponse.of("멤버에 추가되었습니다", HttpStatus.OK.value(), null);

        }
        return BaseResponse.of("보드멤버가 아닙니다", HttpStatus.CONFLICT.value(), null);
    }


    @Transactional
    public BaseResponse<Card> updateCard(Long boardId, Long columnId, Long cardId, CardUpdateRequestDto
            cardUpdateDto, User user) {
        Board boards = checkBoard(boardId);
        checkColumn(columnId);

        //보드 유저 확인
        validateUserInvitation(boards.getId(), user.getId());

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        card.update(cardUpdateDto);

        return BaseResponse.of("카드가 수정되었습니다", HttpStatus.OK.value(), card);
    }


    @Transactional
    public BaseResponse<String> deleteCard(Long boardId, Long columnId, Long cardId, User user) {
        Board boards = checkBoard(boardId);
        checkColumn(columnId);

        //보드 유저 확인
        validateUserInvitation(boards.getId(), user.getId());

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        cardRepository.delete(card);

        return BaseResponse.of(ResponseCode.DELETED_CARD, null);
    }


    public BaseResponse<CardResponseDto> getCard(Long boardId, Long columnId, Long cardId, User user) {
        Board boards = checkBoard(boardId);
        checkColumn(columnId);

        //보드 유저 확인
        validateUserInvitation(boards.getId(), user.getId());

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));

        return BaseResponse.of("카드를 조회하였습니다", HttpStatus.OK.value(), new CardResponseDto(card));
    }


    @Transactional
    public BaseResponse<Card> changeCardSequence(Long boardId, Long columnId, Long
            cardId, CardSequenceDto cardSequenceDto, User user) {
        Board boards = checkBoard(boardId);
        checkColumn(columnId);

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        int newSequence = cardSequenceDto.getSequence();
        int currentSequence = card.getSequence();
        long currentColumnId = card.getColumn().getId();

        //보드 유저 확인
        validateUserInvitation(boards.getId(), user.getId());

        //대분류 같은 컬럼에서 이동할 때 / 컬럼 이동할 때
        Columns column = columnsRepository.findById(cardSequenceDto.getColumnId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_COLUMN));

        card.setColumn(column);
        card.setSequence(newSequence);
        cardRepository.save(card);

        List<Card> cardsToChange;
        //같은 컬럼에서 이동할 때
        // 변경하려는 순서가 총 갯수를 넘어가면 최대 순서로 변경
        if (currentColumnId == cardSequenceDto.getColumnId()) {
            long totalCards = cardRepository.count();
            int maxSequence = (int) totalCards;

            if (newSequence > maxSequence) {
                newSequence = maxSequence;
            }

            // 순서를 뒤로 옮길 때
            if (currentSequence < newSequence) {
                cardsToChange = cardRepository.findByColumnIdAndSequenceBetweenOrderBySequence(column.getId(),
                        currentSequence + 1, newSequence);
                for (Card c : cardsToChange) {
                    if (c.getId().equals(card.getId()))
                        continue; // 변경 대상 카드는 제외시켜줌
                    c.setSequence(c.getSequence() - 1);
                }
            }
            // 순서를 앞으로 옮길 때
            else if (currentSequence > newSequence) {
                cardsToChange = cardRepository.findByColumnIdAndSequenceBetweenOrderBySequence(column.getId(),
                        newSequence,
                        currentSequence - 1);
                for (Card c : cardsToChange) {
                    if (c.getId().equals(card.getId()))
                        continue; // 변경 대상 카드는 제외시켜줌
                    c.setSequence(c.getSequence() + 1);
                }
            } else
                return BaseResponse.of("변경하고자 하는 카드의 순서가 같습니다", HttpStatus.OK.value(), card);
        } else { // 다른컬럼으로 이동하는 경우 이동한 시퀀스 부터 쭉 밀기
            //기존에 있었던 컬럼 재정렬
            List<Card> cards = cardRepository.findByColumnId(currentColumnId);

            if (!cards.isEmpty()) {
                int seq = 1;
                for (Card c : cards) {
                    c.setSequence(seq++);
                }
            }
            //옮긴 시퀀스 부터 쭉 밀기
            int size = cardRepository.countByColumnId(column.getId());
            cardsToChange = cardRepository.findByColumnIdAndSequenceBetweenOrderBySequence(column.getId(),
                    newSequence, size);
            for (Card c : cardsToChange) {
                if (c.getId().equals(card.getId()))
                    continue; // 변경 대상 카드는 제외시켜줌
                c.setSequence(c.getSequence() + 1);
            }
        }


        return BaseResponse.of("카드 순서를 변경하였습니다", HttpStatus.OK.value(), card);
    }


    private void validateUserInvitation(Long boardId, Long userId) {
        boolean isUserInvited = boardService.isUserInvited(boardId, userId);
        if (!isUserInvited) {
            throw new ApiException(NO_BOARD_AUTHORITY_EXCEPTION);
        }
    }

    private Board checkBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_BOARD));
    }

    private Columns checkColumn(Long columnId) {
        return columnsRepository.findById(columnId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_COLUMN));
    }

    public BaseResponse<String> deleteCardMember(Long boardId, Long columnId, Long cardId, Long userId, User user) {
        User removeUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_USER));
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        Board boards = checkBoard(boardId);
        checkColumn(columnId);
        //보드 유저 확인
        validateUserInvitation(boards.getId(), user.getId());

        UserCard userCard = userCardRepository.findByCardAndUser(card, removeUser).orElseThrow(
                () -> new ApiException(NOT_CARD_MEMBER));

        userCardRepository.delete(userCard);

        return BaseResponse.of(ResponseCode.DELETED_CARD_MEMBER, null);
    }
}
