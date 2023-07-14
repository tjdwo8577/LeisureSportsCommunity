package com.example.board_group3.service;

import com.example.board_group3.dao.CommentDao;
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
}
