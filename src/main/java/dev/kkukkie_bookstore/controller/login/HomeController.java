package dev.kkukkie_bookstore.controller.login;

import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.service.member.MemberService;
import dev.kkukkie_bookstore.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

import static dev.kkukkie_bookstore.model.member.role.MemberRole.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;

    @GetMapping("/")
    public String homeLogin(@Login Member member, Model model) {
        //세션에 회원 데이터가 없으면 home
        if (member == null) { return "home";}

        //세션이 유지되면 로그인으로 이동
        Member foundMember = memberService.findById(member.getId());
        model.addAttribute(
                "member",
                Objects.requireNonNullElse(foundMember, member)
        );

        switch (member.getRole()) {
            case ADMIN:
                return "loginAdminHome";
            case SUB_ADMIN:
            case NORMAL:
                return "loginNormalHome";
            default:
                return "redirect:/";
        }
    }

}