package dev.jmagni.controller.member;

import dev.jmagni.model.condition.MemberSearchCondition;
import dev.jmagni.model.dto.MemberTeamDto;
import dev.jmagni.model.member.Member;
import dev.jmagni.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    /**
     * http://localhost:8080/members?teamName=teamB&ageGoe=31&ageLoe=35&username=member31&page=0&size=3
     */
    @GetMapping("/")
    public Page<MemberTeamDto> searchMemberPaging(MemberSearchCondition memberSearchCondition, Pageable pageable) {
        return memberRepository.searchPaging(memberSearchCondition, pageable);
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@Valid @ModelAttribute Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }

        memberRepository.save(member);
        return "redirect:/";
    }

}
