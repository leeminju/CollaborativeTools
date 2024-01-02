package com.example.collaborativetools.board.controller;

import static com.example.collaborativetools.global.constant.ResponseCode.*;

import com.example.collaborativetools.board.dto.CreateBoardDTO;
import com.example.collaborativetools.board.dto.CreateBoardDTO.Response;
import com.example.collaborativetools.board.dto.GetDetailResponseBoardDTO;
import com.example.collaborativetools.board.dto.GetMemberResponseBoardDTO;
import com.example.collaborativetools.board.dto.GetResponseBoardDTO;
import com.example.collaborativetools.board.dto.InviteRequestBoardDTO;
import com.example.collaborativetools.board.dto.UpdateBoardDTO;
import com.example.collaborativetools.board.service.BoardService;
import com.example.collaborativetools.global.dto.BaseResponse;
import com.example.collaborativetools.global.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/boards")
@RestController
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  //보드 생성
  @PostMapping()
  public ResponseEntity<BaseResponse<CreateBoardDTO.Response>> createBoard(
      @Valid @RequestBody CreateBoardDTO.Request request,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    CreateBoardDTO.Response responseDTO = boardService.createBoard(request,
        userDetails.getUser().getId());

    return ResponseEntity.status(HttpStatus.CREATED).body(
        BaseResponse.of(CREATED_BOARD, responseDTO));
  }

  //로그인중 유저 보드 조회
  @GetMapping()
  public ResponseEntity<BaseResponse<List<GetResponseBoardDTO>>> getBoard(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    List<GetResponseBoardDTO> responseBoardDTOList = boardService.getBoard(
        userDetails.getUser().getId());

    return ResponseEntity.status(HttpStatus.OK).body(
        BaseResponse.of(GET_BOARD, responseBoardDTOList));
  }

  //보드 수정
  @PutMapping("/{boardId}")
  public ResponseEntity<BaseResponse<UpdateBoardDTO.Response>> updateBoard(
      @PathVariable Long boardId,
      @Valid @RequestBody UpdateBoardDTO.Request request,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    UpdateBoardDTO.Response responseDTO = boardService.updateBoard(boardId, request,
        userDetails.getUser().getId());

    return ResponseEntity.status(HttpStatus.OK).body(
        BaseResponse.of(UPDATE_BOARD, responseDTO));

  }

  //보드 삭제
  @DeleteMapping("/{boardId}")
  public ResponseEntity<BaseResponse<String>> deleteBoard(
      @PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    boardService.deleteBoard(boardId, userDetails.getUser().getId());

    return ResponseEntity.status(HttpStatus.OK).body(
        BaseResponse.of(DELETE_BOARD, String.format("%d번 보드 삭제 완료", boardId)));
  }

  //보드 멤버 초대
  @PostMapping("/{boardId}/users")
  public ResponseEntity<BaseResponse<String>> inviteMemberToBoard(
      @PathVariable Long boardId,
      @Valid @RequestBody InviteRequestBoardDTO request,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {

    boardService.inviteMemberToBoard(boardId, request, userDetails.getUser().getId());

    return ResponseEntity.status(HttpStatus.OK).body(
        BaseResponse.of(INVITE_BOARD, request.username() + " 초대 완료"));
  }

  // 보드 내 멤버 조회
  @GetMapping("/{boardId}/users")
  public ResponseEntity<BaseResponse<List<GetMemberResponseBoardDTO>>> getMemberToBoard(
      @PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    List<GetMemberResponseBoardDTO> ResponseDTO = boardService.getMemberToBoard(boardId,
        userDetails.getUser().getId());

    return ResponseEntity.status(HttpStatus.OK).body(
        BaseResponse.of(GET_MEMBER_BOARD, ResponseDTO));

  }


  // 보드 상세 조회
  @GetMapping("/{boardId}")
  public ResponseEntity<BaseResponse<List<GetDetailResponseBoardDTO>>> getBoardId(
      @PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    List<GetDetailResponseBoardDTO> ResponseDTO = boardService.getBoardId(boardId,
        userDetails.getUser().getId());

    return ResponseEntity.status(HttpStatus.OK).body(
        BaseResponse.of(GET_DETAIL_BOARD, ResponseDTO));
  }

  //보드 멤버 탈퇴
  @DeleteMapping("/{boardId}/users")
  public ResponseEntity<BaseResponse<String>> deleteMemberToBoard(
      @PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    Long userId = userDetails.getUser().getId();

    boardService.deleteMemberToBoard(boardId,userId);


    return ResponseEntity.status(HttpStatus.OK).body(
        BaseResponse.of(DELETE_MEMBER_BOARD,
            String.format("%d번 보드 %d번 유저 탈퇴 완료", boardId, userId)));
  }



}
