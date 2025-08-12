import { Outlet } from 'react-router-dom';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchUserAsync } from '../slices/authSlice';
import Header from '../components/Header';
import Footer from '../components/Footer';

const BasicLayout = () => {
  const dispatch = useDispatch();
  const { user, loading, error } = useSelector((state) => state.auth);

  useEffect(() => {
    dispatch(fetchUserAsync());
  }, [dispatch]);

  if (loading) return <div>로그인 확인 중…</div>;
  if (error) return <div>오류: {error}</div>;
  if (!user) return <div>게스트입니다. 로그인 해주세요.</div>;

  console.log(`${user.empNm}, ${user.employeeId}`);

  return (
    <div className="max-w-[1440px] m-auto flex flex-col gap-[20px] px-8 py-4 text-base">
      <Header className="" />
      <main className="grow">
        <Outlet />
      </main>
      <Footer className="" />
    </div>
  );
};

export default BasicLayout;
