package com.kdt.KDT_PJT.attend.ctl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// OpenPDF
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;

@Controller
@RequestMapping("/attend")
@RequiredArgsConstructor
public class AttendController2 {

    private final AttendMapper attendMapper;

    // === 1) 결재 신청 저장 ===
    @PostMapping("/save")
    public String attendSave(
            @RequestParam("workDate") String workDate,                // yyyy-MM-dd
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "actions", required = false) String[] actionsArr,
            HttpSession session,
            RedirectAttributes ra
    ) {
        EmployeeDto loginUser = (EmployeeDto) session.getAttribute("loginUser");
        String employeeId = (loginUser != null) ? loginUser.getEmployeeId() : null;

        if (employeeId == null || workDate == null || workDate.isBlank()) {
            ra.addFlashAttribute("msg", "필수 정보가 누락되었습니다.");
            return "redirect:/attend";
        }

        List<String> actions = (actionsArr == null) ? List.of() : Arrays.asList(actionsArr);
        if (actions.isEmpty()) {
            ra.addFlashAttribute("msg", "정정 항목(정상출근/정상퇴근)을 선택하세요.");
            return "redirect:/attend";
        }

        LocalDateTime now = LocalDateTime.now();
        int inUpdated = 0, outUpdated = 0;

        if (actions.contains("IN")) {
            inUpdated = attendMapper.fixInByEmpAndDate(employeeId, workDate, now, title, content);
        }
        if (actions.contains("OUT")) {
            outUpdated = attendMapper.fixOutByEmpAndDate(employeeId, workDate, now, title, content);
        }

        int total = inUpdated + outUpdated;
        if (total > 0) {
            ra.addFlashAttribute("msg",
                    "정상 처리되었습니다. (출근 수정: " + inUpdated + "건, 퇴근 수정: " + outUpdated + "건)");
        } else {
            ra.addFlashAttribute("msg", "대상 기록이 없거나 상태가 ‘대기’가 아닙니다.");
        }
        return "redirect:/attend";
    }

 // === 2) 변경이력 목록(검색 + 페이징) ===
    @GetMapping("/attendList/search")
    public String attendListPage(
            @RequestParam(name = "fromDate", required = false) String fromDate,    // yyyy-MM-dd
            @RequestParam(name = "toDate", required = false) String toDate,        // yyyy-MM-dd
            @RequestParam(name = "empNm", required = false) String empNm,
            @RequestParam(name = "modifiedBy", required = false) String modifiedBy,
            @RequestParam(name = "stateType", required = false) String stateType,
            @RequestParam(name = "page", defaultValue = "1") int page,             // 1-based
            @RequestParam(name = "size", defaultValue = "10") int size,            // page size
            Model model
    ) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        int offset = (page - 1) * size;

        int total = attendMapper.countAttendListHistory(fromDate, toDate, empNm, modifiedBy, stateType);
        List<AttendDTO> list = attendMapper.searchAttendListHistoryPaged(
                fromDate, toDate, empNm, modifiedBy, stateType, size, offset);

        int totalPages = (int) Math.ceil(total / (double) size);

        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("empNm", empNm);
        model.addAttribute("modifiedBy", modifiedBy);
        model.addAttribute("stateType", stateType);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("mainData", list);

        model.addAttribute("navUrl", "/attend/nav");
        model.addAttribute("mainUrl", "/attend/attendList"); // navTap이 @{${mainUrl}}면 앞에 슬래시 포함 권장
        return "navTap";
    }

    // === 3) PDF 다운로드 (같은 필터, 전체 내보내기) ===
    @GetMapping(value = "/attendList/search/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void exportAttendListPdf(
            @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(name = "toDate", required = false) String toDate,
            @RequestParam(name = "empNm", required = false) String empNm,
            @RequestParam(name = "modifiedBy", required = false) String modifiedBy,
            @RequestParam(name = "stateType", required = false) String stateType,
            HttpServletResponse resp
    ) throws IOException {
        List<AttendDTO> list = attendMapper.searchAttendListHistoryPaged(
                fromDate, toDate, empNm, modifiedBy, stateType, Integer.MAX_VALUE, 0);

        String fileBase = "attendance_changes";
        String encoded = URLEncoder.encode(fileBase, StandardCharsets.UTF_8);
        resp.setContentType(MediaType.APPLICATION_PDF_VALUE);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + encoded + ".pdf\"");
        writePdf(list, resp);
    }


    // === PDF Helper (OpenPDF, 한글 폰트 임베드) ===
    private void writePdf(List<AttendDTO> list, HttpServletResponse resp) {
        try (java.io.OutputStream os = resp.getOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, os);
            doc.open();

            // 1) 한글 폰트 임베드 (classpath/fonts/NotoSansKR-Regular.ttf 필요)
            ClassPathResource fontRes = new ClassPathResource("fonts/NotoSansKR-Regular.ttf");
            byte[] fontBytes;
            try (java.io.InputStream fis = fontRes.getInputStream()) {
                fontBytes = fis.readAllBytes();
            }
            BaseFont base = BaseFont.createFont(
                    "NotoSansKR-Regular.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    true, fontBytes, null
            );

            Font titleFont  = new Font(base, 16, Font.BOLD);
            Font headerFont = new Font(base, 10, Font.BOLD);
            Font cellFont   = new Font(base, 9,  Font.NORMAL);

            // 2) 제목
            Paragraph title = new Paragraph("근태 변경 이력", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            doc.add(title);

            // 3) 테이블 (10열: 근무일/사번/이름/출근/퇴근/근무시간/상태/수정자/수정일/사유)
            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{12,12,12,8,8,10,10,10,10,20});
            addHeader(table, headerFont, "근무일","사번","이름","출근","퇴근","근무시간","상태","수정자","수정일","사유");

            var d  = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            var hm = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

            for (AttendDTO a : list) {
                String workDate = formatLocalDate(extractLdt(a.getCheckInTime()), d);
                String in       = formatLocalTime(extractLdt(a.getCheckInTime()), hm);
                String out      = formatLocalTime(extractLdt(a.getCheckOutTime()), hm);
                String wh       = (a.getWorkHours()==null) ? "" : String.valueOf(a.getWorkHours());
                String st       = (a.getNormalWork()==null) ? "" : (a.getNormalWork() ? "정상" : "비정상");
                String modAt    = formatLocalDate(extractLdt(a.getModifiedAt()), d);

                addCell(table, cellFont, workDate);
                addCell(table, cellFont, ns(a.getEmployeeId()));
                addCell(table, cellFont, ns(a.getEmpNm()));
                addCell(table, cellFont, in);
                addCell(table, cellFont, out);
                addCell(table, cellFont, wh);
                addCell(table, cellFont, st);
                addCell(table, cellFont, ns(a.getModifiedBy()));
                addCell(table, cellFont, modAt);
                addCell(table, cellFont, ns(a.getModificationReason()));
            }

            doc.add(table);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("PDF export failed: " + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
        }
    }

    // ===== 유틸 =====
    private static String ns(String s){ return s==null ? "" : s; }

    private static java.time.LocalDateTime extractLdt(Object t) {
        if (t == null) return null;
        if (t instanceof java.time.LocalDateTime ldt) return ldt;
        if (t instanceof java.sql.Timestamp ts)       return ts.toLocalDateTime();
        if (t instanceof java.util.Date dt)           return java.time.LocalDateTime.ofInstant(dt.toInstant(), java.time.ZoneId.systemDefault());
        return null;
    }
    private static String formatLocalDate(java.time.LocalDateTime ldt, java.time.format.DateTimeFormatter f) {
        return (ldt == null) ? "" : ldt.toLocalDate().format(f);
    }
    private static String formatLocalTime(java.time.LocalDateTime ldt, java.time.format.DateTimeFormatter f) {
        return (ldt == null) ? "" : ldt.toLocalTime().format(f);
    }

    private void addHeader(PdfPTable table, Font headerFont, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setGrayFill(0.9f);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setPadding(5f);
            table.addCell(cell);
        }
    }
    private void addCell(PdfPTable table, Font cellFont, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, cellFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setNoWrap(false); // 긴 사유 줄바꿈
        cell.setPadding(4f);
        table.addCell(cell);
    }
}

