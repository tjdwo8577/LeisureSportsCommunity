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
    //classpath:/templates/userRefForm.html
    @GetMapping("/userRegForm")
    public String userRegForm() {
        return "userRegForm";
    }

    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        System.out.println("name : " + name);
        System.out.println("email : " + email);
        System.out.println("password : " + password);

        userService.addUser(name, email, password);

        // 어떤 기능이 필요한지 미리 알 수 있다.
        // 회원 정보를 저장한다. 동사는 메소드, 메소드만 선언하고 있는것은 인터페이스

        return "redirect:/welcome"; // 브라우저에게 자동으로 http://localhost:8080/welcome으로 이동
    }

    //http://localhost:8080/welcome 으로 이동 해주는 것
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/loginform")
    public String loginform() {
        return "loginform";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession // Spring이 자동으로 session을 처리하는 HttpSession객체를 넣어준다.
    ) {
        //email에 해당하는 회원 정보를 읽어온 후
        // 아이디 암호가 맞다면 세션에 회원정보를 저장한다.
        System.out.println("email : " + email);
        System.out.println("password : " + password);

        try {
            User user = userService.getUser(email);
            if(user.getPassword().equals(password)) {
                System.out.println("암호가 같습니다.");
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());

                // 권환정보를 읽어와서 loginInfo에 추가한다.
                List<String> roles = userService.getRoles(user.getUserId());
                loginInfo.setRoles(roles);

                httpSession.setAttribute("loginInfo", loginInfo); // 첫번째 파라미터가 key, 두번째 파라미터가 값
                System.out.println("세선에 로그인 정보가 저장된다.");
            }else{
                throw new RuntimeException("암호가 일치하지 않습니다.");
            }
        }catch(Exception ex) {
            return "redirect:/loginform?error=true"; // 이메일이 없다면 다시 로그인 form으로 보낸다.
        }
        return "redirect:/";
    }

    //로그아웃 후 리동하는 페이지
    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        //세션에서 회원정보를 삭제한다.
        httpSession.removeAttribute("loginInfo");
        return "redirect:/"; //302code는 redirect 코드이다.
    }
}
