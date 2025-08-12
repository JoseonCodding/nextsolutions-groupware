import { useRef, useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import koLocale from '@fullcalendar/core/locales/ko'; // 한국어 로케일
import useFetchWithUserId from '../../hooks/useFetchWithUserId';
import styles from '../../styles/components/calendar.module.scss';
import ArrowLIcon from '../common/icon/ArrowLIcon';
import ArrowRIcon from '../common/icon/ArrowRIcon';

const Calendar = () => {
  const { data, loading, error } = useFetchWithUserId('/schedules');
  const events = Array.isArray(data) ? data : [];

  const calendarRef = useRef(null);
  const [viewType, setViewType] = useState('dayGridMonth');
  const [title, setTitle] = useState('');

  const api = () => calendarRef.current?.getApi();

  const gotoPrev = () => api()?.prev();
  const gotoNext = () => api()?.next();
  const gotoToday = () => api()?.today();

  const changeView = (type) => {
    api()?.changeView(type);
    setViewType(type);
    // view 바뀌면 title도 datesSet에서 자동 갱신됨
  };

  // 모달 (선택)
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalData, setModalData] = useState({});
  const handleEventClick = (info) => {
    const { title, start, end, extendedProps } = info.event;
    const { description, type } = extendedProps || {};
    setModalData({
      title: title ?? '',
      start: start ? new Date(start).toLocaleString() : '',
      end: end ? new Date(end).toLocaleString() : '',
      description: description ?? '',
      type: type ?? '',
    });
    setIsModalOpen(true);
  };

  if (loading) return <p>로딩 중...</p>;
  if (error) return <p>에러: {error.message || '알 수 없는 에러'}</p>;

  return (
    <div className="flex flex-col gap-2">
      {/* 커스텀 툴바 */}
      <div className="flex items-center justify-between gap-3">
        {/* left */}
        <div className="flex items-center gap-2">
          <button
            onClick={gotoPrev}
            aria-label="이전"
            className="rounded-lg border border-gray-200 bg-white p-1 hover:bg-gray-50 hover:border-gray-300 active:translate-y-px"
          >
            <ArrowLIcon size="24px" />
          </button>
          <button
            onClick={gotoNext}
            aria-label="다음"
            className="rounded-lg border border-gray-200 bg-white p-1 hover:bg-gray-50 hover:border-gray-300 active:translate-y-px"
          >
            <ArrowRIcon size="24px" />
          </button>
          <button
            onClick={gotoToday}
            className="rounded-lg border border-blue-300 bg-blue-50 px-3 py-1.5 text-sm hover:bg-blue-100 active:translate-y-px"
          >
            오늘
          </button>
        </div>

        {/* title */}
        <div className="min-w-40 text-center text-base font-bold">
          <span>{title}</span>
        </div>

        {/* right */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => changeView('dayGridMonth')}
            className={`rounded-lg border px-3 py-1.5 text-sm active:translate-y-px
              ${
                viewType === 'dayGridMonth'
                  ? 'border-green-300 bg-green-50 font-medium'
                  : 'border-gray-200 bg-white hover:bg-gray-50 hover:border-gray-300'
              }`}
          >
            월
          </button>
          <button
            onClick={() => changeView('timeGridWeek')}
            className={`rounded-lg border px-3 py-1.5 text-sm active:translate-y-px
              ${
                viewType === 'timeGridWeek'
                  ? 'border-green-300 bg-green-50 font-medium'
                  : 'border-gray-200 bg-white hover:bg-gray-50 hover:border-gray-300'
              }`}
          >
            주
          </button>
          <button
            onClick={() => changeView('timeGridDay')}
            className={`rounded-lg border px-3 py-1.5 text-sm active:translate-y-px
              ${
                viewType === 'timeGridDay'
                  ? 'border-green-300 bg-green-50 font-medium'
                  : 'border-gray-200 bg-white hover:bg-gray-50 hover:border-gray-300'
              }`}
          >
            일
          </button>
        </div>
      </div>

      {/* FullCalendar */}
      <FullCalendar
        ref={calendarRef}
        plugins={[dayGridPlugin, timeGridPlugin]}
        initialView={viewType}
        headerToolbar={false} // 기본 헤더 숨김
        height="50vh"
        events={events}
        eventClick={handleEventClick}
        locale={koLocale}
        dayCellContent={(arg) => arg.date.getDate()} // 날짜: 숫자만
        datesSet={(arg) => setTitle(arg.view.title)} // "2025년 8월" 식 제목 갱신
      />

      {events.length === 0 && (
        <p className="mt-1 text-xs text-gray-500">등록된 일정이 없습니다.</p>
      )}

      {isModalOpen && (
        <div
          className={styles.modalOverlay}
          onClick={() => setIsModalOpen(false)}
        >
          <div
            className={styles.modalContent}
            onClick={(e) => e.stopPropagation()}
          >
            <h2 className={styles.modalTitle}>일정 상세</h2>
            <div className={styles.modalBody}>
              <p>
                <strong>제목:</strong> {modalData.title}
              </p>
              <p>
                <strong>시작:</strong> {modalData.start}
              </p>
              <p>
                <strong>종료:</strong> {modalData.end}
              </p>
              <p>
                <strong>종류:</strong> {modalData.type}
              </p>
              <p>
                <strong>설명:</strong> {modalData.description}
              </p>
            </div>
            <button
              className={styles.modalClose}
              onClick={() => setIsModalOpen(false)}
            >
              닫기
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Calendar;
