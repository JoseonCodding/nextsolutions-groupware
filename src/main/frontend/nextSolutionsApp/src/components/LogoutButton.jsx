const LogoutButton = () => {
  const handleLogout = async () => {
    try {
      await fetch('/logout', { method: 'GET', credentials: 'include' });
      window.location.href = '/login';
    } catch (error) {
      console.error('로그아웃 실패', error);
    }
  };

  return (
    <button
      onClick={handleLogout}
      style={{
        background: 'none',
        border: '1px solid #3949ab',
        color: '#3949ab',
        padding: '6px 12px',
        borderRadius: '6px',
        fontSize: '0.9em',
        cursor: 'pointer',
        lineHeight: '1',
      }}
    >
      로그아웃
    </button>
  );
};

export default LogoutButton;
