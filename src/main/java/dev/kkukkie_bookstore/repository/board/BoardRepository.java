package dev.kkukkie_bookstore.repository.board;

import dev.kkukkie_bookstore.model.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findById(Long id);

    List<Board> findAll();

}
