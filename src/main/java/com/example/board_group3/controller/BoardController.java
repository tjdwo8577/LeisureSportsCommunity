package com.example.board_group3.controller;

import com.example.board_group3.dto.Board;
import com.example.board_group3.dto.LoginInfo;
import com.example.board_group3.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

// HTTP요청을 받아서 응답을 하는 컴포넌트, 스프링 부트가 자동으로 Bean으로 생성한다.
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //게시물 목록 보여주는 것
    //컨트롤러의 메소드가 리턴하는 문자열을 템플릿(리스트) 이름니다.
    // http://localhost:8080/ --> "list"라는 이름의 템플릿을 사용(forward)하여 화면에 출력.
    //list를 리턴한다는 것을 classpath:/templates/list.html을 사용한다.
    @GetMapping("/")
    public String list(@RequestParam(name = "page", defaultValue = "1") int page, HttpSession session, Model model) { // HttpSession, Model은 Spring이 자동으로 넣어준다.
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo);

        int totalCount = boardService.getTotalCount();
        List<Board> list = boardService.getBoards(page);
        int pageCount = totalCount / 10;
        if (totalCount % 10 > 0) { // 나머지가 있을경유 1page를 추가
            pageCount++;
        }

        int currentPage = page;
//        System.out.println("totalCount : " + totalCount);
//        for(Board board : list) {
//            System.out.println(board);
//        }
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);
        return "list";
    }

    @GetMapping("/board")
    public String board(@RequestParam("boardId") int boardId, Model model) {
        System.out.println("boardId : " + boardId);

        //id에 해당하는 게시물을 읽어온다
        //id에 해당하는 게시물의 조회수도 1증가한다.

        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "board";
    }

    @GetMapping("/writeForm")
    public String writeForm(HttpSession session, Model model) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 리다이렉트
            return "redirect:/loginform";
        }

        model.addAttribute("loginInfo", loginInfo);

        return "writeForm";
    }

    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session

    ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
        // 로그인한 사용자만 글을써야한다.
        // 세션에서 로그인한 정보를 읽어들인다. 로그인 하지 않았다면 리스트보기로 자동 이동 시킨다.
        // 로그인한 회원 정보 + 제목, 내영을 저장한다.

        boardService.addBoard(loginInfo.getUserId(), title, content);

        return "redirect:/"; //리스트 보기로 리다이렉트한다.

    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session

    ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }
        // 이 글의 주인과 로그인한 사용자의 id가 같은가 ?

        //loginInfo.getUserId() 사용자가 쓴 글일때만 삭제 가능한다.
        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else { // 관리자 권한이 아니라면 글 작성자만 삭제 할수 있게 해주는 조건문
            boardService.deleteBoard(loginInfo.getUserId(), boardId);
        }
        return "redirect:/"; //리스트 보기로 redirect
    }

    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId") int boardId, Model model, HttpSession session) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) { //세션에 로그인 정보다 없어서 /loginform으로 redirect
            return "redirect:/loginform";
        }
        // boardId에 대항하는 정보를 읽어와서 updateform 템플렛에게 전달
        Board board = boardService.getBoard(boardId, false);
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";
    }

    @PostMapping("/update")
    public String update(@RequestParam("boardId") int boardId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession session
                        ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) { //세션에 로그인 정보다 없어서 /loginform으로 redirect
            return "redirect:/loginform";
        }

        Board board = boardService.getBoard(boardId, false);
        // board User id 랑 login user id 가 다르다면 수정할수 없다
        if(board.getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId=" + boardId; // 글보기로 바로 이동하기
        }
        // boardId에 해당하는 글의 제목과 내용을 수정한다. 단, 글의 주인만 수정가능
        boardService.updateBoard(boardId, title, content);
        return  "redirect:/board?boardId=" + boardId; // 수정된 글 보기로 redirect
    }
}
