import { Outlet } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchUserAsync } from '../slices/authSlice';
import Header from '../components/Header';
import Sidebar from '../components/Sidebar';

const BasicLayout = () => {
  const dispatch = useDispatch();
  const { user, loading, error } = useSelector((state) => state.auth);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    dispatch(fetchUserAsync());
  }, [dispatch]);

  if (loading) return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center',
                  minHeight: '100vh', color: '#94a3b8', fontFamily: 'Pretendard, sans-serif',
                  fontSize: '14px' }}>
      로딩 중…
    </div>
  );

  if (error || !user) {
    window.location.href = '/login';
    return null;
  }

  return (
    <div style={{
      display: 'flex',
      minHeight: '100vh',
      background: '#f0f2f5',
      fontFamily: "'Pretendard', -apple-system, BlinkMacSystemFont, 'Apple SD Gothic Neo', sans-serif",
    }}>
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div style={{ marginLeft: '220px', flex: 1, display: 'flex', flexDirection: 'column', minHeight: '100vh' }}
           className="react-main-area">
        <Header onMenuClick={() => setSidebarOpen(true)} />
        <main style={{ flex: 1, padding: '24px 28px', background: '#f0f2f5' }}>
          <Outlet />
        </main>
      </div>

      <style>{`
        @media (max-width: 768px) {
          .react-main-area { margin-left: 0 !important; }
        }
      `}</style>
    </div>
  );
};

export default BasicLayout;
