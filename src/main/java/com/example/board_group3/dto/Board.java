package com.example.board_group3.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;

@Getter // private 변수선언 했기때문에 외부 접근을 못하게 때문에 @Getter, @Setter 를 설정 해야한다.
@Setter
@ToString
public class Board {
    private int boardId;
    private String title;
    private String content;
    private String name; // join 한 table값 출력 을 위한 name 추가
    private int userId;
    private LocalDateTime regdate;
    private int viewCnt;
}
