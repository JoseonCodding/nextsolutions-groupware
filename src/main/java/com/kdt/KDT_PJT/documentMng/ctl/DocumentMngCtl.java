package com.kdt.KDT_PJT.documentMng.ctl;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kdt.KDT_PJT.documentMng.mapper.DocumentMngMapper;
import com.kdt.KDT_PJT.documentMng.model.DocumentMngDTO;

@Controller
@RequestMapping("/documentMng")
public class DocumentMngCtl {

    private final DocumentMngMapper documentMngMapper;

    public DocumentMngCtl(DocumentMngMapper documentMngMapper) {
        this.documentMngMapper = documentMngMapper;
    }

    @ModelAttribute("navUrl")
    public String navUrl() {
        return "documentMng/documentMngNav";
    }

    @RequestMapping("/main")
    public String documentMngMain(Model model) {
        List<DocumentMngDTO> list = documentMngMapper.selectAll();
        model.addAttribute("approvalData", list); // HTML에서 사용
        model.addAttribute("mainUrl", "documentMng/documentMngMain");
        return "navTap";
    }
}
