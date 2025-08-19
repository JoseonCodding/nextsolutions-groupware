import dayjs from 'dayjs';

const add1Day = (yyyyMMdd) =>
  dayjs(yyyyMMdd, 'YYYY-MM-DD').add(1, 'day').format('YYYY-MM-DD');

const getYmd = (iso) => (iso ? String(iso).slice(0, 10) : null);

// 랜덤 ID 금지. 서버 키 또는 결정적 조합으로 고정 ID 생성
const stableId = (it) => {
  if (it.scheduleId != null) return String(it.scheduleId);
  const s = getYmd(it.startDate || it.startDateStr) || '';
  const e = getYmd(it.endDate || it.endDateStr) || '';
  const t = it.title || '';
  return `${s}_${e}_${t}`; // 최소한 같은 데이터면 항상 같은 ID
};

/**
 * 서버 스케줄 → FullCalendar Event
 * - allDay 규칙: end는 exclusive (다음날 00:00)
 */
export function mapScheduleToEvents(list = []) {
  return list.map((it) => {
    const isAllDay =
      it.cate === '종일' ||
      it.holiday === '휴무일' ||
      !(it.startTimeStr || it.endTimeStr);

    const sDateStr = it.startDateStr || getYmd(it.startDate);
    const eDateStr = it.endDateStr || getYmd(it.endDate) || sDateStr;

    let start, end;
    if (isAllDay) {
      start = sDateStr; // YYYY-MM-DD
      end = add1Day(eDateStr); // exclusive
    } else if (sDateStr) {
      const sTime = it.startTimeStr || '00:00';
      const eTime = it.endTimeStr || sTime;
      start = `${sDateStr}T${sTime}`;
      end = `${eDateStr}T${eTime}`;
    } else {
      start = it.startDate || it.startTime;
      end = it.endDate || it.endTime || start;
    }

    return {
      id: stableId(it), // 안정 ID
      title: it.title || '(제목 없음)',
      start,
      end,
      allDay: Boolean(isAllDay),
      display: 'block',
      extendedProps: {
        holiday: it.holiday,
      },
    };
  });
}

export default mapScheduleToEvents;
