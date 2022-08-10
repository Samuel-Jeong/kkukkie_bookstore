package dev.jmagni.controller.login;

import dev.jmagni.model.member.Member;
import dev.jmagni.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String homeLogin(@Login Member member, Model model) {
        //세션에 회원 데이터가 없으면 home
        if (member == null) { return "home";}

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", member);

        return member.isAdmin()? "loginAdminHome" : "loginNormalHome";
    }

}