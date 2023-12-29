package com.example.collaborativetools.board.service;

import static com.example.collaborativetools.global.constant.ErrorCode.*;

import com.example.collaborativetools.board.dto.CreateBoardDTO;
import com.example.collaborativetools.board.dto.CreateBoardDTO.Response;
import com.example.collaborativetools.board.dto.GetMemberResponseBoardDTO;
import com.example.collaborativetools.board.dto.GetResponseBoardDTO;
import com.example.collaborativetools.board.dto.InviteRequestBoardDTO;
import com.example.collaborativetools.board.dto.UpdateBoardDTO;
import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.board.repository.BoardRepository;
import com.example.collaborativetools.global.constant.UserRoleEnum;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.user.repository.UserRepository;
import com.example.collaborativetools.userboard.entity.UserBoard;
import com.example.collaborativetools.userboard.repository.UserBoardRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final UserRepository userRepository;
  private final UserBoardRepository userBoardRepository;

  public CreateBoardDTO.Response createBoard(CreateBoardDTO.Request request, Long userId) {
    User user = findUser(userId);

    Board board = request.toEntity();
    boardRepository.save(board);

    UserBoard userBoard = UserBoard.createUserBoard(board, user, UserRoleEnum.ADMIN);
    userBoardRepository.save(userBoard);

    return Response.of(board);
  }


  public List<GetResponseBoardDTO> getBoard(Long userId) {
    User user = findUser(userId);
    List<UserBoard> userBoardList = user.getUserBoardList();

    return userBoardList.stream().map(userBoard -> GetResponseBoardDTO.of(userBoard.getBoard()))
        .toList();
  }

  @Transactional
  public UpdateBoardDTO.Response updateBoard(Long boardId, UpdateBoardDTO.Request request,
      Long userId) {

    UserBoard userBoard = findUserBoard(boardId, userId, "Yes");

    Board board = userBoard.getBoard();

    board.updateBoard(request.title(), request.desc(), request.backgroundColor());

    return UpdateBoardDTO.Response.of(board);
  }

  public void deleteBoard(Long boardId, Long userId) {
    UserBoard userBoard = findUserBoard(boardId, userId, "Yes");

    Board board = userBoard.getBoard();

    boardRepository.delete(board);
  }

  public void inviteMemberToBoard(Long boardId, InviteRequestBoardDTO request, Long userId) {
    //초대 받으려는 유저가 존재하는지
    User inviteUser = userRepository.findByUsername(request.username()).orElseThrow(() ->
        new ApiException(NOT_FOUND_USER_EXCEPTION));

    // 초대 하려는 유저가 해당 보드에 참여중인지 확인
    UserBoard userBoardCheck = findUserBoard(boardId, userId, "No");

    // 초대 받는 유저 해당 보드에 참여
    UserBoard userBoard = UserBoard.createUserBoard(userBoardCheck.getBoard(), inviteUser,
        UserRoleEnum.USER);
    userBoardRepository.save(userBoard);
  }

  public List<GetMemberResponseBoardDTO> getMemberToBoard(Long boardId, Long userId) {
    // 멤버 조회 하려는 보드에 현재 로그인중 유저가 보드에 참여중인지
    UserBoard userBoardCheck = findUserBoard(boardId, userId, "No");

    //1.첫번째 방법
    //보드 엔티티의 UserBoard리스트 
    List<UserBoard> userBoardList = userBoardCheck.getBoard().getUserBoardList();
    return userBoardList.stream()
        .map(userBoard -> GetMemberResponseBoardDTO.of(userBoard.getUser())).toList();

    //2.두번째 방법


  }


  private UserBoard findUserBoard(Long boardId, Long userId, String adminCheck) {
    UserBoard userBoard = userBoardRepository.findByUserIdAndBoardId(userId, boardId);

    if (userBoard == null) {
      throw new ApiException(NOT_FOUND_USER_BOARD_EXCEPTION);
    }
    if (adminCheck.equals("Yes") && userBoard.getRole().equals(UserRoleEnum.USER)) {
      throw new ApiException(NO_BOARD_AUTHORITY_EXCEPTION);
    }

    return userBoard;
  }


  private User findUser(Long userId) {
    return userRepository.findById(userId).orElseThrow(() ->
        new ApiException(NOT_FOUND_USER_EXCEPTION));
  }

  private Board findBoard(Long boardId) {
    return boardRepository.findById(boardId).orElseThrow(() ->
        new ApiException(NOT_FOUND_BOARD_EXCEPTION));
  }


  public boolean isUserInvited(Long boardId, Long userId) {
    Optional<Board> boardOptional = boardRepository.findById(boardId);
    Optional<User> userOptional = userRepository.findById(userId);

    if (boardOptional.isPresent() && userOptional.isPresent()) {
      Board board = boardOptional.get();

      return board.getUserBoardList().stream().anyMatch(userBoard -> userBoard.getUser().getId().equals(userId));
    }
    return false;
  }
}
