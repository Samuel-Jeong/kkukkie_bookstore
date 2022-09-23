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
import dev.kkukkie_bookstore.security.PasswordService;
import dev.kkukkie_bookstore.service.admin.AdminAuthService;
import dev.kkukkie_bookstore.service.member.MemberService;
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

import static dev.kkukkie_bookstore.InitAdminData.SUPER_TEAM_NAME;

@Slf4j
@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    private final MemberService memberService;
    private final AdminAuthService adminAuthService;

    private final PasswordService passwordService;

    private final List<String> memberRoles = new ArrayList<>();

    public MemberController(MemberRepository memberRepository, TeamRepository teamRepository,
                            MemberService memberService, AdminAuthService adminAuthService,
                            PasswordService passwordService) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.memberService = memberService;
        this.adminAuthService = adminAuthService;
        this.passwordService = passwordService;

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

        String authenticationCode = memberRegisterForm.getAuthenticationCode();
        if (!adminAuthService.isContains(authenticationCode)) {
            bindingResult.reject("AuthenticationFailed", new Object[]{}, "인증 실패");
        } else {
            adminAuthService.removeAuthCode(authenticationCode);
        }

        Member member = null;
        if (!bindingResult.hasErrors()) {
            checkDuplicateMemberAtRegisterByLoginId(memberRegisterForm, bindingResult);

            if (bindingResult.hasErrors()) {
                List<Team> teams = teamRepository.findAll();
                if (!teams.isEmpty()) {
                    model.addAttribute("teams", teams);
                }

                //log.warn("errors={}", bindingResult);
                return "members/registerMemberForm";
            }

            Team team = memberRegisterForm.getTeam();
            if (team == null || team.getName() == null || team.getName().isEmpty()) {
                bindingResult.reject("TeamIsNotSelected", new Object[]{}, "팀이 선택되지 않았습니다.");
            }

            member = memberService.saveMember(
                    memberRegisterForm.getLoginId(),
                    passwordService.encryptPassword(memberRegisterForm.getPassword()),
                    memberRegisterForm.getUsername(),
                    memberRegisterForm.getAge(),
                    memberRegisterForm.getTeam(),
                    memberRegisterForm.getProfileImgFile(),
                    bindingResult
            );
        }

        if (member == null) {
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
                                "MemberAlreadyExistByLoginId", new Object[]{foundMember.getId()}, "로그인 ID 가 이미 존재합니다."
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

        if (bindingResult.hasErrors()) {
            List<Team> teams = teamRepository.findAll();
            if (!teams.isEmpty()) {
                model.addAttribute("teams", teams);
            }

            //log.warn("errors={}", bindingResult);
            return "members/addMemberForm";
        }

        Team team = memberAddForm.getTeam();
        if (team == null || team.getName() == null || team.getName().isEmpty()) {
            bindingResult.reject("TeamIsNotSelected", new Object[]{}, "팀이 선택되지 않았습니다.");
        }

        Member member = memberService.saveMember(
                memberAddForm.getLoginId(),
                passwordService.encryptPassword(memberAddForm.getPassword()),
                memberAddForm.getUsername(),
                memberAddForm.getAge(),
                memberAddForm.getTeam(),
                memberAddForm.getProfileImgFile(),
                bindingResult
        );

        if (member == null || bindingResult.hasErrors()) {
            List<Team> teams = teamRepository.findAll();
            if (!teams.isEmpty()) {
                model.addAttribute("teams", teams);
            }

            //log.warn("errors={}", bindingResult);
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
                                        "로그인 ID 가 이미 존재합니다."
                                )
                );
    }

    @GetMapping("/{memberId}/edit")
    public String editForm(@PathVariable Long memberId, Model model) {
        Member member = memberRepository.findById(memberId).orElse(null);

        if (member != null) {
            member.setPassword("");
        }

        model.addAttribute("memberId", memberId);
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
                       BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, "사용자를 찾을 수 없습니다.");
        }

        Team team = memberUpdateForm.getTeam();
        if (team == null || team.getName() == null || team.getName().isEmpty()) {
            bindingResult.reject("TeamIsNotSelected", new Object[]{}, "팀이 선택되지 않았습니다.");
        }

        memberService.updateMember(memberUpdateForm, bindingResult, member);

        if (member == null || bindingResult.hasErrors()) {
            List<Team> teams = teamRepository.findAll();
            if (!teams.isEmpty()) {
                model.addAttribute("teams", teams);
            }

            //log.warn("errors={}", bindingResult);
            return "members/editMemberForm";
        }

        memberRepository.save(member);

        redirectAttributes.addAttribute("memberId", memberId);
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
            bindingResult.reject("NotFoundMember", new Object[]{memberId}, "사용자를 찾을 수 없습니다.");
        }

        if (bindingResult.hasErrors()) {
            //log.warn("errors={}", bindingResult);
            return "redirect:/members";
        }

        if (member != null) {
            // 기존에 프로파일 이미지가 있으면 해당 파일 삭제
            memberService.deletePrevProfileImage(member);

            memberRepository.delete(member);
        }

        return "redirect:/members";
    }

    @ModelAttribute("memberRoles")
    public List<String> memberRoles() {
        return memberRoles;
    }

}
