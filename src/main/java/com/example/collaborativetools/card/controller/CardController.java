package com.example.collaborativetools.card.controller;

import com.example.collaborativetools.card.Dto.*;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.card.service.CardService;
import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class CardController {
    private final CardService cardService;

    @PostMapping("/{columnId}/cards")
    public ResponseEntity<BaseResponse<Card>> addCard(@PathVariable Long columnId, @RequestBody  @Valid CardRequestDto cardRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<Card> response = cardService.addCard(columnId, cardRequestDto, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cards/updatecard/{cardId}")
    public ResponseEntity<BaseResponse<Card>> updateCard(@PathVariable("cardId") Long cardId, @RequestBody CardUpdateRequestDto cardUpdateRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<Card> response = cardService.updateCard(cardId, cardUpdateRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<BaseResponse<String>> deleteCard(@PathVariable Long cardId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<String> response = cardService.deleteCard(cardId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<BaseResponse<CardResponseDto>> getCard(@PathVariable Long cardId) {
        BaseResponse<CardResponseDto> response = cardService.getCard(cardId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cards/changesequence/{cardId}")
    public ResponseEntity<BaseResponse<Card>> changeCardSequence(@PathVariable Long cardId, @RequestBody CardSequenceDto cardSequenceDto) {
        BaseResponse<Card> response = cardService.changeCardSequence(cardId, cardSequenceDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cards/jointocard/{cardId}")
    public ResponseEntity<BaseResponse<Card>> joinToCardMember(@PathVariable Long cardId, @RequestBody InviteDto inviteDto) {
        BaseResponse<Card> response = cardService.joinToCardMember(cardId, inviteDto);
        return ResponseEntity.ok(response);
    }
}
