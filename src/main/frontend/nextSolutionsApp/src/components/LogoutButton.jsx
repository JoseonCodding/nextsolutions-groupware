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
      className="flex justify-center items-center px-3 py-1.5 text-sm text-gray-600 border border-pink-200 bg-pink-50 rounded-lg cursor-pointer"
    >
      로그아웃
    </button>
  );
};

export default LogoutButton;
