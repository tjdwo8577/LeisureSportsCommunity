package com.example.board_group3.dao;

import com.example.board_group3.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


//SQL문 을 전문으로 하는 객체, 클래스 Dao
@Repository //Spring 이 관리하는 Bean 이 된다. @Repository 를 설정함으로서
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate; //Spring JDBC를 이용한 코드 작성, 사용하려면 jdbctemplate를 사용하는데 그중 NamedParameterJdbcTemplate를 사용
    private final SimpleJdbcInsertOperations insertUser; // Insert문을 작성하지 않고도 편리하게 Insert할수 있게 도와준다.

    // jdbcTemplate가 fianl변수로 선언되어 있기 때문에 초기화 를 해야한다.
    public UserDao(DataSource dataSource) { // 초기화를 하려면 DataSource가 필요한다.
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("user") // table이름은 user 테이블에다가 자동으로 insert해주는 것.
                .usingGeneratedKeyColumns("user_id"); // user 테이블에 user_id가 auto increment이기때문에 자동으로 증가하는 id를 설정할때 사용된다.
    }

    // Spring JDBC를 이용한 코드,

    @Transactional // Service가 아님에도 불구하고 INSERT 와 SELECT, 두개의 쿼리가 실행되기때문에 하나의 TRANSACTION 처리를 해야한다. 그래서 @Transactional을 사용한다.
    public User addUser(String email, String name, String password) {
        // Service에서 이미 트랜잭션이 시작했기 때문에, 그 트랜잭션에 묵어간다. 여기서 새로운 트랜잭션을 만들지 않고 이미 시작된 트랜잭션에 포함되서 하나의 로직으로 실행된다.
        //insert into user (email, name, password, regdate) values (:email, :name, :password, :regdate);
        //SELECT LAST_INSERT_ID();
        //INSERT INTO user_role( user_id, role_id) VALUES ( ?, 1);

        // user값을 넣어주면 User가 가지고 잇는 객체를 getter method를 사용해서 column규칠에 맞게 설정
        User user = new User(); //BeanPropertySqlParameterSource의 괄호안에 들어갈 user생성
        user.setName(name); // 웹으로부터 입력받은 name을 테이블 칼럼 넣어준다
        user.setEmail(email); // 웹으로부터 입력받은 email 테이블 칼럼 넣어준다
        user.setPassword(password); // 웹으로부터 입력받은 password 테이블 칼럼 넣어준다
        user.setRegdate(LocalDateTime.now()); // 새로운 date 객체 생성하고 문자열을 변환해서 넣어준다.
        SqlParameterSource params = new BeanPropertySqlParameterSource(user); //DTO의 값을 SqlParameterSource에 자동으로 넣어주는 객체가 BeanPropertySqlParameterSource는이고 ( DTO값을)괄호안데 넣는다.
        Number number = insertUser.executeAndReturnKey(params); // insert를 실행하고, 자동으로 생성된 id를 가져온다. / 리턴받은것을 Number라는 객체로 리턴하게 되는데
        int userId = number.intValue(); // user_id 가 int이기때문에
        user.setUserId(userId); // userId를 위에 있는 user에다가 설정을해서 return user하게 되면 isnert된 전체 데이터를 user가 가지고 리턴해주고 리턴 받은 값을UserSrvice의 User user값이 생겼고, user를 사용해서 mappingUserRoledp값을 저장하고
        return user;

    }
    @Transactional
    public void mappingUserRole(int userId) { // userId 값을 받아들여서 userId한테 권한을 부여해 줘라.
        // Service 에서 이미 트랜잭션이 시작했기 때문에, 그 트랜잭션에 포함된다.
        String sql = "insert into user_role( user_id, role_id) values (:userId, 1)"; // Simple jdbcinsertopertaion사용해도 되지만 직접 sql문을 사용해서 사용한다. "userId는 외부로 밭은 값이고 role_id는 1인 값으로 저장한다."
        SqlParameterSource params = new MapSqlParameterSource("userId", userId); //map객체를많이 사용하기때문에 New MapSqlParemeterSource를 사용한다. values()안에 userId하나 들어가기 개문에, userId 하나믄 들어간다.
                                                                                            // 여러개일때는 Map<Stirng, ?>이 들어간다.
        jdbcTemplate.update(sql, params); // NamedParameterJdbcTemplate 사용하기위해서 "jdbcTemplate를 사용하고" insert, delete, update 는 jdbcTempalte.update를 사용한다.
                                         // jdbcTemplate.update()괄호안에 첫번재 인자는 sql,두번째 인자에는 sqlparametersource인 values(:userid) 와 같은것이 와야한다.
    }

    @Transactional
    public User getUser(String email) {
        try{
            // user_id => setUserId 로 값을 설정, email ==> setEmail로 값을 설정 ......
            String sql = "select user_id, email, name, password, regdate from user where email = :email";
            SqlParameterSource params = new MapSqlParameterSource("email", email); // "email" 이 위에 있는 email = :email 이고, 뒤에 email은 String eamil이다.
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class); // rowMapper가 dto와 동일한 규칠을 가지고 있다면 BeanPropertyMapper를 사용할수 있다.
            //RowMapper<User> 는 우리가 User가 user 테이블의 정보를 가지고, User.class를 넣어주면 이 클래스 정보들을 통해서 각각의 column값(user_id, email,name,password,regdate)들을 mapping해주는 rowMapper라는게 나온다.
            User user = jdbcTemplate.queryForObject(sql, params, rowMapper); // email의 정보는 있든지 없든지 둘중의 하나이기때문에, queryForObject를 사용(sql, paramertersource는 =:값 뒤에 있는걸 말한다., rowMapper는 select할때 들어간 각각의 column 들을 어떤dto에 담아줄껀인지를 담당하는게 rowmapper이다.)
            return user;
        } catch(Exception ex) { // queryforobject는 thorws exception 이발생한다. 만약 email이 없다면 --> 설정을 위해서UserController파일 체크하기
            return null;
        }

    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        String sql = "select r.name from user_role ur, role r where ur.role_id = r.role_id and ur.user_id = :userId";
        List<String> roles = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) ->{ // rs 는 resultSet
            return rs.getString(1);
        });
        return roles;
    }
}