package com.example.board_group3.controller;
import com.example.board_group3.dto.Board;
import com.example.board_group3.dto.Comment;
import com.example.board_group3.dto.LoginInfo;
import com.example.board_group3.dto.WeatherResponse;
import com.example.board_group3.service.BoardService;
import com.example.board_group3.service.CommentService;
import com.example.board_group3.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final WeatherService weatherService;
    // OpenWeatherMap API Key 값을 주입받습니다.
    @GetMapping("/")
    public String list(@RequestParam(name = "page", defaultValue = "1") int page, HttpSession session, Model model) {

        String[] cities = {"Seoul", " Gyeonggi-do", "Busan", "Gangwon-do", "Chungcheongbuk-do", "Jeollabuk-do", "Chungcheongnam-do",
                "Jeollanam-do", "Gyeongsangnam-do", "Gyeongsangbuk-do", "Jeju-do"}; // Add more cities as needed
        WeatherResponse[] weather = new WeatherResponse[cities.length];

        for (int i = 0; i < cities.length; i++) {
            weather[i] = weatherService.getWeather(cities[i]);
        }

        model.addAttribute("weather", weather);

        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo);
        int totalCount = boardService.getTotalCount();
        List<Board> list = boardService.getBoards(page);
        int pageCount = totalCount / 10;

        if (totalCount % 10 > 0) {
            pageCount++;
        }
        int currentPage = page;
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);
        return "list";
    }

    @GetMapping("/board")
    public String board(@RequestParam("boardId") int boardId, Model model) {
        Board board = boardService.getBoard(boardId);
        List<Comment> comments = commentService.getCommentsByBoardId(boardId);
        model.addAttribute("board", board);
        model.addAttribute("comments",comments);
        return "board";
    }
    @GetMapping("/writeForm")
    public String writeForm(HttpSession session, Model model) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
        model.addAttribute("loginInfo", loginInfo);
        return "writeForm";
    }

    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
        boardService.addBoard(loginInfo.getUserId(), title, content);
        return "redirect:/";

    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session
    ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else {
            boardService.deleteBoard(loginInfo.getUserId(), boardId);
        }
        return "redirect:/"; //리스트 보기로 redirect
    }

    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId") int boardId, Model model, HttpSession session) {
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
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
        if(loginInfo == null) {
            return "redirect:/loginform";
        }

        Board board = boardService.getBoard(boardId, false);
        if(board.getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId=" + boardId;
        }
        boardService.updateBoard(boardId, title, content);
        return  "redirect:/board?boardId=" + boardId;
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