package com.kdt.KDT_PJT.pjt_mng.ctl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.pjt_mng.svc.ProjectMngService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/pjtMng")
@RequiredArgsConstructor
public class ProjectMngController {
	
	@Autowired
	private ProjectMngService projectMngService;
	
	// log 사용을 위함
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	
	
	// ✅ 프로젝트 목록 화면 조회 (검색어 유무에 따라 분기)
	@GetMapping("/getPjtList")
	public String getPjtList(
	        @RequestParam(required = false, name = "keyword") String keyword
	        , HttpServletRequest request
	        , Model model) {
		

	    // 🔍 검색어 로그 확인 (디버깅용)
	   System.out.println("getPjtList Called >>> keyword = " + keyword);
	   
       // ① 페이지 번호/사이즈 받아오기 (없으면 기본값)
       int pageNum = 1;
       int pageSize = 10;
       
       try {
           if (request.getParameter("pageNum") != null) {
               pageNum = Integer.parseInt(request.getParameter("pageNum"));
           }
           if (request.getParameter("pageSize") != null) {
               pageSize = Integer.parseInt(request.getParameter("pageSize"));
           }
       } catch (Exception e) {
           log.warn("페이지 번호 파싱 실패, 기본값 사용");
       }
       
		//  리스트 가져오기
       PageInfo<CmmnMap>  list = projectMngService.getProjectList(pageNum, pageSize, keyword);
       // ③ model에 값 담기
       
       if (list == null || list.getList() == null || list.getList().isEmpty()) {
    	    log.warn("⚠️ 프로젝트 리스트가 비어있거나 null입니다.");
    	    model.addAttribute("pjtList", List.of());  // 빈 리스트로 넘기기 (NPE 방지)
    	} else {
    	    log.info("✅ 프로젝트 리스트 로드 성공. 개수: " + list.getList().size());
    	    model.addAttribute("pjtList", list.getList());
    	}
       
       model.addAttribute("pjtList", list.getList());   // 현재 페이지 데이터
       model.addAttribute("pageInfo", list);             // 페이징 정보      
       
       
       
       
       
//	    // 🗂 프로젝트 리스트 선언
//	    List<CmmnMap> pjtList;
//
//	    // 🔎 keyword가 있으면 → 검색 조건으로 조회
//	    if (keyword != null && !keyword.isEmpty()) {
//	        pjtList = projectMngService.searchProjectMngList(keyword);
//	        log.info("🔍 검색 결과 개수: " + pjtList.size());
//	    } 
//	    // 🔁 keyword 없으면 → 전체 리스트 조회
//	    else {
//	        pjtList = projectMngService.getPjtList();
//	        log.info("📄 전체 목록 개수: " + pjtList.size());
//	    }
//
//	    // 💾 View에 리스트 전달
//	    model.addAttribute("pjtList", pjtList);
//	    model.addAttribute("currentPage", 1);
//	    model.addAttribute("totalPages", 10); // ✅ 이거 안 넘기면 NPE 뜸
	   
	    model.addAttribute("mainUrl", "pjt_mng/pjt_main");
	    return "home";

	}

	



	
	//2. 프로젝트 상세보기 페이지 호출 
	@GetMapping("/pjtDetail")
	public String getPjtDetail(@RequestParam("pjtSn") String pjtSn, Model model) {
	    log.info("getPjtDetail Called >>> " + pjtSn);
	    
	    CmmnMap pjtDetail = projectMngService.getPjtDetail(pjtSn);
	    model.addAttribute("pjt", pjtDetail);

	 	    
	    model.addAttribute("mainUrl", "pjt_mng/pjt_detail");
	    return "home";
	    
	}

	
	@GetMapping("/list")
	public String showProjectList(@RequestParam(required = false) String keyword, Model model) {
		List<CmmnMap> result = projectMngService.searchProjectMngList(keyword);    // 검색어 넘기기
	    model.addAttribute("pjtList", result);
	    
	    model.addAttribute("mainUrl", "pjt_mng/pjt_main");
	    return "home";
	   
	}
	
	
	
	
	@GetMapping("/getSavePjtForm")
	public String getSavePjtForm(HttpSession session, Model model) {
		
	    Object loginUser = session.getAttribute("loginUser");
	    boolean isAdmin = true;  // 임시로 항상 true로 설정 !!
	    

	    // 이후 사용자 관리자가 세팅해주면 교체하기 (주석풀기)
	    // loginUser 객체가 null이 아니고, UserVO 타입일 경우
//	    if (loginUser != null && loginUser instanceof com.kdt.KDT_PJT.sample.vo.UserVO) {
//	        com.kdt.KDT_PJT.sample.vo.UserVO user = (com.kdt.KDT_PJT.sample.vo.UserVO) loginUser;
//
//	        // 실제 UserVO에 getRole() 또는 getGrade(), getAuth() 등 확인 필요
//	        isAdmin = "관리자".equals(user.getRole());  // 💡 필드명이 getRole()이 아니면 맞게 바꿔야 해
//	    }

	    // isAdmin 값을 모델에 넣음
	    model.addAttribute("isAdmin", isAdmin);

	    // 화면 이동
	    model.addAttribute("mainUrl", "pjt_mng/pjt_reg_form");
	    return "home";
	    
	}

	
	@GetMapping("/pjtEditForm")
	public String getPjtEditForm(@RequestParam("pjtSn") String pjtSn, Model model) {
	    log.info("getPjtEditForm Called >>> " + pjtSn);
	    
	    CmmnMap pjtDetail = projectMngService.getPjtDetail(pjtSn);  // 기존 상세조회 사용
	    model.addAttribute("pjt", pjtDetail);

	    
	    model.addAttribute("mainUrl","pjt_mng/pjt_edit_form");
	    return "home";
	    
	}
	
	
	@PostMapping("/pjtEditSave")
	public String saveEditedProject(@ModelAttribute CmmnMap pjtData, RedirectAttributes redirectAttributes) {
	    log.info("saveEditedProject Called >>> " + pjtData);

	    String content = (String) pjtData.get("content");

	    Safelist customSafelist = Safelist.basicWithImages()
	    	    .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup", "caption")
	    	    .addAttributes("table", "style", "border", "cellpadding", "cellspacing")
	    	    .addAttributes("th", "style", "colspan", "rowspan")
	    	    .addAttributes("td", "style", "colspan", "rowspan")
	    	    .addAttributes("tr", "style")
	    	    .addAttributes("thead", "style")
	    	    .addAttributes("tbody", "style")
	    	    .addAttributes("tfoot", "style")
	    	    .addAttributes("col", "style", "span", "width")
	    	    .addAttributes("colgroup", "span", "width", "style")
	    	    .addAttributes("caption", "style")
	    	    .addAttributes("img", "style", "src", "alt", "width", "height")
	    	    .addProtocols("img", "src", "data", "http", "https");


	    String safeContent = Jsoup.clean(content, customSafelist);
	    pjtData.put("content", safeContent);

	    try {
	        projectMngService.updatePjtProc(pjtData);
	        String pjtSn = (String) pjtData.get("pjtSn");
	        return "redirect:/pjtDetail?pjtSn=" + pjtSn;
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("msg", "수정 중 오류 발생: " + e.getMessage());
	        return "redirect:/pjtEditForm?pjtSn=" + pjtData.get("pjtSn");
	    }
	}

	
	

	
	
	@PostMapping("/updatePjtProc")
	public String updatePjtProc(
	        @RequestParam("pjtSn") String pjtSn,
	        @RequestParam("pjtNm") String pjtNm,
	        @RequestParam(value = "empNm", required = false) String empNm,
	        @RequestParam(value = "pjtBgngDt", required = false)
	        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
	        @RequestParam(value = "pjtEndDt", required = false)
	        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt,
	        @RequestParam(value = "pjtSttsCd", required = false) String pjtSttsCd,
	        @RequestParam(value = "content", required = false) String content,
	        @RequestParam(value = "approvers", required = false) String approvers,
	        @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile,
	        @RequestParam(value = "oldFileName", required = false) String oldFileName,
	        @RequestParam(value = "oldOrgFileName", required = false) String oldOrgFileName
	) {

	    Safelist customSafelist = Safelist.basicWithImages()
	            .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup", "caption")
	            .addAttributes("table", "style", "border", "cellpadding", "cellspacing")
	            .addAttributes("th", "style", "colspan", "rowspan")
	            .addAttributes("td", "style", "colspan", "rowspan")
	            .addAttributes("tr", "style")
	            .addAttributes("thead", "style")
	            .addAttributes("tbody", "style")
	            .addAttributes("tfoot", "style")
	            .addAttributes("col", "style", "span", "width")
	            .addAttributes("colgroup", "span", "width", "style")
	            .addAttributes("caption", "style")
	            .addAttributes("img", "style", "src", "alt", "width", "height")
	            .addProtocols("img", "src", "data", "http", "https");

	    String safeContent = Jsoup.clean(content, customSafelist);

	    log.info("updatePjtProc Called >>> " + pjtSn);

	    CmmnMap params = new CmmnMap();
	    params.put("PJT_SN", pjtSn);
	    params.put("PJT_NM", pjtNm);
	    params.put("EMP_NM", empNm);
	    params.put("PJT_BGNG_DT", pjtBgngDt);
	    params.put("PJT_END_DT", pjtEndDt);
	    params.put("PJT_STTS_CD", pjtSttsCd);
	    params.put("content", safeContent);
	    params.put("APPROVERS", approvers);

	    String uploadDir = "C:/upload/";
	    String newFileName = oldFileName != null ? oldFileName : "";
	    String newOrgFileName = oldOrgFileName != null ? oldOrgFileName : "";

	    if (uploadFile != null && !uploadFile.isEmpty()) {
	        String extension = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."));
	        newFileName = java.util.UUID.randomUUID().toString() + extension;
	        newOrgFileName = uploadFile.getOriginalFilename();

	        File dir = new File(uploadDir);
	        if (!dir.exists()) dir.mkdirs();

	        File dest = new File(uploadDir + newFileName);

	        try {
	            uploadFile.transferTo(dest);
	            // 기존 파일 삭제
	            if (oldFileName != null && !oldFileName.isEmpty()) {
	                File prevFile = new File(uploadDir + oldFileName);
	                if (prevFile.exists()) prevFile.delete();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    params.put("ATCH_FILE_SN", newFileName);
	    params.put("ORG_FILE_NM", newOrgFileName);

	    // 마지막 수정일시 업데이트
	    LocalDateTime now = LocalDateTime.now();
	    String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	    params.put("LAST_MDFCN_DT", formatted);
	    params.put("LAST_MDFR_ID", "SYSTEM"); // 임시

	    projectMngService.updatePjtProc(params);

	    return "redirect:/pjtMng/getPjtList";
	}
	
	@GetMapping("/downloadFile")
	public ResponseEntity<Resource> downloadFile(
	        @RequestParam("fileName") String fileName,        // (uuid명)
	        @RequestParam("orgName") String orgName           // (원본명)
	) throws UnsupportedEncodingException {
	    String path = "C:/upload/" + fileName;
	    Resource resource = new FileSystemResource(path);

	    if (!resource.exists()) {
	        return ResponseEntity.notFound().build();
	    }

	    // 한글/공백 깨짐 대비
	    String encodedOrgName = URLEncoder.encode(orgName, "UTF-8").replaceAll("\\+", " ");

	    HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOrgName + "\"");
	    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
	    headers.add("Content-Transfer-Encoding", "binary");

	    return ResponseEntity.ok()
	            .headers(headers)
	            .body(resource);
	}



	
	
//	@PostMapping("/pjtMng/updatePjtProc")
//	public String updatePjtProc(CmmnMap pjtData
//								, 
//								// @RequestParam("pjtSn") int pjtSn,
//						        @RequestParam("pjtNm") String pjtNm,
//						        @RequestParam("empNm") String empNm,
//						        @RequestParam("pjtBgngDt") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
//						        @RequestParam("pjtEndDt") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt,
//						        @RequestParam("pjtSttsCd") String pjtSttsCd,
//						        @RequestParam("content") String content,
//						        @RequestParam("approvers") String approvers,
//						        @RequestParam("uploadFile") MultipartFile uploadFile,
//						        RedirectAttributes redirectAttributes) {
//		
//		
//	   // log.info("updatePjtProc Called >>> " + pjtData);
//
//		 log.info("updatePjtProc Called >>> " + pjtNm);
//		 log.info("updatePjtProc Called >>> " + empNm);
//	    
//	    
//	    /*
//	    try {
//	        //실제 DB 업데이트 처리--- 이지만 일단 상세페이지로 넘어가게 만들어 놓음 
//	        return "redirect:/pjtDetail?pjtSn=" + pjtData.get("pjtSn");
//	        
//	     // DB에 수정 처리 -- 나중에 적용해보기 
//	        //projectMngService.updatePjtProc(pjtData);
//
//	    } catch (Exception e) {
//	        // 실패 시 수정 페이지로 다시
//	        redirectAttributes.addFlashAttribute("msg", "수정 실패: " + e.getMessage());
//	        return "redirect:/pjtEditForm?pjtSn=" + pjtData.get("pjtSn");
//	    } */
//		 
//		 return "";
//	}



	
	@PostMapping("/savePjtProc")
	public String savePjtProc(
					    Model model,
					    @RequestParam("pjtNm") String pjtNm,
					    @RequestParam(value = "empNm", required = false) String empNm,
					    @RequestParam(value = "pjtBgngDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
					    @RequestParam(value = "pjtEndDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt,
					    @RequestParam(value = "pjtSttsCd", required = false) String pjtSttsCd,   // ✅ 추가된 부분
					    @RequestParam(value = "content", required = false) String content,
					    @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {
		
	    Safelist customSafelist = Safelist.basicWithImages()
	    	    .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup", "caption")
	    	    .addAttributes("table", "style", "border", "cellpadding", "cellspacing")
	    	    .addAttributes("th", "style", "colspan", "rowspan")
	    	    .addAttributes("td", "style", "colspan", "rowspan")
	    	    .addAttributes("tr", "style")
	    	    .addAttributes("thead", "style")
	    	    .addAttributes("tbody", "style")
	    	    .addAttributes("tfoot", "style")
	    	    .addAttributes("col", "style", "span", "width")
	    	    .addAttributes("colgroup", "span", "width", "style")
	    	    .addAttributes("caption", "style")
	    	    .addAttributes("img", "style", "src", "alt", "width", "height")
	    	    .addProtocols("img", "src", "data", "http", "https");

		String safeContent = Jsoup.clean(content, customSafelist);
		
		
		log.info("savePjtProc Called >>> ");
		
		CmmnMap params = new CmmnMap();
		log.info("pjtNm " + pjtNm);
		log.info("empNm" + empNm);
		log.info("pjtBgngDt " + pjtBgngDt);
		log.info("pjtEndDt " + pjtEndDt);
		
		
		// 파일 업로드
		if (uploadFile != null && !uploadFile.isEmpty()) {
		    String originalFileName = uploadFile.getOriginalFilename();
		    String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		    String uuidFileName = UUID.randomUUID().toString() + extension;

		    String uploadDir = "C:/upload/";
		    File dir = new File(uploadDir);
		    if (!dir.exists()) dir.mkdirs();

		    File dest = new File(uploadDir + uuidFileName);

		    try {
		        uploadFile.transferTo(dest);
		        params.put("ATCH_FILE_SN", uuidFileName);
		        params.put("ORG_FILE_NM", originalFileName);
		    } catch (IllegalStateException | IOException e) {
		        e.printStackTrace();
		    }
		}

		
		params.put("PJT_NM", pjtNm);
		params.put("EMP_NM", empNm);
		params.put("PJT_BGNG_DT", pjtBgngDt);
		params.put("PJT_END_DT", pjtEndDt);
		params.put("PJT_STTS_CD", pjtSttsCd);
		params.put("content", safeContent);
		
		LocalDateTime now = LocalDateTime.now(); // 현재 시간 가져오기
		String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); // 14자리 문자열로 포맷

		log.info("현재시간 (formatted): " + formatted);
		params.put("FRST_REG_DT", formatted); // DB에 저장할 값

		
		
		projectMngService.savePjtProc(params);		
		
		return "redirect:/pjtMng/getPjtList";

	}	
}
