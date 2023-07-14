package com.example.board_group3.dao;

import com.example.board_group3.dto.Comment;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class CommentDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsertOperations insertComment;

    public CommentDao(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertComment = new SimpleJdbcInsert(dataSource)
                .withTableName("comment")
                .usingGeneratedKeyColumns("comment_id");
    }

    @Transactional
    public void addComment(int boardId, String content, String name) {
        Comment comment = new Comment();
        comment.setBoardId(boardId);
        comment.setContent(content);
        comment.setName(name);
        comment.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(comment);
        insertComment.execute(params);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByBoardId(int boardId) {
        String sql = "SELECT * FROM comment WHERE board_id = :boardId";
        RowMapper<Comment> rowMapper = BeanPropertyRowMapper.newInstance(Comment.class);
        return jdbcTemplate.query(sql, Map.of("boardId", boardId), rowMapper);
    }
}
