package com.example.board_group3.dao;

import com.example.board_group3.dto.Board;
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

    @Transactional(readOnly = true)
    public Board getComment(int boardId) {
        // 1건 또는 0건이 나오는 쿼리
        String sql = "select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name, b.content from board b, user u where b.user_id = u.user_id and b.board_id = :boardId"; // = :boardId 매번 변하기 때문에 이렇게 작성
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        Board board = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return board;
    }

    public void deleteComment(int boardId) {
        String sql = "delete from comment where comment_id = :comment_Id";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }
}
