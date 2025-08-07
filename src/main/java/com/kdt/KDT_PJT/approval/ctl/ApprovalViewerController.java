package com.kdt.KDT_PJT.approval.ctl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/approval")
public class ApprovalViewerController {
    
    @RequestMapping("/viewer")
    public String approvalViewer() {
    	
    	return "approval/approvalViewer";
    }
	

}
