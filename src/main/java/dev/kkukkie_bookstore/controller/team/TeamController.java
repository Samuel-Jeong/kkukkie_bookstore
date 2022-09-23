package dev.kkukkie_bookstore.controller.team;

import dev.kkukkie_bookstore.controller.team.form.TeamDeleteForm;
import dev.kkukkie_bookstore.controller.team.form.TeamSaveForm;
import dev.kkukkie_bookstore.controller.team.form.TeamUpdateForm;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.team.Team;
import dev.kkukkie_bookstore.repository.member.MemberRepository;
import dev.kkukkie_bookstore.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static dev.kkukkie_bookstore.InitAdminData.SUPER_TEAM_NAME;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/add")
    public String addForm(@ModelAttribute("team") Team team) {
        return "teams/addTeamForm";
    }

    @PostMapping("/add")
    public String add(@Validated @ModelAttribute("team") TeamSaveForm form,
                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        String name = form.getName();
        if (name.isEmpty()) {
            bindingResult.reject("NameError", new Object[]{name}, "팀 이름이 비어있습니다.");
        }

        // 팀 이름으로 중복 체크
        Team team = teamRepository.findByName(form.getName()).orElse(null);
        if (team != null) {
            bindingResult.reject("TeamAlreadyExist", new Object[]{team.getId()}, "팀이 이미 존재합니다.");
        }

        if (bindingResult.hasErrors()) {
            //log.warn("errors={}", bindingResult);
            return "teams/addTeamForm";
        }

        team = new Team(name);
        Team savedTeam = teamRepository.save(team);
        redirectAttributes.addAttribute("teamId", savedTeam.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/teams/{teamId}";
    }

    @GetMapping("{teamId}/delete")
    public String deleteForm(@PathVariable long teamId, Model model) {
        Team team = teamRepository.findById(teamId).orElse(null);
        model.addAttribute("team", team);

        if (team != null) {
            List<Member> members = team.getMembers();
            model.addAttribute("members", members);
        }

        return "teams/deleteTeamForm";
    }

    @PostMapping("{teamId}/delete")
    public String delete(@PathVariable Long teamId,
                         @Validated @ModelAttribute("team") TeamDeleteForm form,
                         BindingResult bindingResult) {
        String name = form.getName();
        if (name.isEmpty()) {
            bindingResult.reject("NameError", new Object[]{name}, "팀 이름이 비어있습니다.");
        } else if (name.equals(SUPER_TEAM_NAME)) {
            bindingResult.reject("NameError", new Object[]{name}, "관리자 팀은 삭제할 수 없습니다.");
        }

        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            bindingResult.reject("NotFoundTeam", new Object[]{teamId}, "팀이 존재하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            //log.warn("errors={}", bindingResult);
            return "redirect:/teams";
        }

        if (team != null) {
            memberRepository.deleteAll(team.getMembers());
            teamRepository.deleteById(team.getId());
        }

        return "redirect:/teams";
    }

    @GetMapping("/{teamId}")
    public String team(@PathVariable long teamId, Model model) {
        //로그인 여부 체크
        Team team = teamRepository.findById(teamId).orElse(null);
        model.addAttribute("team", team);

        if (team != null) {
            List<Member> members = team.getMembers();
            model.addAttribute("members", members);
        }

        return "teams/team";
    }

    @GetMapping
    public String teams(Model model) {
        //로그인 여부 체크
        List<Team> teams = teamRepository.findAll();
        model.addAttribute("teams", teams);
        return "teams/teams";
    }

    @GetMapping("/{teamId}/edit")
    public String editForm(@PathVariable Long teamId, Model model) {
        Team team = teamRepository.findById(teamId).orElse(null);
        model.addAttribute("team", team);

        return "teams/editTeamForm";
    }

    @PostMapping("/{teamId}/edit")
    public String edit(@PathVariable Long teamId,
                       @Validated @ModelAttribute("team") TeamUpdateForm form,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        String name = form.getName();
        if (name.isEmpty()) {
            bindingResult.reject("NameError", new Object[]{name}, "팀 이름이 비어있습니다.");
        }

        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            bindingResult.reject("NotFoundTeam", new Object[]{teamId}, "팀이 존재하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            //log.warn("errors={}", bindingResult);
            return "teams/editTeamForm";
        }

        if (team != null) {
            team.setName(name);

            teamRepository.save(team);
        }

        redirectAttributes.addAttribute("teamId", teamId);
        return "redirect:/teams/{teamId}";
    }
    
}
