package com.kdt.KDT_PJT.pjt_mng.ctl;

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

	private static final Object offset = null;

	@Autowired
	private ProjectMngService projectMngService;

	// log 사용을 위함
	private final Logger log = LoggerFactory.getLogger(getClass());

	// ✅ 프로젝트 목록 화면 조회 (검색어 유무에 따라 분기)
	@GetMapping("/getPjtList")
	public String getPjtList(@RequestParam(required = false, name = "keyword") String keyword,
			HttpServletRequest request, Model model, HttpSession session) {


	    
	    

	    
	    EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
	    System.out.println("getPjtList Called >>> employeeId = " + loginUser.getEmployeeId());
		
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

		// 리스트 가져오기
		PageInfo<CmmnMap> list = projectMngService.getProjectList(pageNum, pageSize, keyword);
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

		model.addAttribute("mainUrl", "pjt_mng/pjt_main");
		return "home";

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
	    return "home";
	}


	// 결재자 리스트 디비에 받아오는거
	@GetMapping("/project/edit")
	public String editProject(@RequestParam int pjtSn, Model model) {
		CmmnMap pjt = projectMngService.getProjectWithApprover(pjtSn);
		model.addAttribute("pjt", pjt);
		return "pjt_mng/pjt_edit_form";
	}

	// 결재자 리스트 불러오는거
	@Autowired
	private EmployeeService employeeService; // 팀원 EmployeeService 사용

	private Object cmmnDao;

	// 프로젝트 등록 폼 열기
	@GetMapping("/project/register")
	public String projectRegisterForm(Model model) {
		// 사원 목록 가져오기
		model.addAttribute("employeeList", projectMngService.getApproverCandidates());

		// 등록 페이지로 이동
		return "pjt_mng/pjt_reg_form";
	}

	@GetMapping("/list")
	public String showProjectList(@RequestParam(required = false) String keyword, Model model) {
		List<CmmnMap> result = projectMngService.searchProjectMngList(keyword); // 검색어 넘기기
		model.addAttribute("pjtList", result);

		model.addAttribute("mainUrl", "pjt_mng/pjt_main");
		return "home";

	}

	@GetMapping("/getSavePjtForm")
	public String getSavePjtForm(HttpSession session, Model model) {

		EmployeeDto loginUser =(EmployeeDto)session.getAttribute("loginUser");
		boolean isAdmin = true; // 임시로 항상 true로 설정 !!

	    List<CmmnMap> approverList = projectMngService.selectApproverCandidates();

		//isAdmin 값을 모델에 넣음
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("id", loginUser.getEmployeeId());
	    model.addAttribute("approverList", approverList);

		// 화면 이동
		model.addAttribute("mainUrl", "pjt_mng/pjt_reg_form");
		return "home";

	}

	@GetMapping("/pjtEditForm")
	public String getPjtEditForm(@RequestParam("pjtSn") int pjtSn, Model model) {
		log.info("getPjtEditForm Called >>> {}", pjtSn);

		CmmnMap pjtDetail = projectMngService.getPjtDetail(pjtSn);
		log.debug("DETAIL TB_PJT_APR={}", pjtDetail.get("TB_PJT_APR")); // 값 확인

		model.addAttribute("pjt", pjtDetail);
		model.addAttribute("mainUrl", "pjt_mng/pjt_edit_form");
		return "home";
	}

	@GetMapping("/pjtDetail")
	public String pjtDetail(@RequestParam("pjtSn") int pjtSn, Model model, HttpSession session) {

		CmmnMap pjt = projectMngService.getPjtDetail(pjtSn);

		log.debug("DETAIL pjtSn={}, TB_PJT_APR={}", pjtSn, pjt.get("TB_PJT_APR"));
		log.debug("DETAIL keys={}", pjt.keySet());
		model.addAttribute("pjt", pjt);
		model.addAttribute("mainUrl", "pjt_mng/pjt_detail");
		return "home";
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
	public String updatePjtProc(@RequestParam("pjtSn") String pjtSn, @RequestParam("pjtNm") String pjtNm,
			@RequestParam(value = "empNm", required = false) String empNm,
			@RequestParam(value = "pjtBgngDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
			@RequestParam(value = "pjtEndDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt,
			@RequestParam(value = "pjtSttsCd", required = false) String pjtSttsCd,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "approvers", required = false) String approvers,
			@RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile,
			@RequestParam(value = "oldFileName", required = false) String oldFileName,
			@RequestParam(value = "oldOrgFileName", required = false) String oldOrgFileName) {

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
			String extension = uploadFile.getOriginalFilename()
					.substring(uploadFile.getOriginalFilename().lastIndexOf("."));
			newFileName = java.util.UUID.randomUUID().toString() + extension;
			newOrgFileName = uploadFile.getOriginalFilename();

			File dir = new File(uploadDir);
			if (!dir.exists())
				dir.mkdirs();

			File dest = new File(uploadDir + newFileName);

			try {
				uploadFile.transferTo(dest);
				// 기존 파일 삭제
				if (oldFileName != null && !oldFileName.isEmpty()) {
					File prevFile = new File(uploadDir + oldFileName);
					if (prevFile.exists())
						prevFile.delete();
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

		return "redirect:/pjtMng/pjtDetail?pjtSn=" + pjtSn;
	}

	@GetMapping("/downloadFile")
	public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName, // (uuid명)
			@RequestParam("orgName") String orgName // (원본명)
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

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	@PostMapping("/savePjtProc")
	public String savePjtProc(HttpSession session, Model model, @RequestParam("pjtNm") String pjtNm,
			@RequestParam(value = "empNm", required = false) String empNm,
			@RequestParam(value = "pjtBgngDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
			@RequestParam(value = "pjtEndDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt,
			@RequestParam(value = "pjtSttsCd", required = false) String pjtSttsCd, // ✅ 추가된 부분
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {

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
			if (!dir.exists())
				dir.mkdirs();

			File dest = new File(uploadDir + uuidFileName);

			try {
				uploadFile.transferTo(dest);
				params.put("ATCH_FILE_SN", uuidFileName);
				params.put("ORG_FILE_NM", originalFileName);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}

		EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");

		params.put("PJT_NM", pjtNm);
		params.put("employeeId", loginUser.getEmployeeId());
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
