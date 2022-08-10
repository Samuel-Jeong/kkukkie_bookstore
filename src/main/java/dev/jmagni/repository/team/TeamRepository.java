package dev.jmagni.repository.team;

import dev.jmagni.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository
        extends JpaRepository<Team, Long> {

    List<Team> findByName(String name);

    Optional<Team> findById(Long id);

    List<Team> findAll();


}
