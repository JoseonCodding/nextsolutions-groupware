// src/main/resources/static/js/project-validation.js

// ===== 정규식 모음 =====
const REG_PROJECT_NAME     = /^[가-힣A-Za-z0-9]{1,20}$/;    // 프로젝트명: 공백/특수문자 불가, 1~20자
const REG_EMPLOYEE_ID      = /^[A-Za-z0-9]{4,50}$/;         // 담당자: 영숫자 4~50
const REG_DATE             = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/; // YYYY-MM-DD
const REG_CONTENT_NONEMPTY = /^(?=.*\S)[\s\S]+$/;           // 내용: 공백만 금지
const REG_APPROVER_ID      = /^\d+$/;                       // 결재자: (예) TB_PJT_APR PK가 숫자일 때
const REG_FILE_EXT         = /\.(pdf|docx?|xlsx?|png|jpe?g)$/i; // 파일 확장자

// ===== 옵션(숫자만 바꾸면 됨) =====
const MAX_FILE_EACH_MB  = 10;   // 파일 1개 최대 10MB
const MAX_FILE_TOTAL_MB = 20;   // 전체 합 최대 20MB
const MB = 1024 * 1024;

// ===== 자동 바인딩: data-validate="project" 폼에 자동 적용 =====
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('form[data-validate="project"]').forEach(form => {
    form.addEventListener('submit', e => {
      if (!validateProjectForm(form)) e.preventDefault();
    });
  });
});

function validateProjectForm(form) {
  const val = name => (form.elements[name]?.value ?? '').trim();

  // 1) 프로젝트명
  const pjtNm = val('pjtNm');
  if (!REG_PROJECT_NAME.test(pjtNm)) {
    return stop('프로젝트명은 공백/특수문자 없이 1~20자(한글/영문/숫자)만 가능합니다.', form.elements['pjtNm']);
  }

  // 2) 담당자 ID (employeeId)
  const employeeId = val('employeeId'); // 이름을 사용할 땐 empNm로 바꿔도 됨
  if (!REG_EMPLOYEE_ID.test(employeeId)) {
    return stop('담당자 ID는 영문/숫자 4~50자로 입력하세요.', form.elements['employeeId']);
  }

  // 3) 상태
  const stts = val('pjtSttsCd');
  if (!['대기','진행중','완료'].includes(stts)) {
    return stop('진행상태를 선택하세요.', form.elements['pjtSttsCd']);
  }

  // 4) 기간(형식 + 논리: 시작 ≤ 종료)
  const start = val('pjtBgngDt');
  const end   = val('pjtEndDt');

  if (!REG_DATE.test(start)) {
    return stop('시작일 형식이 올바르지 않습니다. (예: 2025-08-13)', form.elements['pjtBgngDt']);
  }
  if (end && !REG_DATE.test(end)) {
    return stop('종료일 형식이 올바르지 않습니다. (예: 2025-08-13)', form.elements['pjtEndDt']);
  }
  if (end && toDate(start) > toDate(end)) {
    return stop('종료일은 시작일보다 빠를 수 없습니다.', form.elements['pjtEndDt']);
  }

  // 5) 내용(공백만 금지)
  const content = val('pjtCn'); // 네 폼의 name에 맞춰주세요
  if (!REG_CONTENT_NONEMPTY.test(content)) {
    return stop('내용을 입력하세요. (공백만 입력 불가)', form.elements['pjtCn']);
  }

  // 6) 결재자: TB_PJT_APR 기반 (단일 select 가정)
  const approverId = val('approverId');
  if (!REG_APPROVER_ID.test(approverId)) {
    return stop('결재자를 선택하세요.', form.elements['approverId']);
  }

  // 7) 파일(확장자/용량)
  const filesInput = form.elements['files'];
  if (filesInput && filesInput.files) {
    let total = 0;
    for (const f of filesInput.files) {
      total += f.size;
      if (!REG_FILE_EXT.test(f.name)) {
        return stop('허용되지 않은 파일 형식입니다. (pdf, doc/docx, xls/xlsx, png, jpg/jpeg 허용)', filesInput);
      }
      if (f.size > MAX_FILE_EACH_MB * MB) {
        return stop(`개별 파일은 ${MAX_FILE_EACH_MB}MB를 초과할 수 없습니다.`, filesInput);
      }
    }
    if (total > MAX_FILE_TOTAL_MB * MB) {
      return stop(`첨부파일 총합은 ${MAX_FILE_TOTAL_MB}MB를 초과할 수 없습니다.`, filesInput);
    }
  }

  return true;
}

// ===== 유틸 =====
function stop(msg, el){ alert(msg); el?.focus?.(); return false; }
function toDate(yyyyMMdd){ return new Date(yyyyMMdd + 'T00:00:00'); }
