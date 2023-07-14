package com.example.board_group3.service;

import com.example.board_group3.dao.UserDao;
import com.example.board_group3.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 트랜젝션 단위로 실행될 메소드를 선언하고 있는 클래스
// 스프링이 관리하는 Bean
@Service
@RequiredArgsConstructor // lombok이 final 필드를 초기화하는 생성자를 자동을 생성한다.
public class UserService {
    private final UserDao userDao; // fianl 변수는 무조건 초기화 해줘야 한다.

    /*
    //Spring이 UserService를 Bean으로 생성할때 생성자를 이용해 생성을 하는데, 이때 UserDao Bean이 있는지 보고
    //그 빈을 주입한다. 생성자 주입.
    public UserService(UserDao userDao){
        this.userDao = userDao; // userDao 를 초기화 해준다.
    }
    */

    //위에 주석처리 이유는 @RequiredArgsConstructor를 사용 하기 때문이다.



    // 보통 서비스에서는 @Transactional 을 붙여서 하나의 트랜젝션으로 처리하게 한다.
    // Spring boot는 트랜잭션을 처리해주는 트랜잭션 관리자를 가지고 있다.
    @Transactional
    public User addUser(String name, String email, String password) {
        //트랜잭션이 시작한다.
        User user1 = userDao.getUser(email); // 이메일 중복 검사
        if (user1 != null) {
            throw new RuntimeException("이미 가입된 이메일 입니다.");
        }

        User user = userDao.addUser(email,name,password);
        userDao.mappingUserRole(user.getUserId()); // 권한을 부여한다.
        return user;
        //트랜잭션 끝
    }

    @Transactional
    public User getUser(String email) {
        return userDao.getUser(email);
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        return userDao.getRoles(userId);
    }
}