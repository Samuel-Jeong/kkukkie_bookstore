package dev.jmagni.controller.member;

import dev.jmagni.model.condition.MemberSearchCondition;
import dev.jmagni.model.dto.MemberDto;
import dev.jmagni.model.dto.MemberTeamDto;
import dev.jmagni.model.member.Member;
import dev.jmagni.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    // http://localhost:8080/members?memberName=memberB&ageGoe=31&ageLoe=35&username=member31&page=0&size=3
    /*@GetMapping("/")
    public Page<MemberTeamDto> searchMemberPaging(MemberSearchCondition memberSearchCondition, Pageable pageable) {
        return memberRepository.searchPaging(memberSearchCondition, pageable);
    }*/

    @GetMapping("/{memberId}")
    public String member(@PathVariable long memberId, Model model) {
        //로그인 여부 체크
        Member member = memberRepository.findById(memberId).orElse(null);
        model.addAttribute("member", member);
        return "members/member";
    }

    @GetMapping
    public String memberList(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members.stream().map(
                member -> new MemberDto(
                        member.getId(),
                        member.getUsername(),
                        member.getAge(),
                        member.getLoginId(),
                        member.getRole()
                )
        ));
        //model.addAttribute("members", members);
        return "members/members";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("member") Member member) {
        return "members/registerMemberForm";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "members/registerMemberForm";
        }

        memberRepository.save(member);
        return "redirect:/";
    }


    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Member member, BindingResult bindingResult) {
        memberRepository.findById(
                member.getId()).ifPresent(
                        foundMember ->
                                bindingResult.reject(
                                        "MemberAlreadyExist", new Object[]{foundMember.getId()}, null
                                )
        );

        if (bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }

        memberRepository.save(member);
        return "redirect:/";
    }

    @GetMapping("/{memberId}/edit")
    public String editForm(@PathVariable Long memberId, Model model) {
        Member member = memberRepository.findById(memberId).orElse(null);
        model.addAttribute("member", member);
        return "members/editMemberForm";
    }

    @PostMapping("/{memberId}/edit")
    public String edit(@PathVariable Long memberId, @Validated @ModelAttribute("member") MemberUpdateForm form, BindingResult bindingResult) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, null);
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "members/editMemberForm";
        }

        if (member != null) {
            member.setUsername(member.getUsername());
            member.setAge(member.getAge());
            member.setPassword(member.getPassword());
            memberRepository.save(member);
        }

        return "redirect:/members/{memberId}";
    }
    
}
