package com.example.board_group3.controller;

import com.example.board_group3.dto.LoginInfo;
import com.example.board_group3.dto.User;
import com.example.board_group3.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.sql.SQLOutput;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // http://localhost:8080/userRegForm
    //classpath:/templates/userRefForm.html 파일 생성하기
    @GetMapping("/userRegForm") // /userRegForm 요청이 오면 회원가입 폼이 보인다.
    public String userRegForm() {
        return "userRegForm";
    }

    @PostMapping("/userReg") //localhost:8080/userRegForm에서 회원가입후 /userReg로 넘어오게 되는데
    //userRegForm에서 작성한 값들을 /userReg에 post방식으로 넘어오는 값들을 @RequestParam으로 받아들인다.
    public String userReg(
            @RequestParam("name") String name, // name으로 오는것은 name으로 받고
            @RequestParam("email") String email, // email으로 오는것은 email으로 받고
            @RequestParam("password") String password // pswd으로 오는것은 pswd으로 받고
    ) {
        userService.addUser(name, email, password); // userService.method이름(받아들인 정보 즉 이름, 이메일 비밀번호)를 넣어주면 회원정보가 저장되고 welcome페이지 리턴

        // 어떤 기능이 필요한지 미리 알 수 있다.
        // 회원 정보를 저장한다. 동사는 메소드, 메소드만 선언하고 있는것은 인터페이스

        return "redirect:/welcome"; // redirect 를 통해 브라우저에게 자동으로 http://localhost:8080/welcome으로 이동
    }

    @GetMapping("/welcome") //http://localhost:8080/welcome 으로 이동 해주는 것 왜냐하면, 회원가입후 넘어가는 페이지는
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/loginform") // welcome.html에서 <a href="/loginform">로그인</a> 이라는 링크를 만들어 놨기때문에 loginform의 요청이 들어오면 loginform.html으로 보내준다.
    public String loginform() {
        return "loginform";
    }

    @PostMapping("/login") //loginform.html에서 post방식으로 입력한 정보를 받았기 때문에 @PostMapping을 이용해서 @RequestParam에 전달받은 정보들을 받아들이고 redirect:/로 이동시킨다.
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession //  session에 저장하고 싶으면, controller 메소드 뒤에 HttpSession이라는 객체를 적어준다. Spring이 자동으로 session을 처리하는 HttpSession객체를 넣어준다.
            //session은 현재 부라우저 사용자만 접근 가능하고, 각각의 browser 마다 다르게 session이 만들어지게 된다.
    ) {
        //email에 해당하는 회원 정보를 읽어온 후
        // 아이디 암호가 맞다면 세션에 회원정보를 저장한다.
        try {
            User user = userService.getUser(email);
            if(user.getPassword().equals(password)) { // 외부로 받아들인 비밀번호(@RequestParam("password")가 같은지 확인 한다.
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName()); // 암호가 같으면 loginInfo객체에 (getUserId, getEmail, getName)저장하고 그리고 session에 저장한다.

                // 권환정보를 읽어와서 loginInfo에 추가한다.
                List<String> roles = userService.getRoles(user.getUserId());
                loginInfo.setRoles(roles);


                httpSession.setAttribute("loginInfo", loginInfo); // 첫번째 파라미터가 key값, 두번째 파라미터가 value값


            }else{ // 암호가 틀릴때 발생하는 Exception 이고, 마찬가지로 "redirect:/loginform?error=true" 로 넘어간다.
                throw new RuntimeException("암호가 일치하지 않습니다.");
            }
        }catch(Exception ex) {
            return "redirect:/loginform?error=true"; // 이메일이 없다면 다시 로그인 form으로 보낸다.
        }
        return "redirect:/";
    }

    //로그아웃 후 이동하는 페이지
    @GetMapping("/logout") //list.html 에서 <a href="/logout"> 울 만들었기때문에 GetMapping(클릭이기때문에get)
    public String logout(HttpSession httpSession) {
        //세션에서 회원정보를 삭제한다.
        httpSession.removeAttribute("loginInfo");
        return "redirect:/"; //로그아웃하면 다시 Main Page로 넘어가게 해준다. 정보 : 302code는 redirect 코드이다.
    }
}