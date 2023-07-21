package com.example.board_group3.service;

import com.example.board_group3.dao.CommentDao;
import com.example.board_group3.dto.Board;
import com.example.board_group3.dto.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentDao commentDao;

    @Transactional
    public void addComment(int boardId, String content, String name) {
        commentDao.addComment(boardId, content, name);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByBoardId(int boardId) {
        return commentDao.getCommentsByBoardId(boardId);
    }


    //게시글 작성자 삭제 기능
    @Transactional
    public void deleteComment(int userId, int boardId) {
        Board board = commentDao.getComment(boardId);
        if(board.getUserId() == userId) {
            commentDao.deleteComment(boardId);
        }
    }

    @Transactional
    public void deleteComment( int boardId) { // 조건없이 관리자 권한으로 게시글 삭제
        commentDao.deleteComment(boardId);
    }}
