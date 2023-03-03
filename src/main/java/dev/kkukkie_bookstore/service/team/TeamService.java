package dev.kkukkie_bookstore.service.team;

import dev.kkukkie_bookstore.model.team.Team;
import dev.kkukkie_bookstore.repository.team.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team findById(long teamId) {
        return teamRepository.findById(teamId).orElse(null);
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public Team save(Team team) {
        return teamRepository.save(team);
    }

    public void delete(Team team) {
        teamRepository.delete(team);
    }

}
