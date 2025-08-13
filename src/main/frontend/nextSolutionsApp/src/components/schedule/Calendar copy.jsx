import { useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import useFetchWithUserId from '../../hooks/useFetchWithUserId';
import styles from '../../styles/components/calendar.module.scss';

const Calendar = () => {
  const { data, loading, error } = useFetchWithUserId('/schedules');

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalData, setModalData] = useState({});

  const handleEventClick = (info) => {
    // title/start/end은 기본 필드, description/type은 extendedProps에 있을 수 있음
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

  const closeModal = () => setIsModalOpen(false);

  if (loading) return <p>로딩 중...</p>;
  if (error) return <p>에러: {error.message || '알 수 없는 에러'}</p>;

  // 데이터가 없어도 캘린더가 보이도록 빈 배열로 대체
  const events = Array.isArray(data) ? data : [];

  return (
    <div className={styles.calendarWrapper}>
      <FullCalendar
        plugins={[dayGridPlugin, timeGridPlugin]}
        initialView="dayGridMonth"
        headerToolbar={{
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek,timeGridDay',
        }}
        locale="ko"
        height="600px"
        events={events}
        eventClick={handleEventClick}
        dayCellContent={(arg) => arg.date.getDate()}
      />

      {/* 선택: 일정이 없을 때 안내 */}
      {events.length === 0 && (
        <p className="mt-2 text-sm text-gray-500">등록된 일정이 없습니다.</p>
      )}

      {/* 모달 */}
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>일정 상세</h2>
            <div className="modal-body">
              <p>
                <strong>Title:</strong> {modalData.title}
              </p>
              <p>
                <strong>Start Date:</strong> {modalData.start}
              </p>
              <p>
                <strong>End Date:</strong> {modalData.end}
              </p>
              <p>
                <strong>Type:</strong> {modalData.type}
              </p>
              <p>
                <strong>Description:</strong> {modalData.description}
              </p>
            </div>
            <button onClick={closeModal}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Calendar;
