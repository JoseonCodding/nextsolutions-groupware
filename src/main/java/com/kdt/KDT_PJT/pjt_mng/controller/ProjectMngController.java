package com.kdt.KDT_PJT.pjt_mng.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.pagehelper.PageInfo;
import com.kdt.KDT_PJT.cmmn.context.CompanyContext;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.svc.EmployeeService;
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

   @Value("${app.upload-dir}")
   private String uploadDir;

   // 로그 객체 (현재 클래스 이름으로 로거 생성)
   private final Logger log = LoggerFactory.getLogger(getClass());
   
   @ModelAttribute("navUrl")
	String navUrl() {
		return "pjt_mng/nav";
	}

   // ✅ 프로젝트 목록 화면 조회 (검색어 유무에 따라 분기)
   @GetMapping("/getPjtList")
   public String getPjtList(@RequestParam(required = false, name = "keyword") String keyword,
         HttpServletRequest request, Model model, HttpSession session
         , @RequestParam(required = false, name = "keywordType") String keywordType) {

	   // 디버깅 로그
	   log.info("keywordType(raw) = {}", keywordType);
	   log.info("keyword(raw) = {}", keyword);
	    
	   // ✅ null /대소문자 정리
	    String type = (keywordType == null) ? "" : keywordType;
	    if (keywordType == null) keywordType = "";  	 // 선택값 유지 용

	    
	     // ✅ 뷰에 현재 검색/정렬 상태 전달
	    model.addAttribute("keywordType", type);
	    model.addAttribute("keyword", keyword);  // 입력칸 초기화(유지하려면 keyword 넣어도 됨)

       

	    // ✅ 로그인 사용자 세션에서 가져오기 (세션 키: "loginUser")
       EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
      
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

      // 리스트 가져오기
      //PageInfo<CmmnMap> list = projectMngService.getProjectList(pageNum, pageSize, keyword);
      PageInfo<CmmnMap> list = null;
      switch (keywordType) {
      case "writer":
    	  log.info(" sortType = writer ");
    	  list = projectMngService.getProjectListOrderByWriter(pageNum, pageSize, keyword);
          break;
      case "project":
    	  log.info(" sortType = project ");
    	  list = projectMngService.getProjectListOrderByProject(pageNum, pageSize, keyword);
          break;
      case "status":
    	  log.info(" sortType = status ");
    	  list = projectMngService.getProjectListOrderByStatus(pageNum, pageSize, keyword);
          break;
      case "my":
    	  log.info(" sortType = my ");
    	  keyword = loginUser.getEmployeeId();    	  
    	  list = projectMngService.getProjectListMyProject(pageNum, pageSize, keyword);
          break;     
      case "app":
    	  log.info(" sortType = app ");    	  
    	  keyword = loginUser.getEmployeeId();    	  
    	  list = projectMngService.getProjectListMyApprovalTodoCount(pageNum, pageSize, keyword);
          break;
      case "myApproval":
    	  log.info(" sortType = myApproval ");    	  
    	  //keyword = loginUser.getEmployeeId();    	  
    	  list = projectMngService.getProjectMyApprovalList(pageNum, pageSize, keyword, loginUser.getEmployeeId());
          break;         
          
          
      default:
          // 기본 정렬(현재 쓰던 메서드)
    	  list = projectMngService.getProjectList(pageNum, pageSize, keyword);
      }
      
      
      
      // ③ model에 값 담기

      if (list == null || list.getList() == null || list.getList().isEmpty()) {
         log.warn("⚠️ 프로젝트 리스트가 비어있거나 null입니다.");
         model.addAttribute("pjtList", List.of()); // 빈 리스트로 넘기기 (NPE 방지)
      } else {
         log.info("✅ 프로젝트 리스트 로드 성공. 개수: " + list.getList().size());
         model.addAttribute("pjtList", list.getList());
      }

      // DB 전체 개수 조회
      int totalCount = projectMngService.getTotalCount();
      model.addAttribute("totalCount", totalCount);

      // DB에서 PJT_STTS_CD가 '진행중'인 개수 조회
      int progressCount = projectMngService.getProgressCount();
      model.addAttribute("progressCount", progressCount);

      // DB에서 PJT_STTS_CD가 '완료'인 개수 조회
      int completeCount = projectMngService.getCompleteCount();
      model.addAttribute("completeCount", completeCount);

      // DB에서 PJT_STTS_CD가 '대기'인 개수 조회
      int pendingCount = projectMngService.getPendingCount();
      

       int myProjectCount = projectMngService.countMyProjects(loginUser.getEmployeeId());
       int myApprovalTodoCount = projectMngService.countMyPendingApprovals(loginUser.getEmployeeId());
            
       model.addAttribute("myProjectCount", myProjectCount);
       model.addAttribute("myApprovalTodoCount", myApprovalTodoCount);   

      
      model.addAttribute("pendingCount", pendingCount);

      model.addAttribute("pjtList", list.getList()); // 현재 페이지 데이터
      model.addAttribute("pageInfo", list); // 페이징 정보
      
      
      // 권한 받아오기
      model.addAttribute("loginUser", loginUser);

      model.addAttribute("mainUrl", "pjt_mng/pjt_main");
      
      
      
      /* 페이지네이션 필규버전 (시작) */
      //totalCount = projectMngService.getTotalCount();					// 위쪽 DB 전체 개수 조회와 동일 (에러나면 totalCount 앞에 int 넣어요)
      
      
      int ppCount = totalCount;
      
      switch (keywordType) {
      case "writer":
    	  ppCount =  projectMngService.countWriter(keyword);
          break;
      case "project":
    	  ppCount =projectMngService.countProject(keyword);
          break;
      case "status":
    	    ppCount = projectMngService.countStatus(keyword);
    	    break;
      case "my":
    	  ppCount = myProjectCount;
          break;     
      case "app":
    	  
          break;
      case "myApproval":
    	  ppCount =myApprovalTodoCount;
          break;         
  
    
      }
      
      int totalPages = (int) Math.ceil((double) ppCount / pageSize);	// 총 페이지 수를 계산
      																	// pageSize : 페이지당 나타낼 페이지 수 (위에서 int pageSize = 10;으로 1페이지당 10라고 선언하고 있어요. 에러나면 int pageSize = 10;을 윗줄에 추가하세요)
      																	// 전체 DB 개수를 페이지 당 나타낼 개수로 나눈 값을 double타입(소수)으로 정확하게 계산하고, 소수점 이하를 올림(Math.ceil)하고, int타입(정수)로 만들었어요. 이렇게 하면 DB가 25개일때, 10으로 나눠서 2.5로 계산한 다음, 총 3페이지를 보여줄 수 있어요. 

      int startPage = Math.max(1, pageNum - 2);							// 페이지네이션 - 시작 페이지를 계산
      																	// 1미만의 숫자가 나타나는 것을 방지합니다. '1'과 '현재페이지 -2' 중에 더 큰 값을 나타내도록 했어요.

      int endPage = Math.min(totalPages, startPage + 4);				// 페이지네이션 - 마지막 페이지를 계산
      																	// 총 페이지 수보다 큰 숫자가 나타나는 것을 방지합니다. '총 페이지 수'와 '시작 페이지 +4' 중에 더 작은 값을 나타내도록 했어요.
      
      if ((endPage - startPage + 1) < 5) {
          startPage = Math.max(1, endPage - 4);
      }
      
      if (totalPages == 0) endPage = 1;									// 보여줄 DB가 없을 때, 페이지가 1과 0으로 표기되는 현상 방지
      
      
      // 위에서 선언하고 계산한 값들을 뷰(html)로 보내기 위해서 Model에 담을거에요.
      // 매개변수로 Model이 들어있어야해요. (현재 @GetMapping("/getPjtList") 어노테이션 걸려있는, public String getPjtList 메서드에, Model model이 들어있음)
      // 만약 에러나면 Model model을 매개변수에 추가하세요. (주의: 동일한 변수명이 페이지네이션 필규버전 코드 아래에 추가되면 페이지네이션이 박살남)
      model.addAttribute("totalCount", totalCount);
      model.addAttribute("startPage", startPage);
      model.addAttribute("endPage", endPage);
      model.addAttribute("totalPages", totalPages);
      model.addAttribute("pageNum", pageNum);
      model.addAttribute("pageSize", pageSize);
      /* 페이지네이션 필규버전 (끝) */
      
      
      
      return "navTap";

   }  
   
   
   
   // 내프로젝트만 눌러서 볼수 있는거 
   @GetMapping("/pjtMng/pjtList")
   public String getPjtList(HttpSession session, Model model,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false, defaultValue = "N") String my,
                            @RequestParam(required = false, defaultValue = "N") String approval,
                            @RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "10") int pageSize) {

       String employeeId = (String) session.getAttribute("employeeId"); // 로그인 시 세팅해둔 값
       if (employeeId == null) employeeId = "";   // 안전(일단주석처리)

       // 상단 카드 숫자
       int myProjectCount = projectMngService.countMyProjects(employeeId);
       int myApprovalTodoCount = projectMngService.countMyPendingApprovals(employeeId);
       
       // ✅ 여기서 offset 계산 (서비스 호출 전에!)
       if (pageNum < 1) pageNum = 1;
       int offset = (pageNum - 1) * pageSize;
       

       // 목록 파라미터
       Map<String, Object> param = new HashMap<>();
       param.put("keyword", keyword);
       param.put("employeeId", employeeId);
       param.put("myOnly", "Y".equalsIgnoreCase(my) ? 1 : 0);
       param.put("approvalOnly", "Y".equalsIgnoreCase(approval) ? 1 : 0); // ⬅ 추가
       param.put("pageSize", pageSize);
       param.put("offset", offset);


       List<CmmnMap> list = projectMngService.getProjectList(param);
       int total = projectMngService.getProjectListCount(param);

       model.addAttribute("list", list);
       model.addAttribute("total", total);
       model.addAttribute("keyword", keyword);
       model.addAttribute("my", my);
       model.addAttribute("approval", approval); // ⬅ 추가(뷰에서 배지 표시용)

       model.addAttribute("myProjectCount", myProjectCount);
       model.addAttribute("myApprovalTodoCount", myApprovalTodoCount);

       model.addAttribute("mainUrl", "pjt_mng/pjt_list");
       return "navTap";
   }


   @Autowired
   private EmployeeService employeeService;
   
   
   
   @GetMapping("/list")
   public String showProjectList(@RequestParam(required = false) String keyword, Model model) {
      List<CmmnMap> result = projectMngService.searchProjectMngList(keyword); // 검색어 넘기기
      model.addAttribute("pjtList", result);

      model.addAttribute("mainUrl", "pjt_mng/pjt_main");
      return "navTap";

   }

   @GetMapping("/getSavePjtForm")
   public String getSavePjtForm(HttpSession session, Model model) {

      EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

      if (!("프로젝트".equals(loginUser.getRole()) || "대표".equals(loginUser.getRole()))) {
         model.addAttribute("errorMsg", "권한이 없습니다.");
         return "error/403";
      }

      boolean isAdmin = "프로젝트".equals(loginUser.getRole()) || "대표".equals(loginUser.getRole());
      List<CmmnMap> approverList = projectMngService.selectApproverCandidates();

      model.addAttribute("isAdmin", isAdmin);
      model.addAttribute("loginUser", loginUser);
      model.addAttribute("approverList", approverList);
      model.addAttribute("mainUrl", "pjt_mng/pjt_reg_form");
      return "navTap";
   }


	@GetMapping("/pjtEditForm")
	public String getPjtEditForm(@RequestParam("pjtSn") int pjtSn,
														Model model, 
														HttpSession session) {
		log.info("getPjtEditForm Called >>> {}", pjtSn);
		
	    List<CmmnMap> approverList = projectMngService.selectApproverCandidates();
	    EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
	    boolean isAdmin = "프로젝트".equals(loginUser.getRole()) || "대표".equals(loginUser.getRole());
		
	    
	    
	    //프로젝트 권한 체크 : 권한없는 사람이 접근했을때 막는거 
	    if (!("프로젝트".equals(loginUser.getRole()) || "대표".equals(loginUser.getRole()))) {
	        model.addAttribute("errorMsg", "권한이 없습니다.");
	        return "error/403"; // 접근 불가 페이지
	    }
	    
	    // 1) 프로젝트 상세
		CmmnMap pjtDetail = projectMngService.getPjtDetail(pjtSn);
		log.debug("DETAIL TB_PJT_APR={}", pjtDetail.get("TB_PJT_APR")); // 값 확인
				
		
		// ✅ 전체 직원 리스트 조회
		List<CmmnMap> employeeList = projectMngService.getEmployeeList( );
		model.addAttribute("employeeList", employeeList);
		
		
		

		
	    // 3) 모델 바인딩
		model.addAttribute("isAdmin", isAdmin);
	    model.addAttribute("approverList", approverList);
		model.addAttribute("pjt", pjtDetail);
		model.addAttribute("mainUrl", "pjt_mng/pjt_edit_form");
		return "navTap";
	}


   @GetMapping("/pjtDetail")
   public String pjtDetail(Model model,
						   HttpSession session,
						   @RequestParam("pjtSn") int pjtSn,
						   @RequestParam(value = "pageNum", required = false) Integer pageNum,			// 뒤로가기용 파라미터
				           @RequestParam(value = "keywordType", required = false) String keywordType, 	// 뒤로가기용 파라미터
				           @RequestParam(value = "keyword", required = false) String keyword 			// 뒤로가기용 파라미터
				           ) {

      CmmnMap pjt = projectMngService.getPjtDetail(pjtSn);

      log.debug("DETAIL pjtSn={}, TB_PJT_APR={}", pjtSn, pjt.get("TB_PJT_APR"));
      log.debug("DETAIL keys={}", pjt.keySet());
      
      model.addAttribute("pageNum", pageNum);			// 뒤로가기용 파라미터
      model.addAttribute("keywordType", keywordType);	// 뒤로가기용 파라미터
      model.addAttribute("keyword", keyword);			// 뒤로가기용 파라미터
      model.addAttribute("pjt", pjt);
      model.addAttribute("mainUrl", "pjt_mng/pjt_detail");
      return "navTap";
   }

   @PostMapping("/updateApprover")
   public String updateApprover(@RequestParam("PJT_SN") int pjtSn, @RequestParam("TB_PJT_APR") String approver) {
      CmmnMap param = new CmmnMap();
      param.put("PJT_SN", pjtSn);
      param.put("TB_PJT_APR", approver);

      String queryId = "com.kdt.pjt_pjt.mapper.pjt_mng.PjtMngMapper.updateApprover";
      // cmmnDao.update(queryId, param);

      return "redirect:/pjtMng/pjtDetail?pjtSn=" + pjtSn;
   }

   @PostMapping("/pjtEditSave")
   public String saveEditedProject(@ModelAttribute CmmnMap pjtData, RedirectAttributes redirectAttributes) {
      log.info("saveEditedProject Called >>> " + pjtData);

      String content = (String) pjtData.get("content");

      Safelist customSafelist = Safelist.basicWithImages()
            .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup", "caption")
            .addAttributes("table", "style", "border", "cellpadding", "cellspacing")
            .addAttributes("th", "style", "colspan", "rowspan").addAttributes("td", "style", "colspan", "rowspan")
            .addAttributes("tr", "style").addAttributes("thead", "style").addAttributes("tbody", "style")
            .addAttributes("tfoot", "style").addAttributes("col", "style", "span", "width")
            .addAttributes("colgroup", "span", "width", "style").addAttributes("caption", "style")
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
   public String updatePjtProc(HttpSession session, @RequestParam("pjtSn") String pjtSn, @RequestParam("pjtNm") String pjtNm,
		   @RequestParam(value = "employeeId", required = false) String employeeId,
		   @RequestParam(value = "TB_PJT_APR", required = false) String TB_PJT_APR,
         @RequestParam(value = "empNm", required = false) String empNm,
         @RequestParam(value = "ver", required = false) Integer ver,
         @RequestParam(value = "gid", required = false) Integer gid,         
         @RequestParam(value = "FRST_REG_DT", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime FRST_REG_DT,
         @RequestParam(value = "pjtBgngDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
         @RequestParam(value = "pjtEndDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt,
         @RequestParam(value = "pjtSttsCd", required = false) String pjtSttsCd,
         @RequestParam(value = "content", required = false) String content,
         @RequestParam(value = "approvers", required = false) String approvers,
         @RequestParam(value = "uploadFile1", required = false) MultipartFile uploadFile1,
         @RequestParam(value = "oldFileName1", required = false) String oldFileName1,
         @RequestParam(value = "oldOrgFileName1", required = false) String oldOrgFileName1,
         @RequestParam(value = "uploadFile2", required = false) MultipartFile uploadFile2,
         @RequestParam(value = "oldFileName2", required = false) String oldFileName2,
         @RequestParam(value = "oldOrgFileName2", required = false) String oldOrgFileName2,
         @RequestParam(value = "uploadFile3", required = false) MultipartFile uploadFile3,
         @RequestParam(value = "oldFileName3", required = false) String oldFileName3,
         @RequestParam(value = "oldOrgFileName3", required = false) String oldOrgFileName3,
         @RequestParam(value = "delFileSlots1", required = false) Boolean delFileSlots1,
         @RequestParam(value = "delFileSlots2", required = false) Boolean delFileSlots2,
         @RequestParam(value = "delFileSlots3", required = false) Boolean delFileSlots3,
   @RequestParam(value = "firstSign", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime firstSign,
   @RequestParam(value = "secondSign", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime secondSign,
   @RequestParam(value = "approvedBy", required = false) String approvedBy,
   @RequestParam(value = "page", required = false) Integer page, RedirectAttributes ra
		   )
   
   {
	  
   
      Safelist customSafelist = Safelist.basicWithImages()
            .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup", "caption")
            .addAttributes("table", "style", "border", "cellpadding", "cellspacing")
            .addAttributes("th", "style", "colspan", "rowspan").addAttributes("td", "style", "colspan", "rowspan")
            .addAttributes("tr", "style").addAttributes("thead", "style").addAttributes("tbody", "style")
            .addAttributes("tfoot", "style").addAttributes("col", "style", "span", "width")
            .addAttributes("colgroup", "span", "width", "style").addAttributes("caption", "style")
            .addAttributes("img", "style", "src", "alt", "width", "height")
            .addProtocols("img", "src", "data", "http", "https");

      String safeContent = Jsoup.clean(content, customSafelist);

      log.info("updatePjtProc Called >>> " + pjtSn);

      CmmnMap params = new CmmnMap();
      params.put("ver", ver);
      params.put("PJT_SN", pjtSn);
      params.put("PJT_NM", pjtNm);
      params.put("employeeId", employeeId);
      params.put("TB_PJT_APR", TB_PJT_APR);
      params.put("PJT_BGNG_DT", pjtBgngDt);
      params.put("PJT_END_DT", pjtEndDt);
      params.put("PJT_STTS_CD", pjtSttsCd);
      params.put("content", safeContent);
      params.put("APPROVERS", approvers);
      params.put("firstSign", firstSign);
      params.put("secondSign", secondSign);
      params.put("approvedBy", approvedBy);

      

      EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
      String newFileName = oldFileName1 != null ? oldFileName1 : "";
      String newOrgFileName = oldOrgFileName1 != null ? oldOrgFileName1 : "";
      
     

      //  업로드 할 수 있는  파일 갯수 1~3까지 
      if (uploadFile1 != null && !uploadFile1.isEmpty()) {
         String extension = uploadFile1.getOriginalFilename()
               .substring(uploadFile1.getOriginalFilename().lastIndexOf("."));
         newFileName = java.util.UUID.randomUUID().toString() + extension;
         newOrgFileName = uploadFile1.getOriginalFilename();

         File dir = new File(uploadDir);
         if (!dir.exists())
            dir.mkdirs();

         File dest = new File(uploadDir + newFileName);

         try {
            uploadFile1.transferTo(dest.toPath());
            if (oldFileName1 != null && !oldFileName1.isEmpty()) {
               File prevFile = new File(uploadDir + oldFileName1);
               if (prevFile.exists()) prevFile.delete();
            }
         } catch (Exception e) {
            log.error("파일1 업로드 실패", e);
         }
      }
      params.put("ATCH_FILE_SN1", newFileName);
      params.put("ORG_FILE_NM1", newOrgFileName);
      
      
      newFileName = oldFileName2 != null ? oldFileName2 : "";
      newOrgFileName = oldOrgFileName2 != null ? oldOrgFileName2 : "";

      
      if (uploadFile2 != null && !uploadFile2.isEmpty()) {
         String extension = uploadFile2.getOriginalFilename()
               .substring(uploadFile2.getOriginalFilename().lastIndexOf("."));
         newFileName = java.util.UUID.randomUUID().toString() + extension;
         newOrgFileName = uploadFile2.getOriginalFilename();

         File dir = new File(uploadDir);
         if (!dir.exists())
            dir.mkdirs();

         File dest = new File(uploadDir + newFileName);

         try {
            uploadFile2.transferTo(dest.toPath());
            if (oldFileName2 != null && !oldFileName2.isEmpty()) {
               File prevFile = new File(uploadDir + oldFileName2);
               if (prevFile.exists()) prevFile.delete();
            }
         } catch (Exception e) {
            log.error("파일2 업로드 실패", e);
         }
      }
      params.put("ATCH_FILE_SN2", newFileName);
      params.put("ORG_FILE_NM2", newOrgFileName);
      
      
      newFileName = oldFileName3 != null ? oldFileName3 : "";
      newOrgFileName = oldOrgFileName3 != null ? oldOrgFileName3 : "";
      

      if (uploadFile3 != null && !uploadFile3.isEmpty()) {
         String extension = uploadFile3.getOriginalFilename()
               .substring(uploadFile3.getOriginalFilename().lastIndexOf("."));
         newFileName = java.util.UUID.randomUUID().toString() + extension;
         newOrgFileName = uploadFile3.getOriginalFilename();

         File dir = new File(uploadDir);
         if (!dir.exists())
            dir.mkdirs();

         File dest = new File(uploadDir + newFileName);

         try {
            uploadFile3.transferTo(dest.toPath());
            if (oldFileName3 != null && !oldFileName3.isEmpty()) {
               File prevFile = new File(uploadDir + oldFileName3);
               if (prevFile.exists()) prevFile.delete();
            }
         } catch (Exception e) {
            log.error("파일3 업로드 실패", e);
         }
      }

      params.put("ATCH_FILE_SN3", newFileName);
      params.put("ORG_FILE_NM3", newOrgFileName);

      
      // 마지막 수정일시 업데이트
      LocalDateTime now = LocalDateTime.now();
      String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
      params.put("LAST_MDFCN_DT", formatted);
      params.put("LAST_MDFR_ID", loginUser.getEmployeeId());
      
     
      
      
      CmmnMap pjt = projectMngService.getPjtDetailForVersion(pjtSn);
            
      
      if(pjtSttsCd.equals("진행중")) {
    	  
    	  params.put("gid", gid);
    	  params.put("ver", ver+1);
    	  params.put("FRST_REG_DT",FRST_REG_DT);
    	  params.put("companyId", CompanyContext.get());

    	  if(delFileSlots1!= null && delFileSlots1) {
    		  params.put("ATCH_FILE_SN1", null);
    	      params.put("ORG_FILE_NM1", null);
    	  }
    	  if(delFileSlots2!= null && delFileSlots2) {
    		  params.put("ATCH_FILE_SN2", null);
    	      params.put("ORG_FILE_NM2", null);
    	  }
    	  if(delFileSlots3!= null && delFileSlots3) {
    		  params.put("ATCH_FILE_SN3", null);
    	      params.put("ORG_FILE_NM3", null);
    	  }
    	  
       	  
    	  pjtSn = projectMngService.savePjtProcForVersion(params)+"";      
      }else {
    	  projectMngService.updatePjtProc(params); 
      }
      
      
   // ✅ 저장/갱신 끝난 시점에서, 리다이렉트 파라미터 세팅
      ra.addAttribute("pjtSn", pjtSn);     // 정보화면이 필요로 하는 PK
      if (page != null) {
          ra.addAttribute("page", page);   // 원래 보던 페이지 유지
      }
            

      return "redirect:/pjtMng/pjtDetail?pjtSn=" + pjtSn;
   }

   @GetMapping("/downloadFile")
   public ResponseEntity<Resource> downloadFile(
         @RequestParam("fileName") String fileName,
         @RequestParam("orgName") String orgName
   ) throws UnsupportedEncodingException {
      java.nio.file.Path baseDir = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize();
      java.nio.file.Path target = baseDir.resolve(fileName).normalize();

      if (!target.startsWith(baseDir)) {
         return ResponseEntity.status(403).build();
      }

      java.io.File file = target.toFile();
      if (!file.exists() || !file.isFile()) {
         return ResponseEntity.notFound().build();
      }

      Resource resource = new FileSystemResource(file);
      String encodedOrgName = URLEncoder.encode(orgName, "UTF-8").replaceAll("\\+", " ");
      encodedOrgName = encodedOrgName.replace("\r", "").replace("\n", "").replace("\"", "");

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOrgName + "\"");
      headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
      return ResponseEntity.ok().headers(headers).body(resource);
   }

   @PostMapping("/savePjtProc")
   public String savePjtProc(HttpSession session, Model model, @RequestParam("pjtNm") String pjtNm,
         @RequestParam(value = "employeeId", required = false) String employeeId,
         @RequestParam(value = "pjtBgngDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
         @RequestParam(value = "pjtEndDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt,
         @RequestParam(value = "pjtSttsCd", required = false) String pjtSttsCd, // ✅ 추가된 부분
         @RequestParam(value = "content", required = false) String content,
         @RequestParam(value = "TB_PJT_APR", required = false) String TB_PJT_APR,
         @RequestParam(value = "uploadFile1", required = false) MultipartFile uploadFile1,
         @RequestParam(value = "uploadFile2", required = false) MultipartFile uploadFile2,
         @RequestParam(value = "uploadFile3", required = false) MultipartFile uploadFile3) {

      Safelist customSafelist = Safelist.basicWithImages()
            .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td", "col", "colgroup", "caption")
            .addAttributes("table", "style", "border", "cellpadding", "cellspacing")
            .addAttributes("th", "style", "colspan", "rowspan").addAttributes("td", "style", "colspan", "rowspan")
            .addAttributes("tr", "style").addAttributes("thead", "style").addAttributes("tbody", "style")
            .addAttributes("tfoot", "style").addAttributes("col", "style", "span", "width")
            .addAttributes("colgroup", "span", "width", "style").addAttributes("caption", "style")
            .addAttributes("img", "style", "src", "alt", "width", "height")
            .addProtocols("img", "src", "data", "http", "https");

      String safeContent = Jsoup.clean(content, customSafelist);

      CmmnMap params = new CmmnMap();
      

      // 파일 업로드
      if (uploadFile1 != null && !uploadFile1.isEmpty()) {
         String originalFileName1 = uploadFile1.getOriginalFilename();
         String extension = originalFileName1.substring(originalFileName1.lastIndexOf("."));
         String uuidFileName = UUID.randomUUID().toString() + extension;

         File dir = new File(uploadDir);
         if (!dir.exists()) dir.mkdirs();

         File dest = new File(uploadDir + uuidFileName);
         try {
            uploadFile1.transferTo(dest.toPath());
            params.put("ATCH_FILE_SN1", uuidFileName);
            params.put("ORG_FILE_NM1", originalFileName1);
         } catch (IllegalStateException | IOException e) {
            log.error("파일1 업로드 실패", e);
         }
      }
      if (uploadFile2 != null && !uploadFile2.isEmpty()) {
          String originalFileName2 = uploadFile2.getOriginalFilename();
          String extension = originalFileName2.substring(originalFileName2.lastIndexOf("."));
          String uuidFileName = UUID.randomUUID().toString() + extension;

          File dir2 = new File(uploadDir);
          if (!dir2.exists()) dir2.mkdirs();

          File dest2 = new File(uploadDir + uuidFileName);
          try {
             uploadFile2.transferTo(dest2.toPath());
             params.put("ATCH_FILE_SN2", uuidFileName);
             params.put("ORG_FILE_NM2", originalFileName2);
          } catch (IllegalStateException | IOException e) {
             log.error("파일2 업로드 실패", e);
          }
       }
      if (uploadFile3 != null && !uploadFile3.isEmpty()) {
          String originalFileName3 = uploadFile3.getOriginalFilename();
          String extension = originalFileName3.substring(originalFileName3.lastIndexOf("."));
          String uuidFileName = UUID.randomUUID().toString() + extension;

          File dir3 = new File(uploadDir);
          if (!dir3.exists()) dir3.mkdirs();

          File dest3 = new File(uploadDir + uuidFileName);
          try {
             uploadFile3.transferTo(dest3.toPath());
             params.put("ATCH_FILE_SN3", uuidFileName);
             params.put("ORG_FILE_NM3", originalFileName3);
          } catch (IllegalStateException | IOException e) {
             log.error("파일3 업로드 실패", e);
          }
       }

      EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

      params.put("TB_PJT_APR", TB_PJT_APR);
      params.put("PJT_NM", pjtNm);
      //params.put("employeeId", loginUser.getEmployeeId());
      params.put("employeeId", employeeId);
      params.put("PJT_BGNG_DT", pjtBgngDt);
      params.put("PJT_END_DT", pjtEndDt);
      params.put("PJT_STTS_CD", pjtSttsCd);
      params.put("content", safeContent);

      LocalDateTime now = LocalDateTime.now(); // 현재 시간 가져오기
      String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); // 14자리 문자열로 포맷

      params.put("FRST_REG_DT", formatted); // DB에 저장할 값

      int pjtSn = projectMngService.maxPjtSn();
      params.put("PJT_SN", pjtSn+1);
      params.put("companyId", CompanyContext.get());

      projectMngService.savePjtProc(params);

      return "redirect:/pjtMng/getPjtList";

   }

   // 칸반 보드 화면
   @GetMapping("/kanban")
   public String kanban(HttpSession session, Model model) {
      EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
      boolean isAdmin = "프로젝트".equals(loginUser.getRole()) || "대표".equals(loginUser.getRole());
      model.addAttribute("kanbanList", projectMngService.getKanbanList());
      model.addAttribute("isAdmin", isAdmin);
      model.addAttribute("mainUrl", "pjt_mng/pjt_kanban");
      return "navTap";
   }

   // 칸반 상태 변경 (AJAX)
   @PostMapping("/updateStatus")
   @ResponseBody
   public Map<String, Object> updateStatus(
         @RequestParam int pjtSn,
         @RequestParam String status,
         HttpSession session) {
      Map<String, Object> result = new HashMap<>();
      EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
      boolean isAdmin = "프로젝트".equals(loginUser.getRole()) || "대표".equals(loginUser.getRole());
      if (!isAdmin) {
         result.put("success", false);
         result.put("msg", "권한이 없습니다.");
         return result;
      }
      List<String> allowed = List.of("대기", "진행중", "완료");
      if (!allowed.contains(status)) {
         result.put("success", false);
         result.put("msg", "올바르지 않은 상태값입니다.");
         return result;
      }
      int updated = projectMngService.updatePjtStatus(pjtSn, status);
      result.put("success", updated > 0);
      return result;
   }
}
