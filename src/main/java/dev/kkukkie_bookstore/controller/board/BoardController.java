package dev.kkukkie_bookstore.controller.board;

import dev.kkukkie_bookstore.model.board.Board;
import dev.kkukkie_bookstore.model.board.dto.BoardAddDto;
import dev.kkukkie_bookstore.model.board.dto.BoardDeleteDto;
import dev.kkukkie_bookstore.model.board.dto.BoardUpdateDto;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.member.role.MemberRole;
import dev.kkukkie_bookstore.repository.board.BoardRepository;
import dev.kkukkie_bookstore.repository.member.MemberRepository;
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
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    @GetMapping("{memberId}/{boardId}")
    public String board(@PathVariable long memberId,
                        @PathVariable long boardId, Model model) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return "redirect:/";
        }

        Board board = boardRepository.findById(boardId).orElse(null);
        if (board != null) {
            int hitCount = board.getHitCount();
            board.setHitCount(hitCount + 1);
            boardRepository.save(board);
        }
        model.addAttribute("memberId", memberId);
        model.addAttribute("board", board);

        if (member.getRole().equals(MemberRole.ADMIN)) {
            return "boards/boardAdmin";
        } else {
            return "boards/boardNormal";
        }
    }

    @GetMapping("{memberId}")
    public String boards(@PathVariable long memberId, Model model) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return "redirect:/";
        }

        model.addAttribute("memberId", memberId);
        model.addAttribute("memberRole", member.getRole());

        //로그인 여부 체크
        List<Board> boards = boardRepository.findAll();
        model.addAttribute("boards", boards);

        return "boards/boards";
    }

    @GetMapping("{memberId}/add")
    public String addForm(@PathVariable long memberId,
                          @ModelAttribute("board") Board board,
                          Model model) {
        model.addAttribute("memberId", memberId);
        return "boards/addBoard";
    }

    @PostMapping("{memberId}/add")
    public String add(@PathVariable long memberId,
                      @Validated @ModelAttribute("board") BoardAddDto boardAddDto,
                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        String title = boardAddDto.getTitle();
        String author = boardAddDto.getAuthor();
        String content = boardAddDto.getContent();

        if (bindingResult.hasErrors()) {
            log.warn("errors={}", bindingResult);
            return "boards/addBoard";
        }

        Board board = new Board(title, author, content);
        Board saveBoard = boardRepository.save(board);

        redirectAttributes.addAttribute("memberId", memberId);
        redirectAttributes.addAttribute("boardId", saveBoard.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/boards/{memberId}";
    }

    @GetMapping("{memberId}/{boardId}/delete")
    public String deleteForm(@PathVariable long memberId,
                             @PathVariable long boardId, Model model) {
        Board board = boardRepository.findById(boardId).orElse(null);
        model.addAttribute("memberId", memberId);
        model.addAttribute("board", board);
        return "boards/deleteBoard";
    }

    @PostMapping("{memberId}/{boardId}/delete")
    public String delete(@PathVariable long memberId,
                         @PathVariable Long boardId, @Validated @ModelAttribute("board") BoardDeleteDto boardDeleteDto,
                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("memberId", memberId);

        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            bindingResult.reject("NotFoundBoard", new Object[]{boardId}, null);
        }

        if (bindingResult.hasErrors()) {
            log.warn("errors={}", bindingResult);
            return "redirect:/boards/{memberId}";
        }

        if (board != null) {
            boardRepository.deleteById(board.getId());
        }

        return "redirect:/boards/{memberId}";
    }

    @GetMapping("{memberId}/{boardId}/update")
    public String updateForm(@PathVariable long memberId,
                             @PathVariable Long boardId, Model model) {
        Board board = boardRepository.findById(boardId).orElse(null);
        model.addAttribute("memberId", memberId);
        model.addAttribute("board", board);
        return "boards/updateBoard";
    }

    @PostMapping("{memberId}/{boardId}/update")
    public String update(@PathVariable long memberId,
                         @PathVariable Long boardId,
                       @Validated @ModelAttribute("board") BoardUpdateDto boardUpdateDto,
                       BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("memberId", memberId);

        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            bindingResult.reject("NotFoundBoard", new Object[]{boardId}, null);
        }

        if (bindingResult.hasErrors()) {
            log.warn("errors={}", bindingResult);
            return "redirect:/boards/{memberId}";
        }

        if (board != null) {
            board.setContent(boardUpdateDto.getContent());

            boardRepository.save(board);
        }

        return "redirect:/boards/{memberId}/{boardId}";
    }

}
