package com.example.board_group3.service;

import com.example.board_group3.dao.BoardDao;
import com.example.board_group3.dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor //final 붙은 필드만 초기화 하는 생성자가 자동으로 만들어진다.
public class BoardService {
    private final BoardDao boardDao;

    @Transactional
    public void addBoard(int userId, String title, String content) { // userId, title, content 정보를 가지고 DB에 저장, DB는 dao를 이용한다. 게시판 dao가 없으시 생성
        boardDao.addBoard(userId, title, content);

    }

    @Transactional(readOnly = true) // select 할때는 read only true로 넣어줘야 성능이 좋아진다.
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
        //id에 해당하는 게시물을 일어온다.
        //id에 대항하는 게시물 조회수도 1증가한다. 그래서 readonly = true를 사용할수 없다.
        Board board = boardDao.getBoard(boardId); // boardDao에서 getboard중 boardId를 가져온다.
        if(updateViewCnt) {
            boardDao.updateViewCnt(boardId); // BoardDao에 upadtaeviewcnt 메소드 작성하기
        }
        return board;
    }

    //게시글 작성자 삭제 기능
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


    @Transactional(readOnly = true) // 원래꺼
    public List<Board> searchBoards(String keyword) {
        return boardDao.searchBoards(keyword);
    }
}
