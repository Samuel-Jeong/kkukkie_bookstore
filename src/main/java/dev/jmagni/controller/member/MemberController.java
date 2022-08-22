package dev.jmagni.controller.member;

import dev.jmagni.model.dto.MemberDto;
import dev.jmagni.model.member.Member;
import dev.jmagni.model.role.MemberRole;
import dev.jmagni.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    private final List<MemberRole> memberRoles = new ArrayList<>();

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;

        memberRoles.add(MemberRole.ADMIN);
        memberRoles.add(MemberRole.SUB_ADMIN);
        memberRoles.add(MemberRole.NORMAL);
    }

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
    public String members(Model model) {
        List<Member> members = memberRepository.findAll();
        List<MemberDto> memberDtoList = new ArrayList<>();
        for (Member member : members) {
            memberDtoList.add(
                    new MemberDto(
                            member.getId(),
                            member.getUsername(),
                            member.getAge(),
                            member.getLoginId(),
                            member.getRole()
                    )
            );
        }

        model.addAttribute("members", memberDtoList);
        //model.addAttribute("members", members);
        return "members/members";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("member") Member member) {
        return "members/registerMemberForm";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute MemberRegisterForm memberRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        checkDuplicateMemberAtRegister(memberRegisterForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "members/registerMemberForm";
        }

        Member member = new Member(
                memberRegisterForm.getLoginId(),
                memberRegisterForm.getPassword(),
                memberRegisterForm.getUsername(),
                memberRegisterForm.getAge()
        );

        Member savedMember = memberRepository.save(member);
        redirectAttributes.addAttribute("memberId", savedMember.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/";
    }

    private void checkDuplicateMemberAtRegister(MemberRegisterForm memberRegisterForm, BindingResult bindingResult) {
        memberRepository.findByLoginId(
                memberRegisterForm.getLoginId()).ifPresent(
                foundMember ->
                        bindingResult.reject(
                                "MemberAlreadyExist", new Object[]{foundMember.getId()}, null
                        )
        );
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute MemberSaveForm memberSaveForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        checkDuplicateMemberAtAdd(memberSaveForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }

        Member member = new Member(
                memberSaveForm.getLoginId(),
                memberSaveForm.getPassword(),
                memberSaveForm.getUsername(),
                memberSaveForm.getAge()
        );

        Member savedMember = memberRepository.save(member);
        redirectAttributes.addAttribute("memberId", savedMember.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/members/{memberId}";
    }

    private void checkDuplicateMemberAtAdd(MemberSaveForm memberSaveForm, BindingResult bindingResult) {
        memberRepository.findByLoginId(
                memberSaveForm.getLoginId()).ifPresent(
                        foundMember ->
                                bindingResult.reject(
                                        "MemberAlreadyExist", new Object[]{foundMember.getId()}, null
                                )
        );
    }

    @GetMapping("/{memberId}/edit")
    public String editForm(@PathVariable Long memberId, Model model) {
        Member member = memberRepository.findById(memberId).orElse(null);
        model.addAttribute("member", member);
        return "members/editMemberForm";
    }

    @PostMapping("/{memberId}/edit")
    public String edit(@PathVariable Long memberId, @Validated @ModelAttribute("member") MemberUpdateForm memberUpdateForm, BindingResult bindingResult) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, null);
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "members/editMemberForm";
        }

        if (member != null) {
            member.setUsername(memberUpdateForm.getUsername());
            member.setAge(memberUpdateForm.getAge());
            member.setPassword(memberUpdateForm.getPassword());
            member.setRole(memberUpdateForm.getRole());

            memberRepository.save(member);
        }

        return "redirect:/members/{memberId}";
    }

    @ModelAttribute("memberRoles")
    public List<MemberRole> memberRoles() {
        return memberRoles;
    }
    
}
