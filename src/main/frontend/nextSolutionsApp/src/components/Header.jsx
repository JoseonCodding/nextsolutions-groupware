import { useState, useEffect, useRef } from 'react';
import { useSelector } from 'react-redux';

const Header = ({ onMenuClick }) => {
  const [notifOpen,  setNotifOpen]  = useState(false);
  const [notifCount, setNotifCount] = useState(0);
  const [notifList,  setNotifList]  = useState([]);
  const notifRef = useRef(null);
  const user = useSelector((state) => state.auth.user);

  /* 알림 배지 폴링 */
  useEffect(() => {
    const fetch_ = () =>
      fetch('/notification/unread', { credentials: 'include' })
        .then((r) => r.json())
        .then((d) => setNotifCount(d.count || 0))
        .catch(() => {});
    fetch_();
    const t = setInterval(fetch_, 30000);
    return () => clearInterval(t);
  }, []);

  /* 바깥 클릭 닫기 */
  useEffect(() => {
    const h = (e) => { if (notifRef.current && !notifRef.current.contains(e.target)) setNotifOpen(false); };
    document.addEventListener('click', h);
    return () => document.removeEventListener('click', h);
  }, []);

  const loadNotifs = () =>
    fetch('/notification/list', { credentials: 'include' })
      .then((r) => r.json())
      .then(setNotifList)
      .catch(() => {});

  const toggleNotif = (e) => {
    e.stopPropagation();
    const next = !notifOpen;
    setNotifOpen(next);
    if (next) loadNotifs();
  };

  const markAllRead = () =>
    fetch('/notification/readAll', { method: 'POST', credentials: 'include' })
      .then(() => {
        setNotifCount(0);
        setNotifList((p) => p.map((n) => ({ ...n, isRead: 1 })));
      })
      .catch(() => {});

  const handleLogout = async () => {
    try { await fetch('/login/logout', { credentials: 'include' }); } catch {}
    window.location.href = '/login';
  };

  return (
    <header style={{
      height: '56px',
      background: '#fff',
      borderBottom: '1px solid #e2e8f0',
      display: 'flex',
      alignItems: 'center',
      padding: '0 24px',
      gap: '8px',
      position: 'sticky',
      top: 0,
      zIndex: 900,
      flexShrink: 0,
    }}>

      {/* 모바일 햄버거 */}
      <button
        onClick={onMenuClick}
        className="hd-hamburger"
        style={{ display: 'none', background: 'none', border: 'none', cursor: 'pointer',
                 padding: '6px', borderRadius: '6px', color: '#4a5568', fontSize: '1.1em',
                 marginRight: '4px', transition: 'background 0.15s' }}
        onMouseEnter={(e) => (e.currentTarget.style.background = '#f0f2f5')}
        onMouseLeave={(e) => (e.currentTarget.style.background = 'none')}
      >
        <i className="fa fa-bars" />
      </button>

      <div style={{ flex: 1 }} />

      {/* 알림 벨 */}
      <div ref={notifRef} style={{ position: 'relative' }}>
        <button
          onClick={toggleNotif}
          aria-label="알림"
          style={{ background: 'none', border: 'none', cursor: 'pointer',
                   width: '36px', height: '36px', borderRadius: '6px',
                   display: 'flex', alignItems: 'center', justifyContent: 'center',
                   color: '#4a5568', fontSize: '1em', position: 'relative',
                   transition: 'background 0.15s, color 0.15s' }}
          onMouseEnter={(e) => { e.currentTarget.style.background = '#f0f2f5'; e.currentTarget.style.color = '#3949ab'; }}
          onMouseLeave={(e) => { e.currentTarget.style.background = 'none'; e.currentTarget.style.color = '#4a5568'; }}
        >
          <i className="fa fa-bell" />
          {notifCount > 0 && (
            <span style={{
              position: 'absolute', top: '4px', right: '4px',
              background: '#c62828', color: '#fff',
              fontSize: '9px', fontWeight: 700,
              minWidth: '14px', height: '14px', borderRadius: '7px',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              padding: '0 3px', lineHeight: 1,
            }}>
              {notifCount}
            </span>
          )}
        </button>

        {/* 알림 드롭다운 */}
        {notifOpen && (
          <div style={{
            position: 'absolute', top: 'calc(100% + 8px)', right: 0,
            width: '320px', background: '#fff',
            border: '1px solid #e2e8f0', borderRadius: '14px',
            boxShadow: '0 4px 12px rgba(0,0,0,0.07)', zIndex: 2000, overflow: 'hidden',
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                          padding: '13px 16px 11px', borderBottom: '1px solid #f1f5f9' }}>
              <span style={{ fontWeight: 700, fontSize: '0.88em' }}>알림</span>
              <button onClick={markAllRead}
                style={{ fontSize: '12px', background: 'none', border: 'none',
                         color: '#3949ab', cursor: 'pointer', fontWeight: 500 }}>
                모두 읽음
              </button>
            </div>
            <ul style={{ listStyle: 'none', maxHeight: '300px', overflowY: 'auto', margin: 0, padding: 0 }}>
              {notifList.length === 0 ? (
                <li style={{ padding: '16px', color: '#94a3b8', textAlign: 'center', fontSize: '13px' }}>
                  알림이 없습니다.
                </li>
              ) : notifList.map((n) => (
                <li key={n.notifId}
                  onClick={() => n.refUrl && (window.location.href = n.refUrl)}
                  style={{
                    padding: '11px 16px', fontSize: '13px',
                    borderBottom: '1px solid #f1f5f9',
                    cursor: n.refUrl ? 'pointer' : 'default',
                    background: n.isRead === 0 ? '#eef2ff' : '#fff',
                    fontWeight: n.isRead === 0 ? 600 : 400,
                    textAlign: 'left',
                    transition: 'background 0.1s',
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.background = n.isRead === 0 ? '#e8edff' : '#f8fafc')}
                  onMouseLeave={(e) => (e.currentTarget.style.background = n.isRead === 0 ? '#eef2ff' : '#fff')}
                >
                  {n.message}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>

      {/* 마이페이지 */}
      {user && (
        <a href="/mypage" style={{
          display: 'flex', alignItems: 'center', gap: '8px',
          padding: '5px 10px', borderRadius: '6px',
          textDecoration: 'none', color: '#1a202c',
          transition: 'background 0.15s',
        }}
        onMouseEnter={(e) => (e.currentTarget.style.background = '#f0f2f5')}
        onMouseLeave={(e) => (e.currentTarget.style.background = 'transparent')}
        >
          <div style={{
            width: '30px', height: '30px', borderRadius: '50%',
            background: '#3949ab', display: 'flex',
            alignItems: 'center', justifyContent: 'center',
            color: '#fff', fontSize: '12px', fontWeight: 700, flexShrink: 0,
          }}>
            {user.empNm?.[0] || 'U'}
          </div>
          <div>
            <div style={{ fontSize: '0.82em', fontWeight: 600, lineHeight: 1.2 }}>{user.empNm}</div>
            <div style={{ fontSize: '0.72em', color: '#94a3b8', lineHeight: 1.2 }}>{user.position || ''}</div>
          </div>
        </a>
      )}

      {/* 로그아웃 */}
      <button onClick={handleLogout}
        style={{ padding: '6px 14px', background: '#3949ab', color: '#fff',
                 border: 'none', borderRadius: '6px', fontSize: '0.8em',
                 fontWeight: 600, cursor: 'pointer', whiteSpace: 'nowrap',
                 transition: 'filter 0.15s' }}
        onMouseEnter={(e) => (e.currentTarget.style.filter = 'brightness(0.88)')}
        onMouseLeave={(e) => (e.currentTarget.style.filter = 'none')}
      >
        로그아웃
      </button>

      <style>{`
        @media (max-width: 768px) { .hd-hamburger { display: flex !important; } }
      `}</style>
    </header>
  );
};

export default Header;
