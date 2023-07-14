package com.example.board_group3.service;

import com.example.board_group3.dao.BoardDao;
import com.example.board_group3.dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardDao boardDao;

    @Transactional
    public void addBoard(int userId, String title, String content) {
        boardDao.addBoard(userId, title, content);

    }

    @Transactional(readOnly = true) // select 할때는
    public int getTotalCount() {
        return boardDao.getTotalCount();
    }

    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        return boardDao.getBoards(page);
    }

    @Transactional
    public Board getBoard(int boardId) {
        return getBoard(boardId, true);
    }

    // updateViewCnt 가 true 면 글의 조회수를 증가, false면 글의 조회수를 증가하지 않는다.
    @Transactional
    public Board getBoard(int boardId, boolean updateViewCnt) {
        Board board = boardDao.getBoard(boardId);
        if(updateViewCnt) {
            boardDao.updateViewCnt(boardId);
        }
        return board;
    }

    @Transactional
    public void deleteBoard(int userId, int boardId) {
        Board board = boardDao.getBoard(boardId);
        if(board.getUserId() == userId) {
            boardDao.deleteBoard(boardId);
        }
    }

    @Transactional
    public void deleteBoard( int boardId) { // 조건없이 관리자 권한으로 게시글 삭제
        boardDao.deleteBoard(boardId);
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        boardDao.updateBoard(boardId, title, content);
    }
}
