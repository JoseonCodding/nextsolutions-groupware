import Calendar from '../components/schedule/Calendar';
import ProgressChart from '../components/project/ProgressChart';
import EmployeeProfile from '../components/profile/EmployeeProfile';
import ScheduleAlert from '../components/alert/ScheduleAlert';
import BoardList from '../components/board/BoardList';
import ApprovalList from '../components/approval/ApprovalList';

const Section = ({ emoji, title, moreHref, children }) => (
  <div style={{ background: '#fff', borderRadius: '12px', padding: '20px', boxShadow: '0 1px 4px rgba(0,0,0,0.06)', border: '1px solid #f1f5f9', display: 'flex', flexDirection: 'column', gap: '16px' }}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
        <span style={{ fontSize: '18px' }}>{emoji}</span>
        <span style={{ fontSize: '14px', fontWeight: 700, color: '#1a202c' }}>{title}</span>
      </div>
      <a href={moreHref} style={{ fontSize: '12px', color: '#3949ab', fontWeight: 600, textDecoration: 'none' }}>더보기 →</a>
    </div>
    {children}
  </div>
);

const MainPage = () => (
  <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
    <div style={{ display: 'flex', gap: '20px' }}>
      <div style={{ flex: 1, minWidth: 0 }}>
        <Section emoji="📅" title="일정관리" moreHref="/schedule">
          <Calendar />
        </Section>
      </div>
      <div style={{ width: '240px', flexShrink: 0, display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <Section emoji="📊" title="프로젝트" moreHref="/pjtMng/getPjtList">
          <ProgressChart />
        </Section>
        <Section emoji="👤" title="내 프로필" moreHref="/mypage">
          <EmployeeProfile />
        </Section>
        <Section emoji="🔔" title="일정 알림" moreHref="/schedule">
          <ScheduleAlert />
        </Section>
      </div>
    </div>

    <div style={{ display: 'flex', gap: '20px' }}>
      <div style={{ flex: 1, minWidth: 0 }}>
        <Section emoji="📢" title="공지사항" moreHref="/board/notice">
          <BoardList />
        </Section>
      </div>
      <div style={{ width: '500px', flexShrink: 0 }}>
        <Section emoji="📝" title="전자결재" moreHref="/approval/main">
          <ApprovalList />
        </Section>
      </div>
    </div>

    {/* 푸터 */}
    <footer style={{ borderTop: '1px solid #e2e8f0', padding: '20px 0 8px', textAlign: 'center', fontSize: '12px', color: '#94a3b8', lineHeight: 1.8 }}>
      <p>(주)넥스트솔루션즈 | 대표이사 : ○○○</p>
      <p>주소 : 경남 창원시 성산구 완암로 50, 707호(넥스동, SK테크노파크) (51573)</p>
      <p>사업자등록번호 : 000-00-00000</p>
      <p style={{ marginTop: '4px', fontWeight: 600, color: '#3949ab' }}>Copyright © NextSolutions. All Rights Reserved.</p>
    </footer>
  </div>
);

export default MainPage;
