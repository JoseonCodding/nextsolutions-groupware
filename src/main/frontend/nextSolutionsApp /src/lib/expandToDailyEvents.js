// src/lib/expandToDailyEvents.js
export function expandToDailyEvents(events, start, end) {
  if (!Array.isArray(events) || !start || !end) return [];

  const rangeStart = sod(start);
  const rangeEnd = sod(end); // exclusive
  if (!(rangeStart < rangeEnd)) return [];

  const out = [];

  for (const ev of events) {
    const evStart = toDate(ev?.start);
    const evEnd = ev?.end ? toDate(ev.end) : null;
    if (!evStart) continue;

    const isAllDay = !!ev.allDay;
    const evStartDay = sod(evStart);
    // allDay의 end는 이미 exclusive일 수 있지만, 일 단위 분할을 위해 day 경계만 사용
    const evEndExcl = evEnd
      ? sod(evEnd)
      : addDays(isAllDay ? evStartDay : sod(evStart), 1);

    // 보이는 범위와 교집합
    const segStart = max(rangeStart, evStartDay);
    const segEnd = min(rangeEnd, evEndExcl);
    if (!(segStart < segEnd)) continue;

    // 날짜별로 쪼개기
    for (let day = segStart; day < segEnd; day = addDays(day, 1)) {
      const dayStart = sod(day);
      const dayEnd = addDays(dayStart, 1);

      if (isAllDay) {
        out.push(cloneWith(ev, { start: dayStart, end: dayEnd, allDay: true }));
      } else {
        // 첫날/마지막날의 시간 경계 계산
        const segStartTime =
          dayStart <= evStart && evStart < dayEnd ? evStart : dayStart;
        const segEndTime = evEnd && evEnd <= dayEnd ? evEnd : dayEnd;
        if (segStartTime < segEndTime) {
          out.push(
            cloneWith(ev, {
              start: segStartTime,
              end: segEndTime,
              allDay: false,
            })
          );
        }
      }
    }
  }

  return out;
}

/* ---------- helpers ---------- */
function toDate(d) {
  if (!d) return null;
  return d instanceof Date ? d : new Date(d);
}
function sod(d) {
  // start of day (local)
  const t = toDate(d);
  if (!t) return t;
  const r = new Date(t);
  r.setHours(0, 0, 0, 0);
  return r;
}
function addDays(d, n) {
  const r = new Date(d);
  r.setDate(r.getDate() + n);
  return r;
}
const min = (a, b) => (a < b ? a : b);
const max = (a, b) => (a > b ? a : b);

function cloneWith(ev, patch) {
  return {
    ...ev,
    display: patch.display ?? ev.display ?? 'block',
    ...patch,
    // 확장 시 extendedProps 반드시 보존
    extendedProps: {
      ...(ev.extendedProps || {}),
      ...(patch.extendedProps || {}),
    },
  };
}

export default expandToDailyEvents;
