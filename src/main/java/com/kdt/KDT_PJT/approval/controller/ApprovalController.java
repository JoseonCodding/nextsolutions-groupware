package com.kdt.KDT_PJT.approval.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.approval.mapper.ApprovalTemplateMapper;
import com.kdt.KDT_PJT.approval.model.ApprovalDTO;
import com.kdt.KDT_PJT.approval.model.ApprovalDocDTO;
import com.kdt.KDT_PJT.approval.model.ApproverDTO;
import com.kdt.KDT_PJT.attend.model.LeaveMapper;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.notification.NotificationMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/approval")
public class ApprovalController {
   
   // 콘텐츠 영역 상단 헤더용  
   @ModelAttribute("navUrl")
   public String navUrl() {
      
      return "approval/approvalNav";
   }
   
   @Value("${app.upload-dir}")
   private String uploadDir;

   @Autowired
   ApprovalMapper approvalMapper;
   @Autowired
   ApprovalTemplateMapper templateMapper;
   @Autowired
   LeaveMapper leaveMapper;
   @Autowired
   NotificationMapper notificationMapper;
   
   @InitBinder
   public void initBinder(WebDataBinder binder) {
       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
       dateFormat.setLenient(false);
       binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
   }
   
   // 연차 승인 시, 유효성 검사를 위한 날짜 비교 메서드
   private Date truncateTime(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}
   
   @RequestMapping("/main")
   public String approvalMain(
           Model model,
           HttpServletRequest request,
           @RequestParam(name = "page", defaultValue = "1") int page,
           @RequestParam(name = "type", required = false) String type,
           @RequestParam(name = "status", required = false) String status) {

       HttpSession session = request.getSession(false);
       EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;

       List<ApprovalDTO> approvalData;
       int totalPages;
       int startPage;
       int endPage;
       int size = 10;

       if (loginUser == null) {
           approvalData = List.of();
           totalPages = 0;
           startPage = 1;
           endPage = 1;
       } else {
           String role = loginUser.getRole();
           String employeeId = loginUser.getEmployeeId();
           int companyId = loginUser.getCompanyId();
           boolean isAdmin = "대표".equals(role);

           if ("자유양식".equals(type)) {
               // 자유양식만 표시 (페이지네이션 없음)
               approvalData = new ArrayList<>();
               List<ApprovalDocDTO> freeDocs = templateMapper.selectDocList(companyId, employeeId, isAdmin);
               for (ApprovalDocDTO fd : freeDocs) {
                   if (status != null && !status.isEmpty() && !status.equals(fd.getStatus())) continue;
                   ApprovalDTO dto = new ApprovalDTO();
                   dto.setDocId("free-" + fd.getDocId());
                   dto.setDocType("자유양식");
                   dto.setTitle(fd.getTitle());
                   dto.setStatus(fd.getStatus());
                   dto.setWriter(fd.getWriterName());
                   dto.setCreatedAt(fd.getCreatedAt());
                   approvalData.add(dto);
               }
               totalPages = 0;
               startPage = 1;
               endPage = 1;
           } else {
               int offset = (page - 1) * size;
               int totalCount = approvalMapper.approvalCountByRole(role, type, status, employeeId, companyId);
               totalPages = (int) Math.ceil((double) totalCount / size);
               startPage = Math.max(1, page - 2);
               endPage = Math.min(totalPages, startPage + 4);
               if ((endPage - startPage + 1) < 5 && (endPage == totalPages || startPage == 1)) {
                   startPage = Math.max(1, endPage - 4);
               }
               if (totalPages == 0) endPage = 1;
               approvalData = new ArrayList<>(
                   approvalMapper.approvalDataByRole(offset, size, role, type, status, employeeId, companyId));

               // 타입 필터 없을 때 자유양식 문서도 함께 표시
               if (type == null || type.isEmpty()) {
                   List<ApprovalDocDTO> freeDocs = templateMapper.selectDocList(companyId, employeeId, isAdmin);
                   for (ApprovalDocDTO fd : freeDocs) {
                       if (status != null && !status.isEmpty() && !status.equals(fd.getStatus())) continue;
                       ApprovalDTO dto = new ApprovalDTO();
                       dto.setDocId("free-" + fd.getDocId());
                       dto.setDocType("자유양식");
                       dto.setTitle(fd.getTitle());
                       dto.setStatus(fd.getStatus());
                       dto.setWriter(fd.getWriterName());
                       dto.setCreatedAt(fd.getCreatedAt());
                       approvalData.add(dto);
                   }
               }
           }
       }

       model.addAttribute("approvalData", approvalData);
       model.addAttribute("page", page);
       model.addAttribute("totalPages", totalPages);
       model.addAttribute("startPage", startPage);
       model.addAttribute("endPage", endPage);
       model.addAttribute("type", type);
       model.addAttribute("status", status);
       model.addAttribute("mainUrl", "approval/approvalMain");

       return "navTap";
   }



   
   @RequestMapping("/viewer")
   public String approvalViewer(
       Model model,
       RedirectAttributes redirectAttributes,
       HttpServletRequest request,
       @RequestParam("docId") String docId,
       @RequestParam(name = "page", defaultValue = "1") int page,
       @RequestParam(name = "type", required = false) String type,
       @RequestParam(name = "status", required = false) String status) {

       HttpSession session = request.getSession(false);
       EmployeeDto loginUser = (session != null) ? 
           (EmployeeDto) session.getAttribute("loginUser") : null;

       // 1. 로그인 여부 체크
       if (loginUser == null) {
           return "redirect:/login?error=auth";
       }

       // 자유양식 결재 → doc_viewer로 위임
       if (docId.startsWith("free-")) {
           String actualId = docId.substring(5);
           return "redirect:/approval/doc/view?docId=" + actualId;
       }

       // 2. 권한 필터 내장된 view() 호출
       ApprovalDTO approvalData = approvalMapper.view(
           docId,
           loginUser.getRole(),
           loginUser.getEmployeeId(),
           type,
           status,
           loginUser.getCompanyId()
       );

       // 3. 조회 결과 없으면 접근 차단 (권한 없음 or 없는 문서)
       if (approvalData == null) {
           return "redirect:/approval/main?error=forbidden";
       }
       
       // 결재권한자 목록 추가 조회  
       List<ApproverDTO> approvers = approvalMapper.selectApproversByDocType(approvalData.getDocType());
       approvalData.setApprovers(approvers);

       // 4. 정상 데이터 모델에 담기
       model.addAttribute("approvalData", approvalData);
       model.addAttribute("loginUser", loginUser);
       model.addAttribute("approvalData", approvalData);
       model.addAttribute("page", page);
       model.addAttribute("type", type);
       model.addAttribute("status", status);
       model.addAttribute("mainUrl", "approval/approvalViewer");

       return "navTap";
   }


    
   @GetMapping("/downloadFile")
   public ResponseEntity<Resource> downloadFile(
       @RequestParam("fileName") String fileName,
       @RequestParam("orgName") String orgName
   ) throws UnsupportedEncodingException {

       java.nio.file.Path baseDir = java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize();
       java.nio.file.Path target = baseDir.resolve(fileName).normalize();

       // 디렉토리 탈출 방지
       if (!target.startsWith(baseDir)) {
           return ResponseEntity.status(403).build();
       }

       java.io.File file = target.toFile();
       if (!file.exists() || !file.isFile()) {
           return ResponseEntity.notFound().build();
       }

       Resource resource = new FileSystemResource(file);

       String encodedOrgName = URLEncoder.encode(orgName, "UTF-8").replaceAll("\\+", " ");
       // 헤더 주입 방지: 개행/쿼트 제거
       encodedOrgName = encodedOrgName.replace("\r", "").replace("\n", "").replace("\"", "");

       HttpHeaders headers = new HttpHeaders();
       headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedOrgName + "\"");
       headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
       return ResponseEntity.ok().headers(headers).body(resource);
   }



   @RequestMapping("/delete")
   public String approvalDelete(
           HttpServletRequest request,
           RedirectAttributes redirectAttributes,
           @RequestParam("docId") String docId,
           @RequestParam(name = "page", defaultValue = "1") int page,
           @RequestParam(name = "type", required = false) String type,
           @RequestParam(name = "status", required = false) String status) {

       HttpSession session = request.getSession(false);
       EmployeeDto loginUser = (session != null) ?
           (EmployeeDto) session.getAttribute("loginUser") : null;

       if (loginUser == null) {
           return "redirect:/login?error=auth";
       }

       // 권한/가드 포함 조회
       ApprovalDTO doc = approvalMapper.view(
           docId, loginUser.getRole(), loginUser.getEmployeeId(), type, status, loginUser.getCompanyId()
       );
       if (doc == null) {
           return "redirect:/approval/main?error=forbidden";
       }

       // 문서 타입 가드: 연차/근태만 '삭제' 허용
       if (!"연차".equals(doc.getDocType()) && !"근태".equals(doc.getDocType())) {
           return "redirect:/approval/main?error=deleteNotAllowed";
       }

       // 상태=대기만 삭제 허용
       if (!"대기".equals(doc.getStatus())) {
           return "redirect:/approval/main?error=forbidden";
       }

       // 작성자 본인만 허용
       if (!loginUser.getEmployeeId().equals(doc.getWriterId())) {
           return "redirect:/approval/main?error=forbidden";
       }

       String pkId = docId.split("-")[1].trim();

       // 실제 '삭제' 동작: 연차 → state_type NULL, 근태 → status NULL
       int updated = 0;
       if ("연차".equals(doc.getDocType())) {
           updated = approvalMapper.softDeleteLeave(pkId);
       } else if ("근태".equals(doc.getDocType())) {
           updated = approvalMapper.softDeleteAttendance(pkId);
       }

       if (updated == 0) {
           return "redirect:/approval/main?error=notUpdated";
       }

       // 삭제 이후 페이지 재계산
       int size = 10;
       int totalCount = approvalMapper.approvalCountByRole(
           loginUser.getRole(), type, status, loginUser.getEmployeeId(), loginUser.getCompanyId()
       );
       int totalPages = (int) Math.ceil((double) totalCount / size);
       int deletePage = page > totalPages ? totalPages : page;
       if (totalPages == 0) deletePage = 1;

       redirectAttributes.addAttribute("page", deletePage);
       redirectAttributes.addAttribute("type", type == null ? "" : type);
       redirectAttributes.addAttribute("status", status == null ? "" : status);

       return "redirect:/approval/main";
   }



    
    @GetMapping("/edit")
    public String approvalEditForm(
            Model model,
            HttpServletRequest request,
            @RequestParam("docId") String docId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;

        ApprovalDTO editData = approvalMapper.view(
            docId, loginUser.getRole(), loginUser.getEmployeeId(), type, status, loginUser.getCompanyId()
        );
        if (editData == null) return "redirect:/approval/main?error=forbidden";

        // 타입 가드: 연차/근태만 수정 허용
        if (!"연차".equals(editData.getDocType()) && !"근태".equals(editData.getDocType())) {
            return "redirect:/approval/main?error=forbidden";
        }

        // 상태=대기만 수정 허용
        if (!"대기".equals(editData.getStatus())) {
            return "redirect:/approval/main?error=forbidden";
        }

        // 작성자 본인만 수정 폼 접근 허용
        if (!loginUser.getEmployeeId().equals(editData.getWriterId())) {
            return "redirect:/approval/main?error=forbidden";
        }

        model.addAttribute("editData", editData);
        model.addAttribute("page", page);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("mainUrl", "approval/approvalEditForm");
        return "navTap";
    }




    
    @PostMapping("/edit")
    public String approvalEditProc(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @ModelAttribute ApprovalDTO editData,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "docType") String docType,
            @RequestParam(name = "actions", required = false) List<String> actions) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;

        ApprovalDTO current = approvalMapper.view(
            editData.getDocId(), loginUser.getRole(), loginUser.getEmployeeId(), type, status, loginUser.getCompanyId()
        );
        if (current == null) return "redirect:/approval/main?error=forbidden";

        // 타입 가드: 연차/근태만 수정 허용
        if (!"연차".equals(current.getDocType()) && !"근태".equals(current.getDocType())) {
            return "redirect:/approval/main?error=forbidden";
        }
        // 상태=대기만 수정 허용
        if (!"대기".equals(current.getStatus())) {
            return "redirect:/approval/main?error=forbidden";
        }
        // 작성자 본인만 수정 허용
        if (!loginUser.getEmployeeId().equals(current.getWriterId())) {
            return "redirect:/approval/main?error=forbidden";
        }

        // --- 실제 UPDATE 실행 (연차/근태만) ---
        String pkId = editData.getDocId().split("-")[1].trim();
        
        // XSS 방어용 content 정화
        if (editData.getContent() != null) {
            String cleanContent = Jsoup.clean(editData.getContent(), Safelist.none());
            editData.setContent(cleanContent);
        }

        if ("연차".equals(docType)) {
            // 연차: create_reason(=title), used_reason(=content), used_date(=leaveUsedDate)
            int updated = approvalMapper.editLeave(pkId, editData);
            if (updated == 0) {
                return "redirect:/approval/main?error=notUpdated";
            }
        } else if ("근태".equals(docType)) {
            // 근태: 체크박스 → timeInout 계산
            if (actions != null && !actions.isEmpty()) {
                if (actions.contains("IN") && actions.contains("OUT")) {
                    editData.setTimeInout("출퇴근");
                } else if (actions.contains("IN")) {
                    editData.setTimeInout("출근");
                } else if (actions.contains("OUT")) {
                    editData.setTimeInout("퇴근");
                }
            }
            // 근태: modification_reason(=content), time_inout 업데이트
            int updated = approvalMapper.editAttendance(pkId, editData);
            if (updated == 0) {
                return "redirect:/approval/main?error=notUpdated";
            }
        }

        redirectAttributes.addAttribute("docId", editData.getDocId());
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("type", type);
        redirectAttributes.addAttribute("status", status);
        return "redirect:/approval/viewer";
    }
    
    @PostMapping("/approve")
    @Transactional
    public String approvalApprove(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam("docId") String docId) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;

        ApprovalDTO doc = approvalMapper.view(
            docId,
            loginUser.getRole(),
            loginUser.getEmployeeId(),
            null,
            null,
            loginUser.getCompanyId()
        );
        if (doc == null) return "redirect:/approval/main?error=forbidden";

        String[] parts = docId.split("-");
        if (parts.length != 2) return "redirect:/approval/main?error=badDocId";
        String pkId = parts[1].trim();

        String docType = doc.getDocType();
        String currentStatus = doc.getStatus();
        String role = loginUser.getRole();
        String approverId = loginUser.getEmployeeId();
        
        // 연차 유효성검사 - 오늘 이전 날짜는 승인 방어
        if ("연차".equals(docType)) {
            if (doc.getLeaveUsedDate() != null) {
                Date today = new Date();
                if (doc.getLeaveUsedDate().before(truncateTime(today))) {
                    // 오늘 이전 날짜면 승인 불가로 리다이렉트 (원하는 에러 처리 경로로 수정 가능)
                    redirectAttributes.addAttribute("docId", docId);
                    redirectAttributes.addAttribute("error", "pastDate");
                    return "redirect:/approval/viewer";
                }
            }
        }
        
        // 근태: 1차 승인 즉시 완료 + approvedBy 저장
        if ("근태".equals(docType) && "대기".equals(currentStatus)
                && ("대표".equals(role) || "근태".equals(role))) {
            approvalMapper.approveAttendance(pkId, doc.getTimeInout(), approverId, role);
        }
        // 연차: 1차 승인 즉시 완료 + 역할별 사인 기록 + approvedBy 저장
        else if ("연차".equals(docType) && "대기".equals(currentStatus)
                && ("대표".equals(role) || "근태".equals(role))) {
            int pk = Integer.parseInt(pkId);
            int updated = approvalMapper.approveLeave(pk, role, approverId);
            if (updated == 0) return "redirect:/approval/main?error=notUpdated";
            leaveMapper.insertScheduleRest(pkId);
        }
        // 공지사항: 게시판 단독 결재 → '대기' -> '완료' + approvedBy 저장
        else if ("공지사항".equals(docType)) {
            if ("대기".equals(currentStatus) && "게시판".equals(role)) {
                int updated = approvalMapper.updateStatusNotice(pkId, "완료", currentStatus, approverId);
                if (updated == 0) return "redirect:/approval/main?error=notUpdated";
            } else {
                return "redirect:/approval/main?error=forbidden";
            }
        }
        // 프로젝트: 대표 최종결재 → '대기' -> '진행중' + approvedBy 저장
        else if ("프로젝트".equals(docType)) {
            if ("대기".equals(currentStatus) && "대표".equals(role)) {
                int updated = approvalMapper.updateStatusProject(pkId, "진행중", currentStatus, approverId);
                if (updated == 0) return "redirect:/approval/main?error=notUpdated";
            } else {
                return "redirect:/approval/main?error=forbidden";
            }
        }

        // 승인 알림 → 신청자에게
        if (doc.getWriterId() != null && !doc.getWriterId().equals(approverId)) {
            notificationMapper.insert(
                loginUser.getCompanyId(),
                doc.getWriterId(),
                loginUser.getEmpNm(),
                "APPROVED",
                "[" + docType + "] 결재가 승인되었습니다.",
                "/approval/viewer?docId=" + docId
            );
        }

        redirectAttributes.addAttribute("docId", docId);
        return "redirect:/approval/viewer";
    }



    @PostMapping("/reject")
    @Transactional
    public String approvalReject(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam("docId") String docId) {

        HttpSession session = request.getSession(false);
        EmployeeDto loginUser = (session != null) ? (EmployeeDto) session.getAttribute("loginUser") : null;

        ApprovalDTO doc = approvalMapper.view(
            docId,
            loginUser.getRole(),
            loginUser.getEmployeeId(),
            null,
            null,
            loginUser.getCompanyId()
        );
        if (doc == null) return "redirect:/approval/main?error=forbidden";

        String[] parts = docId.split("-");
        if (parts.length != 2) return "redirect:/approval/main?error=badDocId";
        String pkId = parts[1].trim();

        String docType = doc.getDocType();
        String currentStatus = doc.getStatus();
        String role = loginUser.getRole();
        String approverId = loginUser.getEmployeeId();

        // 근태: 1차 반려 + approvedBy 저장
        if ("근태".equals(docType) && "대기".equals(currentStatus)
                && ("대표".equals(role) || "근태".equals(role))) {
            int pk = Integer.parseInt(pkId);
            int updated = approvalMapper.rejectAttendance(pk, "반려", role, approverId);
            if (updated == 0) return "redirect:/approval/main?error=notUpdated";
        }
        // 연차: 1차 반려 + 역할별 사인 기록 + approvedBy 저장
        else if ("연차".equals(docType) && "대기".equals(currentStatus)
                && ("대표".equals(role) || "근태".equals(role))) {
            int pk = Integer.parseInt(pkId);
            int updated = approvalMapper.rejectLeave(pk, "반려", role, approverId);
            if (updated == 0) return "redirect:/approval/main?error=notUpdated";
        }
        // 공지사항: 게시판 단독 반려 → '대기' -> '반려' + approvedBy 저장
        else if ("공지사항".equals(docType)) {
            if ("대기".equals(currentStatus) && "게시판".equals(role)) {
                int updated = approvalMapper.updateStatusNotice(pkId, "반려", currentStatus, approverId);
                if (updated == 0) return "redirect:/approval/main?error=notUpdated";
            } else {
                return "redirect:/approval/main?error=forbidden";
            }
        }
        // 프로젝트: 대표 반려만 허용 + approvedBy 저장
        else if ("프로젝트".equals(docType)) {
            if ("대기".equals(currentStatus) && "대표".equals(role)) {
                int updated = approvalMapper.updateStatusProject(pkId, "반려", currentStatus, approverId);
                if (updated == 0) return "redirect:/approval/main?error=notUpdated";
            } else {
                return "redirect:/approval/main?error=forbidden";
            }
        }

        // 반려 알림 → 신청자에게
        if (doc.getWriterId() != null && !doc.getWriterId().equals(approverId)) {
            notificationMapper.insert(
                loginUser.getCompanyId(),
                doc.getWriterId(),
                loginUser.getEmpNm(),
                "REJECTED",
                "[" + docType + "] 결재가 반려되었습니다.",
                "/approval/viewer?docId=" + docId
            );
        }

        redirectAttributes.addAttribute("docId", docId);
        return "redirect:/approval/viewer";
    }
}
