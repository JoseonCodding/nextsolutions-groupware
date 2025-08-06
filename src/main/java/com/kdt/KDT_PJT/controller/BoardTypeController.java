package com.kdt.KDT_PJT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kdt.KDT_PJT.mapper.BoardTypeMapper;
import com.kdt.KDT_PJT.model.BoardType;

import java.util.List;

@Controller
@RequestMapping("/types")
public class BoardTypeController {

    private final BoardTypeMapper mapper;

    public BoardTypeController(BoardTypeMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public String list(Model model) {
        List<BoardType> list = mapper.findAll();
        model.addAttribute("types", list);
        return "parts/list-types";
    }

    @GetMapping("/form")
    public String form(@RequestParam(required = false) Long id, Model model) {
        model.addAttribute("type", id==null ? new BoardType() : mapper.findById(id));
        return "parts/form-type";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardType type) {
        if (type.getId() == null) mapper.insert(type);
        else mapper.update(type);
        return "redirect:/types";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        mapper.delete(id);
        return "redirect:/types";
    }
}
