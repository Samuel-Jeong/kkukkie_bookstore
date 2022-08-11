package dev.jmagni.controller.team;

import dev.jmagni.model.team.Team;
import dev.jmagni.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    @GetMapping("/add")
    public String addForm(@ModelAttribute("team") Team team) {
        return "teams/addTeamForm";
    }

    @PostMapping("/add")
    public String add(@Validated @ModelAttribute("team") TeamSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        String name = form.getName();
        if (name == null || name.isEmpty()) {
            bindingResult.reject("NameError", new Object[]{name}, null);
        }

        // 팀 이름으로 중복 체크
        Team team = teamRepository.findByName(form.getName()).stream().findFirst().orElse(null);
        if (team != null) {
            bindingResult.reject("TeamAlreadyExist", new Object[]{team.getId()}, null);
        }

        if (bindingResult.hasErrors()) {
            log.warn("errors={}", bindingResult);
            return "teams/addTeamForm";
        }

        team = new Team(name);
        Team savedTeam = teamRepository.save(team);
        redirectAttributes.addAttribute("teamId", savedTeam.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/teams/{teamId}";
    }

    @GetMapping("/{teamId}")
    public String team(@PathVariable long teamId, Model model) {
        //로그인 여부 체크
        Team team = teamRepository.findById(teamId).orElse(null);
        model.addAttribute("team", team);
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
    public String edit(@PathVariable Long teamId, @Validated @ModelAttribute("team") TeamUpdateForm form, BindingResult bindingResult) {
        String name = form.getName();
        if (name == null || name.isEmpty()) {
            bindingResult.reject("NameError", new Object[]{name}, null);
        }

        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            bindingResult.reject("NotFoundTeam", new Object[]{teamId}, null);
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "teams/editTeamForm";
        }

        if (team != null) {
            team.setName(name);
            teamRepository.save(team);
        }

        return "redirect:/teams/{teamId}";
    }
    
}
