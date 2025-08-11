package com.kdt.KDT_PJT.attend.ctl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.attend.model.AttendMapper2;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/attend")
public class AttendController2 {


   @Autowired
    AttendMapper attendMapper;  //소현
   @Autowired
    AttendMapper2 attendMapper2; //동현

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
            inUpdated = attendMapper2.fixInByEmpAndDate(employeeId, workDate, now, title, content);
        }
        if (actions.contains("OUT")) {
            outUpdated = attendMapper2.fixOutByEmpAndDate(employeeId, workDate, now, title, content);
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


    @GetMapping(value="/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void attendListPdf(AttendDTO dto, HttpServletResponse resp) throws IOException {
       
        // 기존 그대로 유지해도 됨 (성능상 권장은 아님)
        List<AttendDTO> list = attendMapper2.searchAttendListHistoryPaged(dto);

        String fileBase = "attendance_changes";
        String encoded = URLEncoder.encode(fileBase, StandardCharsets.UTF_8).replace("+", "%20");
        resp.reset();
        resp.setContentType(MediaType.APPLICATION_PDF_VALUE);
        resp.setHeader("Content-Disposition",
            "attachment; filename=\"" + fileBase + ".pdf\"; filename*=UTF-8''" + encoded + ".pdf");

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
            // ✅ 8개 컬럼으로 맞춤
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{12,12,12,10,10,12,12,20});

            addHeader(table, headerFont, "근무일","사번","이름","출근","퇴근","수정자","수정일","사유");

            // var 대신 명시적 타입 써도 됨 (혹시 sourceCompatibility 이슈 있을 때)
            java.time.format.DateTimeFormatter d  = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            java.time.format.DateTimeFormatter hm = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

            for (AttendDTO a : list) {
                // ✅ null-safe 포맷터 사용 (아래 헬퍼도 함께 추가)
                String workDate = formatLocalDate(extractLdt(a.getCheckInTime()), d);
                String in       = formatLocalTime(extractLdt(a.getCheckInTime()), hm);
                String out      = formatLocalTime(extractLdt(a.getCheckOutTime()), hm);
                String modAt    = formatLocalDate(extractLdt(a.getModifiedAt()), d);

                addCell(table, cellFont, workDate);
                addCell(table, cellFont, ns(a.getEmployeeId()));
                addCell(table, cellFont, ns(a.getEmpNm()));
                addCell(table, cellFont, in);
                addCell(table, cellFont, out);
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
    
    private static String ns(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static java.time.LocalDateTime extractLdt(Object t) {
        if (t == null) return null;
        if (t instanceof java.time.LocalDateTime ldt) return ldt;
        if (t instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        if (t instanceof java.util.Date d)
            return java.time.LocalDateTime.ofInstant(d.toInstant(), java.time.ZoneId.systemDefault());
        return null;
    }

    private static String formatLocalDate(java.time.LocalDateTime ldt, java.time.format.DateTimeFormatter fmt) {
        return ldt == null ? "" : ldt.toLocalDate().format(fmt);
    }

    private static String formatLocalTime(java.time.LocalDateTime ldt, java.time.format.DateTimeFormatter fmt) {
        return ldt == null ? "" : ldt.toLocalTime().format(fmt);
    }

    // (만약 아직 없다면) addHeader/addCell도 같은 위치에 함께 두세요.
    private static void addHeader(com.lowagie.text.pdf.PdfPTable t, com.lowagie.text.Font f, String... cols) {
        // java.awt.Color 사용!
        for (String c : cols) {
            var cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(c, f));
            cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(new java.awt.Color(240, 240, 240));
            t.addCell(cell);
        }
    }

    private static void addCell(com.lowagie.text.pdf.PdfPTable t, com.lowagie.text.Font f, String val) {
        t.addCell(new com.lowagie.text.Phrase(val == null ? "" : val, f));
    }
 


 

    // ===== 유틸 =====
    private static String ns(String s){ return s==null ? "" : s; }

    
   

}

