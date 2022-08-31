package dev.kkukkie_bookstore.repository.team;

import dev.kkukkie_bookstore.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository
        extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    Optional<Team> findById(Long id);

    List<Team> findAll();

}
