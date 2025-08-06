package com.kdt.KDT_PJT.sample.ctl;

import com.kdt.KDT_PJT.sample.dto.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    // 사용자 등록 페이지
    @GetMapping("/new")
    public String showCreateUserForm() {
        return "user/createUser"; // /templates/user/createUser.html or /WEB-INF/views/user/createUser.jsp
    }

    // 사용자 등록 처리
    @PostMapping
    public String createUser(UserDTO dto) {
        // Service 호출 → 사용자 저장
        return "redirect:/users"; // 등록 후 목록 페이지로 이동
    }

    // 사용자 전체 조회 페이지
    @GetMapping
    public String getAllUsers(Model model) {
        // Service 호출 → 전체 사용자 조회
        List<UserDTO> users = List.of(); // 예시 빈 리스트
        model.addAttribute("users", users);
        return "user/userList"; // 목록 페이지
    }

    // 특정 사용자 상세 페이지
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        // Service 호출 → 특정 사용자 조회
        UserDTO user = new UserDTO(); // 예시
        model.addAttribute("user", user);
        return "user/userDetail"; // 상세 페이지
    }

    // 사용자 수정 페이지
    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        // Service 호출 → 특정 사용자 조회
        UserDTO user = new UserDTO(); // 예시
        model.addAttribute("user", user);
        return "user/editUser";
    }

    // 사용자 수정 처리
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, UserDTO dto) {
        // Service 호출 → 사용자 정보 수정
        return "redirect:/users/" + id;
    }

    // 사용자 삭제 처리
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        // Service 호출 → 사용자 삭제
        return "redirect:/users";
    }
}
