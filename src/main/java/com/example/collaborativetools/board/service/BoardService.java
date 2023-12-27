package com.example.collaborativetools.board.service;

import static com.example.collaborativetools.global.constant.ErrorCode.*;

import com.example.collaborativetools.board.dto.CreateBoardDTO;
import com.example.collaborativetools.board.dto.CreateBoardDTO.Request;
import com.example.collaborativetools.board.dto.CreateBoardDTO.Response;
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

    UserBoard userBoard = UserBoard.createUserBoard(board, user,UserRoleEnum.ADMIN);
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

    UserBoard userBoard = findUserBoard(boardId, userId);

    Board board = userBoard.getBoard();

    board.updateBoard(request.title(), request.desc(), request.backgroundColor());

    return UpdateBoardDTO.Response.of(board);
  }

  public void deleteBoard(Long boardId, Long userId) {
    UserBoard userBoard = findUserBoard(boardId, userId);

    Board board = userBoard.getBoard();

    boardRepository.delete(board);
  }

  public void inviteMemberToBoard(Long boardId, InviteRequestBoardDTO request, Long userId) {


  }


  private UserBoard findUserBoard(Long boardId, Long userId) {
    UserBoard userBoard = userBoardRepository.findByUserIdAndBoardId(boardId, userId);

    if (userBoard == null) {
      throw new ApiException(NOT_FOUND_USER_BOARD_EXCEPTION);
    }
    if (userBoard.getRole().equals(UserRoleEnum.USER)) {
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

}
