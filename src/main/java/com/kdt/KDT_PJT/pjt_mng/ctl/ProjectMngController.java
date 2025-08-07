package com.kdt.KDT_PJT.pjt_mng.ctl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.pjt_mng.svc.ProjcetMngService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/pjtMng")
@RequiredArgsConstructor
public class ProjectMngController {
	
	@Autowired
	ProjcetMngService projectMngService;
	
	// log 사용을 위함
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	
	
	// ✅ 프로젝트 목록 화면 조회 (검색어 유무에 따라 분기)
	@GetMapping("/getPjtList")
	public String getPjtList(
	        @RequestParam(required = false, name = "keyword") String keyword, Model model) {

	    // 🔍 검색어 로그 확인 (디버깅용)
	   System.out.println("getPjtList Called >>> keyword = " + keyword);

	    // 🗂 프로젝트 리스트 선언
	    List<CmmnMap> pjtList;

	    // 🔎 keyword가 있으면 → 검색 조건으로 조회
	    if (keyword != null && !keyword.isEmpty()) {
	        pjtList = projectMngService.searchProjectMngList(keyword);
	        log.info("🔍 검색 결과 개수: " + pjtList.size());
	    } 
	    // 🔁 keyword 없으면 → 전체 리스트 조회
	    else {
	        pjtList = projectMngService.getPjtList();
	        log.info("📄 전체 목록 개수: " + pjtList.size());
	    }

	    // 💾 View에 리스트 전달
	    model.addAttribute("pjtList", pjtList);

	    // 💡 pjt_mng 폴더 안의 pjt_main.html (또는 .jsp)로 이동
	    return "pjt_mng/pjt_main";
	}

	
	//2. 프로젝트 상세보기 페이지 호출 
	@GetMapping("/pjtDetail")
	public String getPjtDetail(@RequestParam("pjtSn") String pjtSn, Model model) {
	    log.info("getPjtDetail Called >>> " + pjtSn);
	    
	    CmmnMap pjtDetail = projectMngService.getPjtDetail(pjtSn);
	    model.addAttribute("pjt", pjtDetail);

	    return "pjt_mng/pjt_detail";
	}

	
	@GetMapping("/list")
	public String showProjectList(@RequestParam(required = false) String keyword, Model model) {
		List<CmmnMap> result = projectMngService.searchProjectMngList(keyword);    // 검색어 넘기기
	    model.addAttribute("pjtList", result);
	    
	    return "pjt_mng/pjt_main";    
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
	    return "pjt_mng/pjt_reg_form";
	}

	
	@GetMapping("/pjtEditForm")
	public String getPjtEditForm(@RequestParam("pjtSn") String pjtSn, Model model) {
	    log.info("getPjtEditForm Called >>> " + pjtSn);
	    
	    CmmnMap pjtDetail = projectMngService.getPjtDetail(pjtSn);  // 기존 상세조회 사용
	    model.addAttribute("pjt", pjtDetail);

	    return "pjt_mng/pjt_edit_form"; // views/pjt_mng/pjt_edit_form.html
	}
	
	
	@PostMapping("/pjtEditSave")
	public String saveEditedProject(@ModelAttribute CmmnMap pjtData, RedirectAttributes redirectAttributes) {
	    log.info("saveEditedProject Called >>> " + pjtData);

	    try {
	        projectMngService.updatePjtProc(pjtData); // 수정 저장 처리

	        // ✅ 저장 후 상세 페이지로 리디렉트
	        String pjtSn = (String) pjtData.get("pjtSn");
	        return "redirect:/pjtDetail?pjtSn=" + pjtSn; // 상세 페이지 URL로 이동

	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("msg", "수정 중 오류 발생: " + e.getMessage());
	        return "redirect:/pjtEditForm?pjtSn=" + pjtData.get("pjtSn"); // 다시 수정 폼으로
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
	        @RequestParam(value = "CONTENT", required = false) String content,
	        @RequestParam(value = "approvers", required = false) String approvers,
	        @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile

	) {
	    log.info("updatePjtProc Called >>> " + pjtSn);

	    CmmnMap params = new CmmnMap();
	    params.put("PJT_SN", pjtSn);
	    params.put("PJT_NM", pjtNm);
	    params.put("EMP_NM", empNm);
	    params.put("PJT_BGNG_DT", pjtBgngDt);
	    params.put("PJT_END_DT", pjtEndDt);
	    params.put("PJT_STTS_CD", pjtSttsCd);
	    params.put("CONTENT", content);
	    params.put("APPROVERS", approvers);



	    // 마지막 수정일시 업데이트
	    LocalDateTime now = LocalDateTime.now();
	    String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	    params.put("LAST_MDFCN_DT", formatted);
	    params.put("LAST_MDFR_ID", "SYSTEM"); // 임시로 SYSTEM. 나중에 세션 사용자 ID로 대체 가능

	    projectMngService.updatePjtProc(params);  // service에서 update 메서드 필요!

	    return "redirect:/pjtMng/getPjtList";
	}
	
	
	@PostMapping("/pjtMng/updatePjtProc")
	public String updatePjtProc(@ModelAttribute CmmnMap pjtData, RedirectAttributes redirectAttributes) {
	    log.info("updatePjtProc Called >>> " + pjtData);

	    try {
	        //실제 DB 업데이트 처리--- 이지만 일단 상세페이지로 넘어가게 만들어 놓음 
	        return "redirect:/pjtDetail?pjtSn=" + pjtData.get("pjtSn");
	        
	     // DB에 수정 처리 -- 나중에 적용해보기 
	        //projectMngService.updatePjtProc(pjtData);

	    } catch (Exception e) {
	        // 실패 시 수정 페이지로 다시
	        redirectAttributes.addFlashAttribute("msg", "수정 실패: " + e.getMessage());
	        return "redirect:/pjtEditForm?pjtSn=" + pjtData.get("pjtSn");
	    }
	}



	
	@PostMapping("/savePjtProc")
	public String savePjtProc(
	    Model model,
	    @RequestParam("pjtNm") String pjtNm,
	    @RequestParam(value = "empNm", required = false) String empNm,
	    @RequestParam(value = "pjtBgngDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtBgngDt,
	    @RequestParam(value = "pjtEndDt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate pjtEndDt,
	    @RequestParam(value = "pjtSttsCd", required = false) String pjtSttsCd   // ✅ 추가된 부분
	) {

		
		
		log.info("savePjtProc Called >>> ");
		
		CmmnMap params = new CmmnMap();
		log.info("pjtNm " + pjtNm);
		log.info("empNm" + empNm);
		log.info("pjtBgngDt " + pjtBgngDt);
		log.info("pjtEndDt " + pjtEndDt);
		
		
		params.put("PJT_NM", pjtNm);
		params.put("EMP_NM", empNm);
		params.put("PJT_BGNG_DT", pjtBgngDt);
		params.put("PJT_END_DT", pjtEndDt);
		params.put("PJT_STTS_CD", pjtSttsCd);

		
		LocalDateTime now = LocalDateTime.now(); // 현재 시간 가져오기
		String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")); // 14자리 문자열로 포맷

		log.info("현재시간 (formatted): " + formatted);
		params.put("FRST_REG_DT", formatted); // DB에 저장할 값

		
		
		projectMngService.savePjtProc(params);
		
		
		return "redirect:/pjtMng/getPjtList";

	}	
}
