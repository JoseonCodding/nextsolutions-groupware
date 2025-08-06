package com.kdt.KDT_PJT.sample.ctl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/approval")
@Controller
public class ApprovalController {
  
    @RequestMapping("/approvalMain")
    public String approvalMain() {
        return "approval/approvalMain";
    }

}