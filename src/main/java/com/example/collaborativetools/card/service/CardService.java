package com.example.collaborativetools.card.service;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.board.repository.BoardRepository;
import com.example.collaborativetools.board.service.BoardService;
import com.example.collaborativetools.card.Dto.*;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.card.repository.CardRepository;
import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.column.repository.ColumnsRepository;
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

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;
    private final ColumnsRepository columnsRepository;
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
        User userToInvite = userRepository.findByUsername(inviteDto.getUsername()).orElseThrow(() -> new NullPointerException("존재하지 않는 유저입니다"));
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new NullPointerException("존재하지 않는 카드입니다"));
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
    public BaseResponse<Card> updateCard(Long boardId, Long columnId, Long cardId, CardUpdateRequestDto cardUpdateDto, User user) {
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
    public BaseResponse<Card> changeCardSequence(Long boardId, Long columnId, Long cardId, CardSequenceDto cardSequenceDto, User user) {
        Board boards = checkBoard(boardId);
        checkColumn(columnId);

        //보드 유저 확인
        validateUserInvitation(boards.getId(), user.getId());

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        int newSequence = cardSequenceDto.getSequence();
        int currentSequence = card.getSequence();

        long totalCards = cardRepository.count();
        int maxSequence = (int) totalCards;

        // 변경하려는 순서가 총 갯수를 넘어가면 최대 순서로 변경
        if (newSequence > maxSequence) {
            newSequence = maxSequence;
        }

        card.setSequence(newSequence);
        cardRepository.save(card);

        List<Card> cardsToChange;

        // 순서를 뒤로 옮길 때
        if (currentSequence < newSequence) {
            cardsToChange = cardRepository.findBySequenceBetweenOrderBySequence(currentSequence + 1, newSequence);
            for (Card c : cardsToChange) {
                if (c.getId().equals(card.getId())) continue; // 변경 대상 카드는 제외시켜줌
                c.setSequence(c.getSequence() - 1);
            }
        }
        // 순서를 앞으로 옮길 때
        else if (currentSequence > newSequence) {
            cardsToChange = cardRepository.findBySequenceBetweenOrderBySequence(newSequence, currentSequence - 1);
            for (Card c : cardsToChange) {
                if (c.getId().equals(card.getId())) continue; // 변경 대상 카드는 제외시켜줌
                c.setSequence(c.getSequence() + 1);
            }
        } else
            return BaseResponse.of("변경하고자 하는 카드의 순서가 같습니다", HttpStatus.OK.value(), card);


        cardRepository.saveAll(cardsToChange);

        return BaseResponse.of("카드 순서를 변경하였습니다", HttpStatus.OK.value(), card);
    }

    private void validateUserInvitation(Long boardId, Long userId) {
        boolean isUserInvited = boardService.isUserInvited(boardId, userId);
        if (!isUserInvited) {
            throw new SecurityException("보드에 초대되지 않은 사용자는 카드에 접근할 수 없습니다.");
        }
    }

    private Board checkBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("존재하지 않는 보드입니다"));
    }

    private Columns checkColumn(Long columnId) {
        return columnsRepository.findById(columnId).orElseThrow(() -> new NullPointerException("존재하지 않는 컬럼입니다"));
    }
}
