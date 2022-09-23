package dev.kkukkie_bookstore.repository.board;

import dev.kkukkie_bookstore.model.board.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

}