package dev.kkukkie_bookstore.service.board;

import dev.kkukkie_bookstore.model.board.Board;
import dev.kkukkie_bookstore.repository.board.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board findById(long boardId) {
        return boardRepository.findById(boardId).orElse(null);
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    public void delete(Board board) {
        boardRepository.delete(board);
    }

    public void deleteById(long boardId) {
        boardRepository.deleteById(boardId);
    }

}
