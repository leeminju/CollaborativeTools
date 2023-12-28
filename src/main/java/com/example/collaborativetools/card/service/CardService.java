package com.example.collaborativetools.card.service;

import com.example.collaborativetools.card.Dto.CardRequestDto;
import com.example.collaborativetools.card.Dto.CardResponseDto;
import com.example.collaborativetools.card.Dto.CardSequenceDto;
import com.example.collaborativetools.card.Dto.CardUpdateRequestDto;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.card.repository.CardRepository;
import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.column.repository.ColumnsRepository;
import com.example.collaborativetools.global.constant.ErrorCode;
import com.example.collaborativetools.global.constant.ResponseCode;
import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.usercard.entity.UserCard;
import com.example.collaborativetools.usercard.repository.UserCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;
    private final ColumnsRepository columnsRepository;

    public BaseResponse<Card> addCard(Long columnId, CardRequestDto cardRequestDto, User user) {
        Columns columns = columnsRepository.findById(columnId).orElseThrow(() -> new NullPointerException("존재하지 않는 컬럼입니다"));
        Integer lastSequence = cardRepository.findLastSequenceInColumn(columns.getId());
        if (lastSequence == null) {
            lastSequence = 0;
        }

        Card card = new Card(cardRequestDto, columns);
        card.setColumn(columns);
        card.setSequence(lastSequence + 1);

        cardRepository.save(card);

        UserCard userCard = new UserCard();
        userCard.setUser(user);
        userCard.setCard(card);
        userCardRepository.save(userCard);

        return BaseResponse.of("카드가 추가되었습니다", HttpStatus.OK.value(), card);
    }

    @Transactional
    public BaseResponse<Card> updateCard(Long cardId, CardUpdateRequestDto cardUpdateDto, User user) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        card.update(cardUpdateDto);

        updateUserCard(card, user);

        return BaseResponse.of("카드가 수정되었습니다", HttpStatus.OK.value(), card);
    }

    @Transactional
    public BaseResponse<String> deleteCard(Long cardId, User user) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        cardRepository.delete(card);

//        updateUserCard(card, user);

        return BaseResponse.of(ResponseCode.DELETED_CARD, null);
    }

    public BaseResponse<CardResponseDto> getCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));

        return BaseResponse.of("카드를 조회하였습니다", HttpStatus.OK.value(), new CardResponseDto(card));
    }

    @Transactional
    public BaseResponse<Card> changeCardSequence(Long cardId, CardSequenceDto cardSequenceDto) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_CARD));
        int newSequence = cardSequenceDto.getSequence();
        int currentSequence = card.getSequence();

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
        else if(currentSequence > newSequence) {
            cardsToChange = cardRepository.findBySequenceBetweenOrderBySequence(newSequence, currentSequence - 1);
            for (Card c : cardsToChange) {
                if (c.getId().equals(card.getId())) continue; // 변경 대상 카드는 제외시켜줌
                c.setSequence(c.getSequence() + 1);
            }
        }else
            return BaseResponse.of("변경하고자 하는 카드의 순서가 같습니다", HttpStatus.OK.value(), card);


        cardRepository.saveAll(cardsToChange);

        return BaseResponse.of("카드 순서를 변경하였습니다", HttpStatus.OK.value(), card);
    }

    private void updateUserCard(Card card, User user) {
        UserCard userCard = userCardRepository.findByCardAndUser(card, user);
        userCardRepository.save(userCard);
    }
}
