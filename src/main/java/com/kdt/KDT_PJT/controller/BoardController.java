package com.kdt.KDT_PJT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kdt.KDT_PJT.mapper.BoardMapper;
import com.kdt.KDT_PJT.mapper.BoardTypeMapper;
import com.kdt.KDT_PJT.mapper.CommentMapper;
import com.kdt.KDT_PJT.model.Board;
import com.kdt.KDT_PJT.model.BoardType;
import com.kdt.KDT_PJT.model.Comment;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardMapper boardMapper;
    private final BoardTypeMapper typeMapper;
	private final CommentMapper commentMapper;

    public BoardController(BoardMapper boardMapper, BoardTypeMapper typeMapper, CommentMapper commentMapper) {
        this.boardMapper = boardMapper;
        this.typeMapper = typeMapper;
        this.commentMapper = commentMapper;
    }

    @GetMapping
    public String list(Model model) {
        List<Board> boards = boardMapper.findAll();
        List<BoardType> types = typeMapper.findAll();
        model.addAttribute("boards", boards);
        model.addAttribute("types", types);
        return "board/list";
    }

    @GetMapping("/create")
    public String form(
        @RequestParam(name = "id", required = false) Long id,
        Model model
    ) {
        model.addAttribute("board", id == null ? new Board() : boardMapper.findById(id));
        model.addAttribute("types", typeMapper.findAll());
        return "board/form";
    }

    @PostMapping("/save")
    public String save(
        @ModelAttribute Board post,
        @RequestParam(name = "type", required = false) Long typeId
    ) {
        // 1) 작성자 ID (여기서는 임시로 1L 고정)
        post.setAuthorId(1L);

        // 2) 게시판 유형: 파라미터로 넘어온 typeId 혹은 기본 1
        BoardType bt = new BoardType();
        bt.setId(typeId != null ? typeId : 1L);
        post.setBoardId(bt.getId());

        // 3) 저장
        boardMapper.insert(post);

        // 4) 리스트로 리다이렉트
        return "redirect:/board";
    }
    
    @GetMapping("/view")
    public String view(@RequestParam("id") Long postId, Model model) {
        Board post = boardMapper.findById(postId);
        List<Comment> comments = commentMapper.findByPostId(postId);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "board/detail";
    }
    
    @PostMapping("/comment/save")
    public String saveComment(
        @RequestParam("postId") Long postId,
        @RequestParam("content") String content
    ) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(1L); // ⚠️ 지금은 임시로 고정값! 로그인 연동 시 교체
        comment.setContent(content);

        commentMapper.insert(comment);

        return "redirect:/board/view?id=" + postId;
    }


    @PostMapping("/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        boardMapper.delete(id);
        return "redirect:/board";
    }
}
