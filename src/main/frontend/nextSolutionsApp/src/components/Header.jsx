import Logo from '../components/common/Logo';
import LogoutButton from '../components/LogoutButton';
import HelpIcon from '../components/common/icon/HelpIcon';

const Header = () => {
  return (
    <div className="flex items-center justify-between px-4 border border-gray-200 rounded-md">
      <div>
        <span className="flex justify-center items-center gap-[3px]">
          <Logo size="28px" />
          <h1 className="font-medium uppercase text-gray-500">nextSolutions</h1>
        </span>
      </div>
      <div className="flex">
        <nav id="navbar">
          <div className="grow">
            <ul className="flex p-4 text-lg font-medium text-gray-600">
              <li className="pr-6">
                <a href="/schedule">일정관리</a>
              </li>
              <li className="pr-6">
                <a href="/attend">근태관리</a>
              </li>
              <li className="pr-6">
                <a href="/pjtMng/getPjtList">프로젝트관리</a>
              </li>
              <li className="pr-6">
                <a href="/approval/main">전자결재</a>
              </li>
              <li className="pr-6">
                <a href="/board/notice">게시판</a>
              </li>
              <li className="pr-6">
                <a href="">문서관리</a>
              </li>
              <li className="pr-6">
                <a href="/employee/list">사원관리</a>
              </li>
            </ul>
          </div>
        </nav>
      </div>
      <div className="flex gap-[10px]">
        <LogoutButton />
        <button className="cursor-pointer">
          <HelpIcon size="24px" />
        </button>
      </div>
    </div>
  );
};

export default Header;
