package com.kdt.KDT_PJT.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kdt.KDT_PJT.company.dto.CompanyRegisterDto;
import com.kdt.KDT_PJT.company.service.CompanyService;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // 회사 가입 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("dto", new CompanyRegisterDto());
        return "company/companyRegister";
    }

    // 회사 가입 처리
    @PostMapping("/register")
    public String registerProcess(@ModelAttribute CompanyRegisterDto dto, Model model) {

        // 비밀번호 일치 확인
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            model.addAttribute("dto", dto);
            model.addAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
            return "company/companyRegister";
        }

        // 이메일 중복 확인
        if (companyService.isEmailDuplicate(dto.getOwnerEmail())) {
            model.addAttribute("dto", dto);
            model.addAttribute("errorMsg", "이미 등록된 이메일입니다.");
            return "company/companyRegister";
        }

        // 등록 처리
        String adminId = companyService.register(dto);

        return "redirect:/login?registered=true&adminId=" + adminId;
    }
}
