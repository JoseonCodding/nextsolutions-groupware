import { useCallback, useState, useRef, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import axios from '../../lib/axios';
import { mapScheduleToEvents } from '../../lib/mapScheduleToEvents';
import { expandToDailyEvents } from '../../lib/expandToDailyEvents';
import styles from '../../styles/components/calendar.module.scss';

const ymd = (s) =>
  typeof s === 'string'
    ? s.slice(0, 10)
    : new Date(s).toISOString().slice(0, 10);

const Calendar = () => {
  const [title, setTitle] = useState('');
  const calendarRef = useRef(null);

  const fetchEvents = useCallback(async (info, success, failure) => {
    try {
      const params = { start: ymd(info.startStr), end: ymd(info.endStr) };
      const res = await axios.get('/schedules', { params });
      const rows = Array.isArray(res.data) ? res.data : [];
      const base = mapScheduleToEvents(rows);
      const expanded = expandToDailyEvents(base, info.start, info.end);
      success(expanded.length ? expanded : base);
    } catch (err) {
      failure(err);
    }
  }, []);

  useEffect(() => {
    const timer = setInterval(() => {
      console.log('⏱ 캘린더 이벤트 갱신:', new Date().toLocaleString());
      calendarRef.current?.getApi().refetchEvents();
    }, 600000); // 10분
    return () => clearInterval(timer);
  }, []);

  const renderEventContent = ({ event }) => (
    <div className="font-medium text-gray-800">
      {event.extendedProps?.holiday === '휴무일' && (
        <span className="inline-block mb-1 px-1 py-0.5 text-xs text-orange-600 rounded">
          휴무일
        </span>
      )}
      <span className="block px-1 py-0.5 rounded">
        {event.title || '(제목 없음)'}
      </span>
    </div>
  );

  const eventOrder = (a, b) => {
    const ah = a.extendedProps?.holiday === '휴무일' ? 0 : 1;
    const bh = b.extendedProps?.holiday === '휴무일' ? 0 : 1;
    return ah !== bh ? ah - bh : a.title.localeCompare(b.title);
  };

  return (
    <div className={styles.calendarWrap}>
      <div className="mb-4 text-center text-xl font-medium text-gray-600">
        {title}
      </div>
      <FullCalendar
        ref={calendarRef}
        plugins={[dayGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        headerToolbar={false}
        events={fetchEvents}
        displayEventTime={false}
        eventContent={renderEventContent}
        datesSet={(arg) => setTitle(arg.view.title)}
        locale="ko"
        height="auto"
        eventOrder={eventOrder}
        dayCellContent={(arg) => arg.date.getDate()}
        dayMaxEvents={1}
        moreLinkContent={(args) => (
          <span className="px-1 text-xs text-sky-700 border border-sky-300 rounded-sm bg-sky-100">
            {args.num}개 더 보기
          </span>
        )}
        moreLinkClassNames="cursor-pointer"
      />
    </div>
  );
};

export default Calendar;
