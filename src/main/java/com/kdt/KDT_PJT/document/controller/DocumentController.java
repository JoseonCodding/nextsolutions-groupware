//package com.kdt.KDT_PJT.document.controller;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.List;
//
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.kdt.KDT_PJT.document.mapper.DocumentMapper;
//import com.kdt.KDT_PJT.document.model.DocumentDTO;
//
//@Controller
//@RequestMapping("/document")
//public class DocumentController {
//
//    private final DocumentMapper documentMapper;
//
//    public DocumentController(DocumentMapper documentMapper) {
//        this.documentMapper = documentMapper;
//    }
//
//
//	@RequestMapping("/main") public String documentMain(Model model) {
//	List<DocumentDTO> list = documentMapper.selectAll();
//	model.addAttribute("approvalData", list); model.addAttribute("mainUrl",
//	"document/document_list"); return "home"; }
//
//
//
//	@RequestMapping("/viewer") public String
//	documentViewer(@RequestParam("versionId") Long versionId, Model model) {
//	DocumentDTO dto = documentMapper.selectByVersionId(versionId);
//	model.addAttribute("doc", dto); model.addAttribute("mainUrl",
//	"document/documentViewer"); return "navTap"; }
//
//
//    @GetMapping("/downloadFile")
//    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName,
//                                                 @RequestParam("orgName") String orgName) {
//        try {
//            String path = "C:/upload/" + fileName;
//            Resource resource = new FileSystemResource(path);
//
//            if (!resource.exists()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            String encodedOrgName = URLEncoder.encode(orgName, "UTF-8").replaceAll("\\+", " ");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOrgName + "\"");
//            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
//            headers.add("Content-Transfer-Encoding", "binary");
//
//            return ResponseEntity.ok().headers(headers).body(resource);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//
//
//}
