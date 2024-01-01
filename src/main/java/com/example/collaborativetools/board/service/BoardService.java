package com.example.collaborativetools.board.service;

import static com.example.collaborativetools.global.constant.ErrorCode.*;

import com.example.collaborativetools.board.dto.CardInColumnDTO;
import com.example.collaborativetools.board.dto.CreateBoardDTO;
import com.example.collaborativetools.board.dto.CreateBoardDTO.Response;
import com.example.collaborativetools.board.dto.GetDetailResponseBoardDTO;
import com.example.collaborativetools.board.dto.GetMemberResponseBoardDTO;
import com.example.collaborativetools.board.dto.GetResponseBoardDTO;
import com.example.collaborativetools.board.dto.InviteRequestBoardDTO;
import com.example.collaborativetools.board.dto.UpdateBoardDTO;
import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.board.repository.BoardRepository;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.column.repository.ColumnRepository;
import com.example.collaborativetools.global.constant.UserRoleEnum;
import com.example.collaborativetools.global.exception.ApiException;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.user.repository.UserRepository;
import com.example.collaborativetools.userboard.entity.UserBoard;
import com.example.collaborativetools.userboard.repository.UserBoardRepository;

import java.util.ArrayList;
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

        List<UserBoard> userBoardList = userBoardRepository.findByUserId(userId);

        return userBoardList.stream().map(userBoard -> GetResponseBoardDTO.of(userBoard.getBoard()))
                .toList();
    }

    @Transactional
    public UpdateBoardDTO.Response updateBoard(Long boardId, UpdateBoardDTO.Request request,
                                               Long userId) {

        UserBoard userBoard = findUserBoard(userId, boardId, "Yes");

        Board board = userBoard.getBoard();

        board.updateBoard(request.title(), request.desc(), request.backgroundColor());

        return UpdateBoardDTO.Response.of(board);
    }

    public void deleteBoard(Long boardId, Long userId) {
        UserBoard userBoard = findUserBoard(userId, boardId, "Yes");

        Board board = userBoard.getBoard();

        boardRepository.delete(board);
    }

    public void inviteMemberToBoard(Long boardId, InviteRequestBoardDTO request, Long userId) {
        //초대 받으려는 유저가 존재하는지
        User inviteUser = userRepository.findByUsername(request.username()).orElseThrow(() ->
                new ApiException(NOT_FOUND_USER_EXCEPTION));

        // 초대 하려는 유저가 해당 보드에 참여중인지 확인
        UserBoard userBoardCheck = findUserBoard(userId, boardId, "No");

        // 초대 받으려는 유저가 이미 해당 보드에 참여중인지
        for (UserBoard userBoard : userBoardCheck.getBoard().getUserBoardList()) {
            if (userBoard.getUser().getId().equals(inviteUser.getId())) {
                throw new ApiException(USER_ALREADY_JOINED_BOARD);
            }
        }

        // 초대 받으려는 유저 해당 보드에 참여 시키기
        UserBoard userBoard = UserBoard.createUserBoard(userBoardCheck.getBoard(), inviteUser,
                UserRoleEnum.USER);
        userBoardRepository.save(userBoard);
    }

    public List<GetMemberResponseBoardDTO> getMemberToBoard(Long boardId, Long userId) {
        // 멤버 조회 하려는 보드에 현재 로그인중 유저가 보드에 참여중인지
        UserBoard userBoardCheck = findUserBoard(userId, boardId, "No");

        List<UserBoard> userBoardList = userBoardCheck.getBoard().getUserBoardList();
        return userBoardList.stream()
                .map(userBoard -> GetMemberResponseBoardDTO.of(userBoard)).toList();
    }


    public List<GetDetailResponseBoardDTO> getBoardId(Long boardId, Long userId) {
        UserBoard userBoard = findUserBoard(userId, boardId, "No");

        Board board = userBoard.getBoard();

        List<GetDetailResponseBoardDTO> responseBoardDTOList = new ArrayList<>();

        List<Columns> columnsList = board.getColumnsList();

        //해당 보드에 컬럼들 정보 넣기
        for (Columns columns : columnsList) {
            List<CardInColumnDTO> cardInColumnDTOList = new ArrayList<>();

            // 해당 컬럼에 카드들 정보 넣기
            for (Card card : columns.getCards()) {
                CardInColumnDTO cardInColumnDTO = CardInColumnDTO.builder().cardId(card.getId())
                        .cardTitle(card.getTitle()).backgroundColor(card.getBackgroundColor()).sequence(card.getSequence())
                        .dueDate(card.getDueDate())
                        .build();
                cardInColumnDTOList.add(cardInColumnDTO);
            }
            cardInColumnDTOList.sort((o1, o2) -> o1.sequence()-o2.sequence());

            responseBoardDTOList.add(GetDetailResponseBoardDTO.of(columns, cardInColumnDTOList));
        }

        responseBoardDTOList.sort(((o1, o2) -> o1.sequence() - o2.sequence()));//순서 순으로 정렬

        return responseBoardDTOList;
    }

    public void deleteMemberToBoard(Long boardId, Long userId) {
        UserBoard userBoard = findUserBoard(userId, boardId, "No");

        if (userBoard.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new ApiException(ADMIN_NOT_UNREGISTER_BOARD);
        }

        userBoardRepository.deleteById(userBoard.getId());
    }


    private UserBoard findUserBoard(Long userId, Long boardId, String adminCheck) {
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
