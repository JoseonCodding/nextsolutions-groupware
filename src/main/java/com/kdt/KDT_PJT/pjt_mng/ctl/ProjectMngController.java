/*
 * package com.kdt.KDT_PJT.pjt_mng.ctl;
 * 
 * import java.time.LocalDate; import java.time.LocalDateTime; import
 * java.time.format.DateTimeFormatter; import java.util.List;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.format.annotation.DateTimeFormat; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.ModelAttribute; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * org.springframework.web.multipart.MultipartFile; import
 * org.springframework.web.servlet.mvc.support.RedirectAttributes;
 * 
 * import com.github.pagehelper.PageInfo; import
 * com.kdt.KDT_PJT.cmmn.map.CmmnMap; import
 * com.kdt.KDT_PJT.pjt_mng.svc.ProjectMngService;
 * 
 * import jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpSession; import lombok.RequiredArgsConstructor;
 */

/*
 * @Controller
 * 
 * @RequestMapping("/pjtMng")
 * 
 * @RequiredArgsConstructor public class ProjectMngController {
 * 
 * @Autowired private ProjectMngService projectMngService;
 * 
 * // log 사용을 위함 private final Logger log = LoggerFactory.getLogger(getClass());
 * 
 * 
 * // ✅ 프로젝트 목록 화면 조회 (검색 + 페이징 + 연속 순번)
 * 
 * @GetMapping("/getPjtList") public String getPjtList(
 * 
 * @RequestParam(value = "keyword", required = false) String keyword,
 * 
 * @RequestParam(value = "sort", required = false, defaultValue = "PJT_SN")
 * String sort,
 * 
 * @RequestParam(value = "order", required = false, defaultValue = "DESC")
 * String order,
 * 
 * @RequestParam(value = "pageNum", required = false, defaultValue = "1") int
 * pageNum,
 * 
 * @RequestParam(value = "pageSize",required = false, defaultValue = "10") int
 * pageSize, Model model ) {
 * 
 * log.
 * info("getPjtList >>> keyword={}, pageNum={}, pageSize={}, sort={}, order={}",
 * keyword, pageNum, pageSize, sort, order);
 * 
 * // ✅ PageHelper로 페이지네이션된 결과 가져오기 PageInfo<CmmnMap> page =
 * projectMngService.getProjectList(pageNum, pageSize, keyword);
 * 
 * // ✅ 순번 계산용 offset (0, 10, 20 …) int offset = (page.getPageNum() - 1) *
 * page.getPageSize();
 * 
 * // ✅ NPE 방지: list가 null이면 빈 리스트로 대체 List<CmmnMap> list = (page.getList() !=
 * null) ? page.getList() : java.util.Collections.emptyList();
 * 
 * // 뷰에 데이터 바인딩
 * 
 * model.addAttribute("pjtList", list); model.addAttribute("pageInfo", page);
 * model.addAttribute("offset", offset);
 * 
 * model.addAttribute("sort", sort); model.addAttribute("order", order);
 * 
 * // 검색어 유지 (검색창에 그대로 보이게) model.addAttribute("keyword", keyword);
 * 
 * // 메인 레이아웃에서 불러올 바디 템플릿 지정 model.addAttribute("mainUrl", "pjt_mng/pjt_main");
 * return "home"; }
 * 
 * 
 * // 프로젝트 상세보기 페이지 호출
 * 
 * @GetMapping("/pjtDetail") public String getPjtDetail(@RequestParam("pjtSn")
 * String pjtSn, Model model) { log.info("getPjtDetail Called >>> " + pjtSn);
 * 
 * CmmnMap pjtDetail = projectMngService.getPjtDetail(pjtSn);
 * model.addAttribute("pjt", pjtDetail);
 * 
 * 
 * model.addAttribute("mainUrl", "pjt_mng/pjt_detail"); return "home";
 * 
 * }
 * 
 * 
 * @GetMapping("/list") public String showProjectList(@RequestParam(required =
 * false) String keyword, Model model) { List<CmmnMap> result =
 * projectMngService.searchProjectMngList(keyword); // 검색어 넘기기
 * model.addAttribute("pjtList", result);
 * 
 * model.addAttribute("mainUrl", "pjt_mng/pjt_main"); return "home";
 * 
 * }
 * 
 * @GetMapping("/getSavePjtForm") public String getSavePjtForm(HttpSession
 * session, Model model) {
 * 
 * Object loginUser = session.getAttribute("loginUser"); boolean isAdmin = true;
 * // 임시로 항상 true로 설정 !!
 * 
 * 
 * // 이후 사용자 관리자가 세팅해주면 교체하기 (주석풀기) // loginUser 객체가 null이 아니고, UserVO 타입일 경우 //
 * if (loginUser != null && loginUser instanceof
 * com.kdt.KDT_PJT.sample.vo.UserVO) { // com.kdt.KDT_PJT.sample.vo.UserVO user
 * = (com.kdt.KDT_PJT.sample.vo.UserVO) loginUser; // // // 실제 UserVO에 getRole()
 * 또는 getGrade(), getAuth() 등 확인 필요 // isAdmin = "관리자".equals(user.getRole());
 * // 💡 필드명이 getRole()이 아니면 맞게 바꿔야 해 // }
 * 
 * // isAdmin 값을 모델에 넣음 model.addAttribute("isAdmin", isAdmin);
 * 
 * // 화면 이동 model.addAttribute("mainUrl", "pjt_mng/pjt_reg_form"); return
 * "home";
 * 
 * }
 * 
 * @GetMapping("/pjtEditForm") public String
 * getPjtEditForm(@RequestParam("pjtSn") String pjtSn, Model model) {
 * log.info("getPjtEditForm Called >>> " + pjtSn);
 * 
 * CmmnMap pjtDetail = projectMngService.getPjtDetail(pjtSn); // 기존 상세조회 사용
 * model.addAttribute("pjt", pjtDetail);
 * 
 * 
 * model.addAttribute("mainUrl","pjt_mng/pjt_edit_form"); return "home";
 * 
 * }
 * 
 * @GetMapping("/pjtRegForm") public String pjtRegForm(Model model) {
 * List<CmmnMap> approverList = projectMngService.getApproverList();
 * model.addAttribute("approverList", approverList);
 * 
 * model.addAttribute("mainUrl", "pjt_mng/pjt_reg_form"); return "home"; // 등록
 * 페이지 경로 }
 * 
 * 
 * @PostMapping("/pjtSave") // ← 폼의 th:action과 일치!! public String
 * saveProject(@ModelAttribute CmmnMap form,
 * 
 * @RequestParam(name="files", required=false) MultipartFile[] files,
 * HttpSession session, RedirectAttributes ra) { int pjtSn =
 * projectMngService.saveOrUpdateProject(form); if (files != null &&
 * files.length > 0) { String loginId = (String)
 * session.getAttribute("loginId"); projectMngService.saveProjectFiles(pjtSn,
 * files, loginId == null ? "system" : loginId); }
 * ra.addFlashAttribute("msg","저장되었습니다."); return
 * "redirect:/pjtMng/pjtDetail?pjtSn=" + pjtSn; }
 * 
 * 
 * 
 * @PostMapping("/pjtEditSave") public String saveEditedProject(@ModelAttribute
 * CmmnMap pjtData, RedirectAttributes redirectAttributes) {
 * log.info("saveEditedProject Called >>> " + pjtData);
 * 
 * try { projectMngService.updatePjtProc(pjtData); // 수정 저장 처리
 * 
 * // ✅ 저장 후 상세 페이지로 리디렉트 String pjtSn = (String) pjtData.get("pjtSn"); return
 * "redirect:/pjtDetail?pjtSn=" + pjtSn; // 상세 페이지 URL로 이동
 * 
 * } catch (Exception e) { redirectAttributes.addFlashAttribute("msg",
 * "수정 중 오류 발생: " + e.getMessage()); return "redirect:/pjtMng/pjtDetail…" +
 * pjtData.get("pjtSn"); // 다시 수정 폼으로 } }
 * 
 * 
 * 
 * @PostMapping("/updatePjtProc") public String updatePjtProc(
 * 
 * @RequestParam(value="pjtSn") String pjtSn,
 * 
 * @RequestParam(value="pjtNm") String pjtNm,
 * 
 * @RequestParam(value="empNm", required=false) String empNm,
 * 
 * @RequestParam(value="pjtBgngDt", required=false)
 * 
 * @org.springframework.format.annotation.DateTimeFormat(pattern="yyyy-MM-dd")
 * java.time.LocalDate pjtBgngDt,
 * 
 * @RequestParam(value="pjtEndDt", required=false)
 * 
 * @org.springframework.format.annotation.DateTimeFormat(pattern="yyyy-MM-dd")
 * java.time.LocalDate pjtEndDt,
 * 
 * @RequestParam(value="pjtSttsCd", required=false) String pjtSttsCd,
 * 
 * @RequestParam(value="content", required=false) String content, // ← 폼 name과
 * 동일 (소문자)
 * 
 * @RequestParam(value="approvers", required=false) String approvers,
 * 
 * @RequestParam(value="uploadFile", required=false)
 * org.springframework.web.multipart.MultipartFile uploadFile ){ var params =
 * new com.kdt.KDT_PJT.cmmn.map.CmmnMap(); params.put("PJT_SN", pjtSn);
 * params.put("PJT_NM", pjtNm); params.put("EMP_NM", empNm);
 * params.put("PJT_BGNG_DT", pjtBgngDt); params.put("PJT_END_DT", pjtEndDt);
 * params.put("PJT_STTS_CD", pjtSttsCd); params.put("CONTENT", content);
 * params.put("APPROVERS", approvers); if (uploadFile != null &&
 * !uploadFile.isEmpty()) { params.put("UPLOAD_ORG_NM",
 * uploadFile.getOriginalFilename()); // TODO: 저장 로직 있으면 서비스에서 처리 }
 * params.put("LAST_MDFCN_DT", java.time.LocalDateTime.now()
 * .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
 * params.put("LAST_MDFR_ID", "SYSTEM");
 * 
 * projectMngService.updatePjtProc(params); return
 * "redirect:/pjtMng/getPjtList"; }
 * 
 * }
 * 
 */