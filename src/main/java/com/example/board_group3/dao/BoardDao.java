package com.example.board_group3.dao;

import com.example.board_group3.dto.Board;
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

@Repository //Component이고, Component는 컨테이너가 관리하는 Bean이다.
public class BoardDao {
    private final NamedParameterJdbcTemplate jdbcTemplate; // sql 문에 value 에 ? 대신에 property명을 쓸수 있다.
    private final SimpleJdbcInsertOperations insertBoard; // insert를 쉽게 하도록 도와주는 인터페이스

    // 생성자 주입, 스프링이 자동으로 HikariCP Bean을 주입한다.
    public BoardDao(DataSource dataSource) {// 생성자에 파라미터를 넣어주면 스프링 부트가 자동으로 주입한다.(생성자는 BoardDoa이고 주입된 파라미터는 DataSource dataSource이다.
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource); // 초기화 완료
        insertBoard = new SimpleJdbcInsert(dataSource)
                .withTableName("board") //withTableName이 사용하는 table이름은 baord라는 테이블이다.
                .usingGeneratedKeyColumns("board_id"); // 자동으로 증가되는 id를 설정할때.
    }
    @Transactional
    public void addBoard(int userId, String title, String content) {
        Board board = new Board(); // 한건의 baord를 가질수 있는 board객체 생성
        board.setUserId(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(board); // board dto이다, SqlParameterSource를 구현하고 있는 구현체가 BeanPropertySqlParameterSource라는 개체이고()괄호안에는 board객체가 들어간다.
        insertBoard.execute(params);
    }

    @Transactional(readOnly = true)
    public int getTotalCount() {
        String sql = "select count(*) as total_count from board"; // 무조건 1건의 데이터가 나온다. , as total_count가 Integer totalCount가 된것인다.
        Integer totalCount = jdbcTemplate.queryForObject(sql, Map.of(), Integer.class); // queryForObject는 1건의 데이터를 가져올때 사용한다. , Map.of()는 비어있는 map을 하나 리턴한다.
        return totalCount.intValue(); // .intValue 하면 정수값을 리턴해준다.
    }

    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        // start 는 0,10,20,30,40, 50 는 1페이지, 2페이지 3, 4, 5페이지
        int start = (page - 1) *10; // 현재 페이지에서 -1 해서 10을 곱해준다. page가 1이면, 1-1*10 = 0, page가 2 ==> 2-1 = 1*10 = 10
        String sql = "select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name from board b, user u where b.user_id = u.user_id order by board_id desc limit :start, 10";

        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class); //
        List<Board> list = jdbcTemplate.query(sql, Map.of("start", start), rowMapper); // 여러건의 내용을 구하는거기때문에 .query를 사용한다.
        return list;

    }
    // 게시글 상세보기에 코드
    @Transactional(readOnly = true)
    public Board getBoard(int boardId) {
        // 1건 또는 0건이 나오는 쿼리
        String sql = "select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name, b.content from board b, user u where b.user_id = u.user_id and b.board_id = :boardId"; // = :boardId 매번 변하기 때문에 이렇게 작성
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        Board board = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return board;
    }

    @Transactional
    public void updateViewCnt(int boardId) {
        String sql = "update board\n" +
                "set view_cnt = view_cnt + 1\n" +
                "where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId)); // .update는(메소드)는 insert,update, delete sql문을 실행할 때 사용한다.
                                                                 // Map.of("boardId", boardId) 는 where 절 뒤에 들어오는 정보를 입력한다.
    }

    @Transactional
    public void deleteBoard(int boardId) {
        String sql = "delete from board where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        String sql = "update board\n" +
                "set title = :title , content = :content\n" +
                "where board_id = :boardId";

        Board board = new Board();
        board.setBoardId(boardId);
        board.setTitle(title);
        board.setContent(content);
        SqlParameterSource params = new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql, params);

        //jdbcTemplate.update(sql, Map.of("boardId", boardId, "title", title, "content", content));
    }



    @Transactional(readOnly = true)
    public List<Board> searchBoards(String keyword) {
        String sql = "SELECT * FROM board WHERE title LIKE :keyword OR content LIKE :keyword";
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        List<Board> searchResults = jdbcTemplate.query(sql, Map.of("keyword", "%" + keyword + "%"), rowMapper);
        return searchResults;
    }

}
