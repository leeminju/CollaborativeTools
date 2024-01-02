package com.example.collaborativetools.card.controller;

import com.example.collaborativetools.card.dto.*;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.card.service.CardService;
import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/boards/{boardId}/columns/{columnId}/cards")
@RequiredArgsConstructor
@RestController
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<BaseResponse<Card>> addCard(@PathVariable Long boardId, @PathVariable Long columnId, @RequestBody CardRequestDto cardRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<Card> response = cardService.addCard(boardId, columnId, cardRequestDto, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cardId}/update")
    public ResponseEntity<BaseResponse<Card>> updateCard(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable("cardId") Long cardId, @RequestBody CardUpdateRequestDto cardUpdateRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<Card> response = cardService.updateCard(boardId, columnId, cardId, cardUpdateRequestDto, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<BaseResponse<String>> deleteCard(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long cardId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<String> response = cardService.deleteCard(boardId, columnId, cardId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<BaseResponse<CardResponseDto>> getCard(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long cardId , @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<CardResponseDto> response = cardService.getCard(boardId, columnId, cardId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cardId}/sequence")
    public ResponseEntity<BaseResponse<Card>> changeCardSequence(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long cardId, @RequestBody CardSequenceDto cardSequenceDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<Card> response = cardService.changeCardSequence(boardId, columnId, cardId, cardSequenceDto, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cardId}/cardmember")
    public ResponseEntity<BaseResponse<Card>> joinToCardMember(@PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long cardId, @RequestBody InviteDto inviteDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BaseResponse<Card> response = cardService.joinToCardMember(boardId, columnId, cardId, inviteDto, userDetails.getUser());
        return ResponseEntity.ok(response);
    }
}
