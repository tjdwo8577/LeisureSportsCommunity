package com.example.board_group3.service;

import com.example.board_group3.dao.UserDao;
import com.example.board_group3.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 트랜젝션 단위로 실행될 메소드를 선언하고 있는 클래스
@Service // 스프링이 관리해주는 Bean이된다.
@RequiredArgsConstructor // lombok이 final 필드를 초기화하는 생성자를 자동을 생성한다.
public class UserService {
    //UserService에서는 UserDao를 사용해야 하기 때문에 UserDao를 injection받아야 한다. injection되는 Dao는 final로 선언, final로 선언된 것은 반드시 생성자 주입이 필요하다.
    private final UserDao userDao; // userDao라는 컴포넌트로 관리가 될려면 UserDao에서 @Repository로 해야한다. fianl 변수는 무조건 초기화 해줘야 한다.

    @Transactional // 보통 서비스에서는 @Transactional을 붙여서 하나의 트랜잭션으로 처리하게 한다.
    // Spring boot는 트랜잭션을 처리해주는 트랜잭션 관리자를 가지고 있다.
    public User addUser(String name, String email, String password) { // UserController에서 userReg에 받아들인 정보는 name, email password 이기때문에 괄호안에 3가지 값을 DB 저장하고,
                                                                        // 저장된 정보를 읽어들여서 반환하도록 User를 리턴한다.
        //트랜잭션이 시작한다.
        User user1 = userDao.getUser(email); // 이메일 중복 검사
        if (user1 != null) {
            throw new RuntimeException("이미 가입된 이메일 입니다.");
        }

        // 회원 정보가 호출될때는 userDao의 addUser메소드가 호출
        User user = userDao.addUser(email,name,password); // usrDao가 가지고 있는 3가지 메소드가 호출 addUser / email, name, password를 받아들이고, "회원정보 테이블에 저장했고" 하나의 트랜잭셕은로 auto increment로 증가한 아이디 값을 user객체를 린턴해주고
        userDao.mappingUserRole(user.getUserId()); // user가 가지고 있는 userId를 .getUserId()를 사용해서 "권한부여" ,
        return user; // 권한받은 user를 리턴한다.
        //트랜잭션 끝
    }

    @Transactional
    public User getUser(String email) { // 회원정보를 가져오는 기능이 필요해서 작성
        return userDao.getUser(email); // email에 해당하는 회원정보를 읽어오려면 dao가 필요하기때문에 userDao 에 getUser라는 메소드를 만들어줘야 한다.
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        return userDao.getRoles(userId);
    }
}
