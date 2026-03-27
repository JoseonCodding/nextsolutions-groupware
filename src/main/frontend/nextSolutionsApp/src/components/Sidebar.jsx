import { useSelector } from 'react-redux';

const menuItems = [
  { label: '대시보드',     href: '/rc/',              icon: 'fa-house',           match: ['/rc/', '/rc'] },
  { label: '일정관리',     href: '/schedule',         icon: 'fa-calendar-days',   match: '/schedule' },
  { label: '근태관리',     href: '/attend',           icon: 'fa-clock',           match: '/attend' },
  { label: '프로젝트관리', href: '/pjtMng/getPjtList', icon: 'fa-diagram-project', match: '/pjtMng' },
  { label: '전자결재',     href: '/approval/main',    icon: 'fa-file-signature',  match: '/approval' },
  { label: '게시판',       href: '/board/notice',     icon: 'fa-bullhorn',        match: '/board' },
  { label: '문서관리',     href: '/document/main',    icon: 'fa-folder-open',     match: '/document' },
  { label: '사원관리',     href: '/employee/list',    icon: 'fa-users',           match: '/employee' },
];

const isActive = (match) => {
  const path = window.location.pathname;
  if (Array.isArray(match)) return match.includes(path);
  return path.startsWith(match);
};

const Sidebar = ({ open, onClose }) => {
  const user = useSelector((state) => state.auth.user);

  return (
    <>
      <aside style={{
        width: '220px',
        minHeight: '100vh',
        background: '#1a2250',
        position: 'fixed',
        top: 0, left: 0,
        display: 'flex',
        flexDirection: 'column',
        zIndex: 1000,
        transition: 'transform 0.3s ease',
      }}
      className={`app-sidebar${open ? ' sidebar-open' : ''}`}
      >
        {/* 로고 */}
        <div style={{
          padding: '0 20px',
          minHeight: '56px',
          display: 'flex',
          alignItems: 'center',
          borderBottom: '1px solid rgba(255,255,255,0.07)',
          flexShrink: 0,
        }}>
          <a href="/rc/" style={{
            fontWeight: 800,
            fontSize: '0.95em',
            letterSpacing: '0.3px',
            background: 'linear-gradient(135deg, #90caf9, #80deea)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            backgroundClip: 'text',
            textDecoration: 'none',
            whiteSpace: 'nowrap',
          }}>
            NEXTSOLUTIONS
          </a>
        </div>

        {/* 메뉴 */}
        <nav style={{ flex: 1, padding: '10px 0 16px', overflowY: 'auto' }}>
          {menuItems.map((item) => {
            const active = isActive(item.match);
            return (
              <a
                key={item.href}
                href={item.href}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  padding: '9px 16px 9px 20px',
                  color: active ? '#fff' : 'rgba(255,255,255,0.55)',
                  textDecoration: 'none',
                  fontSize: '0.875em',
                  fontWeight: active ? 600 : 500,
                  borderLeft: `3px solid ${active ? '#90caf9' : 'transparent'}`,
                  background: active ? 'rgba(255,255,255,0.12)' : 'transparent',
                  borderRadius: '0 6px 6px 0',
                  margin: '1px 8px 1px 0',
                  transition: 'all 0.15s',
                }}
                onMouseEnter={(e) => {
                  if (!active) {
                    e.currentTarget.style.background = 'rgba(255,255,255,0.07)';
                    e.currentTarget.style.color = 'rgba(255,255,255,0.9)';
                  }
                }}
                onMouseLeave={(e) => {
                  if (!active) {
                    e.currentTarget.style.background = 'transparent';
                    e.currentTarget.style.color = 'rgba(255,255,255,0.55)';
                  }
                }}
              >
                <i className={`fa ${item.icon}`} style={{ width: '16px', textAlign: 'center', fontSize: '0.9em', opacity: active ? 1 : 0.8 }} />
                {item.label}
              </a>
            );
          })}
        </nav>

        {/* 하단 사용자 */}
        {user && (
          <div style={{
            padding: '14px 16px',
            borderTop: '1px solid rgba(255,255,255,0.07)',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
          }}>
            <div style={{
              width: '32px', height: '32px', borderRadius: '50%',
              background: 'rgba(255,255,255,0.15)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: '13px', fontWeight: 700, color: '#fff', flexShrink: 0,
            }}>
              {user.empNm?.[0] || 'U'}
            </div>
            <div style={{ minWidth: 0 }}>
              <div style={{ fontSize: '0.82em', fontWeight: 600, color: 'rgba(255,255,255,0.9)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {user.empNm}
              </div>
              <div style={{ fontSize: '0.72em', color: 'rgba(255,255,255,0.4)' }}>
                {user.position || ''}
              </div>
            </div>
          </div>
        )}
      </aside>

      {/* 모바일 오버레이 */}
      {open && (
        <div
          onClick={onClose}
          style={{
            position: 'fixed', inset: 0,
            background: 'rgba(0,0,0,0.45)',
            zIndex: 999,
          }}
        />
      )}

      <style>{`
        @media (max-width: 768px) {
          .app-sidebar { transform: translateX(-100%); }
          .app-sidebar.sidebar-open { transform: translateX(0); }
        }
      `}</style>
    </>
  );
};

export default Sidebar;
