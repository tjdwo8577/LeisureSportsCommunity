package com.example.board_group3.controller;

//import com.example.board_group3.dao.CommentDao;
//import com.example.board_group3.dao.SubcommentDao;
import com.example.board_group3.dto.Board;
//import com.example.board_group3.dto.Comment;
import com.example.board_group3.dto.Comment;
import com.example.board_group3.dto.LoginInfo;
import com.example.board_group3.service.BoardService;
import com.example.board_group3.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

// HTTP요청을 받아서 응답을 하는 컴포넌트, 스프링 부트가 자동으로 Bean으로 생성한다.
@Controller
@RequiredArgsConstructor // lombok에서 final로 받은 생성자를 자동으로 초기화 해준다.
public class BoardController {

    private final BoardService boardService; // BoardService가 BoardController에서도 사용되어야 하니깐 선언을 해야한다.
    private final CommentService commentService;

    //게시물 목록 보여주는 것
    //컨트롤러의 메소드가 리턴하는 문자열을 템플릿(리스트) 이름니다.


    //게시물 목록을 보여주는것
    @GetMapping("/") //localhost:8080 입력하면 처음 보여주는 화면이다.
    public String list(@RequestParam(name = "page", defaultValue = "1") int page, HttpSession session, Model model) { // HttpSession, Model은 Spring이 자동으로 넣어준다.
        // ("/") 라는 요청을 받아들이면 Main Page로 보내주는 것

        // @RequestParam(name = "page", defaultValue = "1") int page 는 페이지네이션 이다.

        // Model값을 닫으면 teampltae(html)에서 사용할수 있다. 어떤 이름으로? loginfInf라는 이름으로 사용할수 있다.
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo); // Template에게 loginInfo라는 키값으로 객체를 list로 넘기게 된다.

        //목록 가져오기
        int totalCount = boardService.getTotalCount(); // 전체 글수 가져오기
        List<Board> list = boardService.getBoards(page); //page가 1페이지, 2페이지,3페이지,4페이지,5페이지 등등등.....
        int pageCount = totalCount / 10; //총페이지 수 구하기

        if (totalCount % 10 > 0) { // 나머지가 있을경유 1page를 추가
            pageCount++;
        }
        int currentPage = page;

        //model에 담아서 보낸다.
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);
        return "list";  //list라는 템플릿으로 리턴한다.
        // http://localhost:8080/ --> "list"라는 이름의 템플릿을 사용(forward)하여 화면에 출력.
        //list를 리턴한다는 것을 classpath:/templates/list.html을 사용한다.
    }

    @GetMapping("/board") //특정 게시글을 클릭했을때 보여지는 페이지 설정(상세보기)
    public String board(@RequestParam("boardId") int boardId, Model model) { // @RequestParam(boardId), int boardId localhost:8080/board?baordId= 뒤에 값이 int에 있는 baordId값이 붙는다.
        //id에 해당하는 게시물을 읽어오는 과정
        //id에 해당하는 게시물의 조회수도 1증가해야한다.
        Board board = boardService.getBoard(boardId); //board service에서 작성
        List<Comment> comments = commentService.getCommentsByBoardId(boardId);
        model.addAttribute("board", board);
        model.addAttribute("comments",comments);
        return "board"; // 특정 게시글을 클릭했을때 board.html로 넘겨준다.
    }

    @GetMapping("/writeForm") // 마찬가지로 list.html 에서 <a href="/writeForm">로 클릭 할수 있는 버튼을 생성해서 @GetMapping을 사용한다.
    public String writeForm(HttpSession session, Model model) { // session에서 정보 읽어들이고, 읽어들인 정보를 writeForm에 전달
        //세션에서 로그인한 정보를 읽어들인다.
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        // 세션에 로그인 정보가 없으면 /loginform으로 리다이렉트
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
        model.addAttribute("loginInfo", loginInfo); // 로그인 값을 model에 담아서 writeForm에 전달 loginInfo객체를 전달
        return "writeForm";
    }

    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title, //writeForm.html 에서 사용자가 입력한 값이 글 제목 과 글 내용이기때문에 @RequestParam으로 title 과 contents를 받아들인다.
            @RequestParam("content") String content,
            HttpSession session) {
        // 로그인한 사용자만 글을써야한다.
        // 세션에서 로그인한 정보를 읽어들인다. 로그인 하지 않았다면 리스트보기로 자동 이동 시킨다.
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
        // 로그인한 회원 정보 + 제목, 내용을 저장한다.
        boardService.addBoard(loginInfo.getUserId(), title, content); // 사용자가 입력한 제목과 내용을 저장한다.
        return "redirect:/"; //게시글 작성 후 저장되면 화면은 다시 리스트 보기로 리다이렉트한다.

    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session

    ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo"); // 로그인한 정보를 loginInfo를 읽어온다.
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

    @PostMapping("/update") //input 에 input tag에 name을 boardId를 @RequestParam에서 boardId를 받겠다 라는것이다.
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



    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<Board> searchResults = boardService.searchBoards(keyword);
        model.addAttribute("searchResults", searchResults);
        return "search-results";
    }


    //댓글
    @PostMapping("/writeComment")
    public String writeComment(
            @RequestParam("boardId") int boardId,
            @RequestParam("content") String content,
            HttpSession session
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        commentService.addComment(boardId, content, loginInfo.getName());
        return "redirect:/board?boardId=" + boardId;
    }

}
