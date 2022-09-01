package dev.kkukkie_bookstore.controller.member;

import dev.kkukkie_bookstore.controller.member.form.MemberAddForm;
import dev.kkukkie_bookstore.controller.member.form.MemberDeleteForm;
import dev.kkukkie_bookstore.controller.member.form.MemberRegisterForm;
import dev.kkukkie_bookstore.controller.member.form.MemberUpdateForm;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.member.dto.MemberDto;
import dev.kkukkie_bookstore.model.member.role.MemberRole;
import dev.kkukkie_bookstore.model.team.Team;
import dev.kkukkie_bookstore.repository.member.MemberRepository;
import dev.kkukkie_bookstore.repository.team.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static dev.kkukkie_bookstore.InitData.SUPER_TEAM_NAME;

@Slf4j
@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    private final List<String> memberRoles = new ArrayList<>();

    public MemberController(MemberRepository memberRepository, TeamRepository teamRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;

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
                            member.getRole(),
                            member.getTeam().getName(),
                            member.getBooks()
                    )
            );
        }

        model.addAttribute("members", memberDtoList);

        return "members/members";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("member") Member member, Model model) {
        List<Team> teams = teamRepository.findAll();
        if (!teams.isEmpty()) {
            teams.removeIf(team -> team.getName().equals(SUPER_TEAM_NAME));
            model.addAttribute("teams", teams);
        }

        return "members/registerMemberForm";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("member") MemberRegisterForm memberRegisterForm,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        checkDuplicateMemberAtRegisterByLoginId(memberRegisterForm, bindingResult);

        Team team = memberRegisterForm.getTeam();
        if (team == null || team.getName() == null || team.getName().isEmpty()) {
            bindingResult.reject("TeamIsNotSelected", new Object[]{}, null);
        }

        Member member = null;
        try {
            member = new Member(
                    memberRegisterForm.getLoginId(),
                    memberRegisterForm.getPassword(),
                    memberRegisterForm.getUsername(),
                    Integer.parseInt(memberRegisterForm.getAge()),
                    memberRegisterForm.getTeam()
            );
        } catch (Exception e) {
            bindingResult.reject("InputException", new Object[]{}, null);
        }

        if (member == null || bindingResult.hasErrors()) {
            List<Team> teams = teamRepository.findAll();
            if (!teams.isEmpty()) {
                teams.removeIf(foundTeam -> foundTeam.getName().equals(SUPER_TEAM_NAME));
                model.addAttribute("teams", teams);
            }

            return "members/registerMemberForm";
        }

        Member savedMember = memberRepository.save(member);
        redirectAttributes.addAttribute("memberId", savedMember.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/";
    }

    private void checkDuplicateMemberAtRegisterByLoginId(MemberRegisterForm memberRegisterForm,
                                                         BindingResult bindingResult) {
        memberRepository.findByLoginId(
                memberRegisterForm.getLoginId()).ifPresent(
                foundMember ->
                        bindingResult.reject(
                                "MemberAlreadyExist", new Object[]{foundMember.getId()}, null
                        )
        );
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member, Model model) {
        List<Team> teams = teamRepository.findAll();
        if (!teams.isEmpty()) {
            model.addAttribute("teams", teams);
        }

        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("member") MemberAddForm memberAddForm,
                      BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        checkDuplicateMemberAtAddByLoginId(memberAddForm, bindingResult);

        Team team = memberAddForm.getTeam();
        if (team == null || team.getName() == null || team.getName().isEmpty()) {
            bindingResult.reject("TeamIsNotSelected", new Object[]{}, null);
        }

        Member member = null;
        try {
            member = new Member(
                    memberAddForm.getLoginId(),
                    memberAddForm.getPassword(),
                    memberAddForm.getUsername(),
                    Integer.parseInt(memberAddForm.getAge()),
                    memberAddForm.getTeam()
            );
        } catch (Exception e) {
            bindingResult.reject("InputException", new Object[]{}, null);
        }

        if (member == null || bindingResult.hasErrors()) {
            List<Team> teams = teamRepository.findAll();
            if (!teams.isEmpty()) {
                model.addAttribute("teams", teams);
            }

            return "members/addMemberForm";
        }

        Member savedMember = memberRepository.save(member);
        redirectAttributes.addAttribute("memberId", savedMember.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/members/{memberId}";
    }

    private void checkDuplicateMemberAtAddByLoginId(MemberAddForm memberAddForm,
                                                    BindingResult bindingResult) {
        memberRepository.findByLoginId(memberAddForm.getLoginId())
                .ifPresent(
                        foundMember ->
                                bindingResult.reject(
                                        "MemberAlreadyExist",
                                        new Object[]{foundMember.getId()},
                                        null
                                )
                );
    }

    @GetMapping("/{memberId}/edit")
    public String editForm(@PathVariable Long memberId, Model model) {
        Member member = memberRepository.findById(memberId).orElse(null);
        model.addAttribute("member", member);

        List<Team> teams = teamRepository.findAll();
        if (!teams.isEmpty()) {
            model.addAttribute("teams", teams);
        }

        return "members/editMemberForm";
    }

    @PostMapping("/{memberId}/edit")
    public String edit(@PathVariable Long memberId,
                       @Validated @ModelAttribute("member") MemberUpdateForm memberUpdateForm,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, null);
        }

        Team team = memberUpdateForm.getTeam();
        if (team.getName() == null || team.getName().isEmpty()) {
            bindingResult.reject("TeamIsNotSelected", new Object[]{}, null);
        }

        if (member != null) {
            try {
                member.setUsername(memberUpdateForm.getUsername());
                member.setAge(Integer.parseInt(memberUpdateForm.getAge()));
                member.setPassword(memberUpdateForm.getPassword());
                member.setRole(memberUpdateForm.getRole());
                member.setTeam(memberUpdateForm.getTeam());
            } catch (Exception e) {
                bindingResult.reject("InputException", new Object[]{}, null);
            }
        }

        if (member == null || bindingResult.hasErrors()) {
            return "members/editMemberForm";
        }

        memberRepository.save(member);

        return "redirect:/members/{memberId}";
    }

    @GetMapping("{memberId}/delete")
    public String deleteForm(@PathVariable long memberId, Model model) {
        Member member = memberRepository.findById(memberId).orElse(null);
        model.addAttribute("member", member);

        return "members/deleteMemberForm";
    }

    @PostMapping("{memberId}/delete")
    public String delete(@PathVariable Long memberId,
                         @Validated @ModelAttribute("member") MemberDeleteForm form,
                         BindingResult bindingResult) {

        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, null);
        }

        if (bindingResult.hasErrors()) {
            log.warn("errors={}", bindingResult);
            return "redirect:/members";
        }

        if (member != null) {
            memberRepository.delete(member);
        }

        return "redirect:/members";
    }

    @ModelAttribute("memberRoles")
    public List<String> memberRoles() {
        return memberRoles;
    }

}
