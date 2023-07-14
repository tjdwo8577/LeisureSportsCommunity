package com.example.board_group3.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

//private를 사용하게 되면 Getter 와 Setter 메소드가 필요한데 우리는 lombok 을 사용하기 때문에 annotation을 사용해서 자동으로 Getter Setter 메소드가 생성된것이다.(눈에는 보이지 않지만)

@Setter
@Getter
@NoArgsConstructor // 기본생정자가 자동으로 생성된다.(아무것도 없는 기본생성자가 자동으로 생성)
@ToString // Object의 toStirng() 메소드를 자동으로 만들어준다.
public class User {

    // 아래는 user table에 각각의 column 값 이다.
    private int userId;
    private String email;
    private String name;
    private String password;
    private LocalDateTime regdate; // 원래는 날짜 type으로 읽어온 후 문자열로 변환하는 과정을 가져야 좋지만 편하게 하기 위해선

}
