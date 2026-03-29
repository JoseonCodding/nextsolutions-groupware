package com.kdt.KDT_PJT.ai;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.KDT_PJT.approval.mapper.ApprovalMapper;
import com.kdt.KDT_PJT.attend.model.AttendDTO;
import com.kdt.KDT_PJT.attend.model.AttendMapper;
import com.kdt.KDT_PJT.cmmn.map.CmmnMap;
import com.kdt.KDT_PJT.cmmn.map.EmployeeDto;
import com.kdt.KDT_PJT.employee.mapper.EmployeeMapper;
import com.kdt.KDT_PJT.notification.NotificationMapper;
import com.kdt.KDT_PJT.pjt_mng.mapper.PjtMngMapper;
import com.kdt.KDT_PJT.schedule.model.ScheduleDTO;
import com.kdt.KDT_PJT.schedule.service.ScheduleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AiController {

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.model}")
    private String model;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Autowired private AiService aiService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private AttendMapper attendMapper;
    @Autowired private ApprovalMapper approvalMapper;
    @Autowired private PjtMngMapper pjtMngMapper;
    @Autowired private MeetingMapper meetingMapper;
    @Autowired private EmployeeMapper employeeMapper;
    @Autowired private NotificationMapper notificationMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;

    public AiController() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(60_000);
        this.restTemplate = new RestTemplate(factory);
    }

    // ── 기존 AI 채팅 ────────────────────────────────────────────

    @GetMapping("/ai")
    public String aiPage(Model model) {
        model.addAttribute("mainUrl", "ai/ai_chat");
        return "navTap";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/ai/chat")
    @ResponseBody
    public Map<String, Object> chat(@RequestBody Map<String, Object> body, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");

        List<Map<String, Object>> messages = (List<Map<String, Object>>) body.get("messages");
        if (messages == null || messages.isEmpty()) return Map.of("error", "메시지가 없습니다.");

        try {
            String text = aiService.call(buildSystemPrompt(me), messages);
            return Map.of("content", text);
        } catch (HttpClientErrorException e) {
            return Map.of("error", "API 오류: " + e.getStatusCode());
        } catch (Exception e) {
            return Map.of("error", "요청 실패: " + e.getMessage());
        }
    }

    // ── 1. 회의록 자동생성 & 액션아이템 추출 ─────────────────────

    @GetMapping("/ai/meeting")
    public String meetingPage(Model model) {
        model.addAttribute("mainUrl", "ai/meeting");
        return "navTap";
    }

    @PostMapping("/ai/meeting")
    @ResponseBody
    public Map<String, Object> generateMeeting(@RequestBody Map<String, String> body, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");

        String date         = body.getOrDefault("date", LocalDate.now().toString());
        String participants = body.getOrDefault("participants", "");
        String agenda       = body.getOrDefault("agenda", "");
        String rawNotes     = body.getOrDefault("notes", "");

        String systemPrompt = """
                당신은 회의록 작성 전문가입니다. 주어진 회의 내용을 아래 JSON 형식으로만 반환하세요.
                다른 설명이나 markdown 코드블록 없이 순수 JSON만 반환하세요.
                {
                  "title": "회의 제목",
                  "summary": "회의 요약 (3-5줄)",
                  "decisions": ["결정사항1", "결정사항2"],
                  "actions": [
                    {"task": "액션아이템 내용", "owner": "담당자", "due": "마감일 또는 미정"}
                  ],
                  "minutes": "정형화된 회의록 전문 (마크다운 형식)"
                }
                """;

        String userMessage = String.format("""
                회의 일시: %s
                참석자: %s
                안건: %s

                회의 내용:
                %s
                """, date, participants, agenda, rawNotes);

        try {
            String json = aiService.call(systemPrompt, userMessage);
            // JSON 파싱 없이 그대로 반환 (프론트에서 JSON.parse)
            return Map.of("result", aiService.stripCodeBlock(json));
        } catch (Exception e) {
            return Map.of("error", "생성 실패: " + e.getMessage());
        }
    }

    // ── 1-b. 회의록 저장 & 액션아이템 등록 ───────────────────────

    @PostMapping("/ai/meeting/save")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Map<String, Object> saveMeeting(@RequestBody Map<String, Object> body, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");

        try {
            // 회의록 저장
            MeetingMinutesDTO dto = new MeetingMinutesDTO();
            dto.setCompanyId(me.getCompanyId());
            dto.setEmployeeId(me.getEmployeeId());
            dto.setMeetDate((String) body.getOrDefault("date", LocalDate.now().toString()));
            dto.setParticipants((String) body.getOrDefault("participants", ""));
            dto.setAgenda((String) body.getOrDefault("agenda", ""));
            dto.setRawNotes((String) body.getOrDefault("notes", ""));
            dto.setAiSummary((String) body.getOrDefault("summary", ""));
            dto.setAiMinutes((String) body.getOrDefault("minutes", ""));

            List<Object> decisions = (List<Object>) body.getOrDefault("decisions", List.of());
            dto.setAiDecisions(objectMapper.writeValueAsString(decisions));

            meetingMapper.insertMinutes(dto);
            int minutesSn = dto.getMinutesSn();

            // 액션아이템 저장 + 담당자 자동 매핑
            List<Map<String, Object>> actions = (List<Map<String, Object>>) body.getOrDefault("actions", List.of());
            List<Map<String, Object>> savedActions = new ArrayList<>();

            for (Map<String, Object> action : actions) {
                MeetingActionDTO actionDto = new MeetingActionDTO();
                actionDto.setMinutesSn(minutesSn);
                actionDto.setTask((String) action.getOrDefault("task", ""));
                actionDto.setDueDate((String) action.getOrDefault("due", "미정"));

                String ownerName = (String) action.getOrDefault("owner", "");
                actionDto.setOwnerName(ownerName);

                // 3단계: 담당자 이름 → 직원 매핑
                String matchedId = null;
                if (ownerName != null && !ownerName.isBlank()) {
                    EmployeeDto matched = employeeMapper.findByEmpNm(ownerName, me.getCompanyId());
                    if (matched != null) {
                        matchedId = matched.getEmployeeId();
                        actionDto.setOwnerEmployeeId(matchedId);
                        // 알림 발송
                        notificationMapper.insert(
                            me.getCompanyId(), matchedId, me.getEmpNm(),
                            "MEETING",
                            "[회의록] " + ownerName + "님 담당 액션아이템: " + actionDto.getTask(),
                            "/ai/meeting/list"
                        );
                    }
                }
                meetingMapper.insertAction(actionDto);

                Map<String, Object> saved = new HashMap<>(action);
                saved.put("matched", matchedId != null);
                saved.put("matchedEmployeeId", matchedId);
                savedActions.add(saved);
            }

            return Map.of("ok", true, "minutesSn", minutesSn, "actions", savedActions);
        } catch (Exception e) {
            return Map.of("error", "저장 실패: " + e.getMessage());
        }
    }

    // ── 1-c. 회의록 목록 ─────────────────────────────────────────

    @GetMapping("/ai/meeting/list")
    public String meetingListPage(Model model, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me != null) {
            try {
                model.addAttribute("minutesList", meetingMapper.selectRecentMinutes(me.getCompanyId()));
            } catch (Exception ignored) {}
        }
        model.addAttribute("mainUrl", "ai/meeting_list");
        return "navTap";
    }

    @GetMapping("/ai/meeting/detail/{minutesSn}")
    @ResponseBody
    public Map<String, Object> meetingDetail(@PathVariable int minutesSn, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");
        try {
            CmmnMap minutes = meetingMapper.selectMinutesDetail(minutesSn);
            List<CmmnMap> actions = meetingMapper.selectActionsByMinutesSn(minutesSn);
            return Map.of("minutes", minutes, "actions", actions);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    // ── 2단계. STT (Whisper) ──────────────────────────────────────

    @PostMapping("/ai/meeting/stt")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Map<String, Object> speechToText(@RequestParam("audio") MultipartFile file, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");
        if (openaiApiKey == null || openaiApiKey.isBlank()) {
            return Map.of("error", "OpenAI API 키가 설정되지 않았습니다. application.yml의 openai.api.key를 확인하세요.");
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + openaiApiKey);

            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("model", "whisper-1");
            formData.add("language", "ko");
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio.webm";
            final byte[] bytes = file.getBytes();
            formData.add("file", new ByteArrayResource(bytes) {
                @Override public String getFilename() { return filename; }
            });

            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/audio/transcriptions",
                new HttpEntity<>(formData, headers),
                Map.class);

            String text = (String) response.getBody().get("text");
            return Map.of("text", text != null ? text : "");
        } catch (Exception e) {
            return Map.of("error", "STT 실패: " + e.getMessage());
        }
    }

    // ── 2. 문서 자동 초안 ────────────────────────────────────────

    @GetMapping("/ai/draft")
    public String draftPage(Model model) {
        model.addAttribute("mainUrl", "ai/draft");
        return "navTap";
    }

    @PostMapping("/ai/draft")
    @ResponseBody
    public Map<String, Object> generateDraft(@RequestBody Map<String, String> body, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");

        String docType   = body.getOrDefault("docType", "업무보고서");
        String title     = body.getOrDefault("title", "");
        String keyPoints = body.getOrDefault("keyPoints", "");
        String tone      = body.getOrDefault("tone", "공식적");

        String systemPrompt = String.format("""
                당신은 비즈니스 문서 작성 전문가입니다.
                작성자: %s (%s, %s)
                문서 유형: %s
                문체: %s

                주어진 핵심 내용을 바탕으로 완성도 높은 문서 초안을 작성하세요.
                마크다운 형식으로 작성하되, 제목/소제목/본문 구조를 갖추세요.
                """,
                me.getEmpNm(),
                me.getDeptName() != null ? me.getDeptName() : "미지정",
                me.getPosition() != null ? me.getPosition() : "미지정",
                docType, tone);

        String userMessage = String.format("문서 제목: %s\n\n핵심 내용:\n%s", title, keyPoints);

        try {
            String draft = aiService.call(systemPrompt, userMessage);
            return Map.of("draft", draft);
        } catch (Exception e) {
            return Map.of("error", "생성 실패: " + e.getMessage());
        }
    }

    // ── 3. 업무 부하 감지 ────────────────────────────────────────

    @GetMapping("/ai/workload")
    public String workloadPage(Model model, HttpSession session) {
        model.addAttribute("mainUrl", "ai/workload");
        return "navTap";
    }

    @PostMapping("/ai/workload")
    @ResponseBody
    public Map<String, Object> analyzeWorkload(HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");

        StringBuilder data = new StringBuilder();

        // 내 프로젝트
        try {
            List<CmmnMap> projects = pjtMngMapper.selectMyProjectList(me.getEmployeeId(), me.getCompanyId());
            int inProgress = (int) projects.stream()
                    .filter(p -> "진행중".equals(p.get("PJT_STTS_CD"))).count();
            data.append("진행중인 프로젝트: ").append(inProgress).append("개\n");
            for (CmmnMap p : projects) {
                data.append("  - ").append(p.get("PJT_NM"))
                    .append(" [").append(p.get("PJT_STTS_CD")).append("]")
                    .append(" 종료: ").append(p.get("PJT_END_DT")).append("\n");
            }
        } catch (Exception ignored) {}

        // 미결재 문서
        try {
            List<Map<String, Object>> pending = approvalMapper.selectPendingListForApprover(
                    me.getEmployeeId(), me.getCompanyId());
            data.append("미결재 문서: ").append(pending.size()).append("건\n");
        } catch (Exception ignored) {}

        // 이번달 일정 수
        try {
            ScheduleDTO schDto = new ScheduleDTO();
            schDto.setEmployeeId(me.getEmployeeId());
            schDto.setCompanyId(me.getCompanyId());
            List<ScheduleDTO> schedules = scheduleService.getScheduleList(schDto);
            LocalDate nowDate = LocalDate.now();
            long upcoming = schedules.stream()
                    .filter(s -> {
                        if (s.getStartDate() == null) return false;
                        LocalDate sd = s.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return !sd.isBefore(nowDate);
                    })
                    .count();
            data.append("이번달 예정 일정: ").append(upcoming).append("건\n");
        } catch (Exception ignored) {}

        // 오늘 출근 여부
        try {
            AttendDTO attend = attendMapper.findTodayAttendance(me.getEmployeeId());
            data.append("오늘 출근: ").append(attend != null ? "O" : "X").append("\n");
        } catch (Exception ignored) {}

        String systemPrompt = """
                당신은 업무 부하 분석 전문가입니다.
                직원의 업무 현황 데이터를 분석하여 아래 JSON 형식으로만 반환하세요.
                다른 설명 없이 순수 JSON만 반환하세요.
                {
                  "score": 7,
                  "level": "높음",
                  "summary": "현재 업무 부하 요약 (2-3줄)",
                  "risks": ["위험 요소1", "위험 요소2"],
                  "recommendations": ["권고사항1", "권고사항2", "권고사항3"]
                }
                score는 1~10 (1=매우 낮음, 10=극도로 높음), level은 낮음/보통/높음/위험 중 하나.
                """;

        String userMessage = String.format("""
                직원: %s (%s / %s)
                오늘 날짜: %s

                업무 현황:
                %s
                """,
                me.getEmpNm(),
                me.getDeptName() != null ? me.getDeptName() : "미지정",
                me.getPosition() != null ? me.getPosition() : "미지정",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                data.toString());

        try {
            String json = aiService.call(systemPrompt, userMessage);
            return Map.of("result", aiService.stripCodeBlock(json));
        } catch (Exception e) {
            return Map.of("error", "분석 실패: " + e.getMessage());
        }
    }

    // ── 4. 스마트 일정 조율 ──────────────────────────────────────

    @GetMapping("/ai/schedule-suggest")
    public String scheduleSuggestPage(Model model, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me != null) {
            try {
                model.addAttribute("employeeList",
                        pjtMngMapper.getEmployeeList(me.getCompanyId()));
            } catch (Exception ignored) {}
        }
        model.addAttribute("mainUrl", "ai/schedule_suggest");
        return "navTap";
    }

    @PostMapping("/ai/schedule-suggest")
    @ResponseBody
    public Map<String, Object> suggestSchedule(@RequestBody Map<String, Object> body, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");

        String topic      = (String) body.getOrDefault("topic", "회의");
        int    duration   = Integer.parseInt(body.getOrDefault("duration", "60").toString());
        @SuppressWarnings("unchecked")
        List<String> participantIds = (List<String>) body.get("participants");

        // 향후 2주 일정 수집
        StringBuilder scheduleData = new StringBuilder();
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksLater = today.plusWeeks(2);

        try {
            ScheduleDTO schDto = new ScheduleDTO();
            schDto.setCompanyId(me.getCompanyId());
            List<ScheduleDTO> allSchedules = scheduleService.getScheduleList(schDto);

            for (ScheduleDTO s : allSchedules) {
                if (s.getStartDate() == null) continue;
                LocalDate sd = s.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (sd.isBefore(today) || sd.isAfter(twoWeeksLater)) continue;
                if (participantIds != null && !participantIds.contains(s.getEmployeeId())) continue;
                scheduleData.append("  - [").append(s.getEmployeeId()).append("] ")
                        .append(s.getStartDate()).append(" ")
                        .append(s.getCate() != null ? s.getCate() : "").append(" ")
                        .append(s.getTitle()).append("\n");
            }
        } catch (Exception ignored) {}

        String systemPrompt = """
                당신은 일정 조율 전문가입니다.
                참여자들의 기존 일정을 고려하여 최적의 회의 시간 3개를 제안하세요.
                아래 JSON 형식으로만 반환하세요. 다른 설명 없이 순수 JSON만 반환하세요.
                {
                  "suggestions": [
                    {
                      "date": "2026-04-01",
                      "time": "14:00",
                      "endTime": "15:00",
                      "reason": "이 시간을 추천하는 이유"
                    }
                  ]
                }
                """;

        String userMessage = String.format("""
                회의 주제: %s
                소요 시간: %d분
                참여자 ID: %s
                오늘 날짜: %s

                향후 2주간 기존 일정:
                %s

                주말을 제외하고, 기존 일정과 겹치지 않는 업무 시간(09:00~18:00) 내 최적 시간 3개를 추천하세요.
                """,
                topic, duration,
                participantIds != null ? String.join(", ", participantIds) : "미지정",
                today.toString(),
                scheduleData.length() > 0 ? scheduleData.toString() : "기존 일정 없음");

        try {
            String json = aiService.call(systemPrompt, userMessage);
            return Map.of("result", aiService.stripCodeBlock(json));
        } catch (Exception e) {
            return Map.of("error", "추천 실패: " + e.getMessage());
        }
    }

    // ── 5. 전자결재 AI 결재선 추천 ───────────────────────────────

    @PostMapping("/ai/approval/recommend")
    @ResponseBody
    public Map<String, Object> recommendApprover(@RequestBody Map<String, Object> body,
                                                  HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");

        String title   = (String) body.getOrDefault("title", "");
        String content = (String) body.getOrDefault("content", "");

        if (title.isBlank() && content.isBlank())
            return Map.of("error", "제목 또는 내용을 먼저 입력하세요.");

        // 결재 가능 직원 목록 조회
        List<CmmnMap> employees;
        try {
            employees = pjtMngMapper.getEmployeeList(me.getCompanyId());
        } catch (Exception e) {
            return Map.of("error", "직원 목록 조회 실패: " + e.getMessage());
        }
        if (employees == null || employees.isEmpty())
            return Map.of("error", "결재 가능한 직원이 없습니다.");

        // 직원 목록 텍스트화
        StringBuilder empList = new StringBuilder();
        for (CmmnMap emp : employees) {
            String id  = String.valueOf(emp.get("employeeId"));
            String nm  = String.valueOf(emp.getOrDefault("emp_nm", emp.getOrDefault("EMP_NM", "")));
            String pos = String.valueOf(emp.getOrDefault("position", ""));
            String dept = String.valueOf(emp.getOrDefault("deptName", ""));
            empList.append("- employeeId: ").append(id)
                   .append(", 이름: ").append(nm)
                   .append(", 직위: ").append(pos)
                   .append(", 부서: ").append(dept).append("\n");
        }

        String systemPrompt = """
                당신은 기업 전자결재 시스템의 결재선 추천 전문가입니다.
                문서의 제목과 내용을 분석하여 가장 적합한 결재자를 추천하세요.
                아래 JSON 형식으로만 반환하세요. 다른 설명 없이 순수 JSON만 반환하세요.
                {
                  "employeeId": "결재자 사번",
                  "name": "결재자 이름",
                  "reason": "이 결재자를 추천하는 이유 (1-2문장)"
                }
                직원 목록에 있는 사람 중에서만 추천하세요.
                """;

        String userMessage = String.format("""
                결재 문서 제목: %s
                결재 문서 내용: %s
                신청자: %s (%s / %s)

                결재 가능한 직원 목록:
                %s

                위 문서에 가장 적합한 결재자 1명을 추천해주세요.
                """,
                title, content,
                me.getEmpNm(),
                me.getDeptName() != null ? me.getDeptName() : "미지정",
                me.getPosition() != null ? me.getPosition() : "미지정",
                empList.toString());

        try {
            String json = aiService.call(systemPrompt, userMessage);
            return Map.of("result", aiService.stripCodeBlock(json));
        } catch (Exception e) {
            return Map.of("error", "추천 실패: " + e.getMessage());
        }
    }

    // ── 6. 맥락 기반 알림 필터링 ─────────────────────────────────

    @PostMapping("/ai/notifications/smart")
    @ResponseBody
    public Map<String, Object> smartNotifications(@RequestBody Map<String, Object> body, HttpSession session) {
        EmployeeDto me = (EmployeeDto) session.getAttribute("loginUser");
        if (me == null) return Map.of("error", "로그인이 필요합니다.");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> notifications = (List<Map<String, Object>>) body.get("notifications");
        if (notifications == null || notifications.isEmpty()) return Map.of("filtered", List.of());

        StringBuilder notifText = new StringBuilder();
        for (int i = 0; i < notifications.size(); i++) {
            Map<String, Object> n = notifications.get(i);
            notifText.append(i).append(". [").append(n.get("type")).append("] ")
                     .append(n.get("message")).append("\n");
        }

        String systemPrompt = """
                당신은 알림 우선순위 분류 전문가입니다.
                직원의 현재 업무 맥락을 고려하여 알림 목록에서 중요한 것을 필터링하세요.
                아래 JSON 형식으로만 반환하세요. 다른 설명 없이 순수 JSON만 반환하세요.
                {
                  "filtered": [
                    {"index": 0, "priority": "high", "reason": "중요한 이유"}
                  ]
                }
                priority는 high/medium/low 중 하나. high인 것만 포함하세요 (최대 5개).
                """;

        String userMessage = String.format("""
                직원: %s (%s / %s)
                오늘 날짜: %s

                알림 목록:
                %s

                업무상 즉시 확인이 필요한 중요 알림만 high로 분류하세요.
                """,
                me.getEmpNm(),
                me.getDeptName() != null ? me.getDeptName() : "미지정",
                me.getPosition() != null ? me.getPosition() : "미지정",
                LocalDate.now().toString(),
                notifText.toString());

        try {
            String json = aiService.call(systemPrompt, userMessage);
            return Map.of("result", aiService.stripCodeBlock(json));
        } catch (Exception e) {
            return Map.of("error", "필터링 실패: " + e.getMessage());
        }
    }

    // ── 공통 시스템 프롬프트 ─────────────────────────────────────

    private String buildSystemPrompt(EmployeeDto me) {
        StringBuilder sb = new StringBuilder();
        sb.append("당신은 NextSolutions 그룹웨어의 AI 어시스턴트입니다. ");
        sb.append("직원들의 업무 관련 질문에 친절하고 간결하게 한국어로 답변해주세요.\n\n");

        sb.append("=== 현재 로그인 사용자 ===\n");
        sb.append("이름: ").append(me.getEmpNm()).append("\n");
        sb.append("부서: ").append(me.getDeptName() != null ? me.getDeptName() : "미지정").append("\n");
        sb.append("직위: ").append(me.getPosition() != null ? me.getPosition() : "미지정").append("\n");
        sb.append("오늘: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))).append("\n\n");

        try {
            AttendDTO attend = attendMapper.findTodayAttendance(me.getEmployeeId());
            sb.append("=== 오늘 출근 현황 ===\n");
            if (attend != null) {
                sb.append("출근: ").append(attend.getCheckInHourMinute()).append("\n");
                sb.append("퇴근: ").append(attend.getCheckOutTime() != null ? attend.getCheckOutHourMinute() : "재직중").append("\n\n");
            } else {
                sb.append("출근 기록 없음\n\n");
            }
        } catch (Exception ignored) {}

        try {
            ScheduleDTO schDto = new ScheduleDTO();
            schDto.setEmployeeId(me.getEmployeeId());
            schDto.setCompanyId(me.getCompanyId());
            List<ScheduleDTO> schedules = scheduleService.getScheduleList(schDto);
            sb.append("=== 이번달 일정 ===\n");
            if (schedules != null && !schedules.isEmpty()) {
                schedules.forEach(s -> sb.append("- ").append(s.getTitle())
                        .append(" (").append(s.getStartDateStr()).append(")\n"));
            } else {
                sb.append("일정 없음\n");
            }
            sb.append("\n");
        } catch (Exception ignored) {}

        try {
            List<Map<String, Object>> pendingList = approvalMapper.selectPendingListForApprover(
                    me.getEmployeeId(), me.getCompanyId());
            sb.append("=== 미결재 문서 ===\n");
            if (pendingList != null && !pendingList.isEmpty()) {
                pendingList.forEach(doc -> sb.append("- ").append(doc.get("title"))
                        .append(" (기안자: ").append(doc.get("writer_name")).append(")\n"));
            } else {
                sb.append("미결재 없음\n");
            }
            sb.append("\n");
        } catch (Exception ignored) {}

        try {
            List<CmmnMap> projects = pjtMngMapper.selectMyProjectList(me.getEmployeeId(), me.getCompanyId());
            sb.append("=== 내 프로젝트 ===\n");
            if (projects != null && !projects.isEmpty()) {
                projects.forEach(p -> sb.append("- ").append(p.get("PJT_NM"))
                        .append(" [").append(p.get("PJT_STTS_CD")).append("]")
                        .append(" (").append(p.get("PJT_BGNG_DT")).append(" ~ ").append(p.get("PJT_END_DT")).append(")\n"));
            } else {
                sb.append("참여 프로젝트 없음\n");
            }
        } catch (Exception ignored) {}

        sb.append("\n위 데이터를 참고하여 답변해주세요.");
        return sb.toString();
    }
}
