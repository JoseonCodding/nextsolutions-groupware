package com.kdt.KDT_PJT.documentMng.ctl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        model.addAttribute("approvalData", list);
        model.addAttribute("mainUrl", "documentMng/documentMngMain");
        return "navTap";
    }
    
    @RequestMapping("/viewer")
    public String documentViewer(@RequestParam("versionId") Long versionId, Model model) {
        DocumentMngDTO dto = documentMngMapper.selectByVersionId(versionId);
        model.addAttribute("doc", dto);
        model.addAttribute("mainUrl", "documentMng/documentMngViewer");
        return "navTap";
    }
    
    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName,
                                                 @RequestParam("orgName") String orgName) {
        try {
            String path = "C:/upload/" + fileName;
            Resource resource = new FileSystemResource(path);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String encodedOrgName = URLEncoder.encode(orgName, "UTF-8").replaceAll("\\+", " ");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOrgName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            headers.add("Content-Transfer-Encoding", "binary");

            return ResponseEntity.ok().headers(headers).body(resource);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }



}
