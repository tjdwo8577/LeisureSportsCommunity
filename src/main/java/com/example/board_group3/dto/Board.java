package com.example.board_group3.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Board {
    private int boardId;
    private String title;
    private String content;
    private String name;
    private int userId;
    private LocalDateTime regdate;
    private int viewCnt;
}
