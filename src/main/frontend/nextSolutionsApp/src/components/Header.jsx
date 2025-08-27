import { useState } from 'react';
import { Link } from 'react-router-dom';
import LogoText from '../components/common/logo/LogoText';
import LogoutButton from '../components/LogoutButton';
import HelpIcon from '../components/common/icon/HelpIcon';
import HelpModal from '../components/modal/HelpModal';

const Header = () => {
  const [isHelpOpen, setIsHelpOpen] = useState(false);

  return (
    <div className="flex items-center justify-between px-6 border border-gray-200 rounded-md">
      <div>
        <Link to="/rc" className="flex justify-center items-center gap-[3px]">
          <LogoText size="160px" />
          {/* <h1 className="font-medium uppercase text-transparent bg-clip-text bg-gradient-to-r from-blue-500 via-sky-500 to-cyan-500">
            nextSolutions
          </h1> */}
        </Link>
      </div>
      <div className="flex">
        <nav id="navbar">
          <div className="grow">
            <ul className="flex p-4 text-base font-medium text-gray-600">
              <li className="pr-6">
                <a href="http://localhost:8080/schedule">일정관리</a>
              </li>
              <li className="pr-6">
                <a href="http://localhost:8080/attend">근태관리</a>
              </li>
              <li className="pr-6">
                <a href="http://localhost:8080/pjtMng/getPjtList">
                  프로젝트관리
                </a>
              </li>
              <li className="pr-6">
                <a href="http://localhost:8080/approval/main">전자결재</a>
              </li>
              <li className="pr-6">
                <a href="http://localhost:8080/board/notice">게시판</a>
              </li>
              <li className="pr-6">
                <a href="http://localhost:8080/document/main">문서관리</a>
              </li>
              <li className="pr-6">
                <a href="http://localhost:8080/employee/list">사원관리</a>
              </li>
            </ul>
          </div>
        </nav>
      </div>
      <div className="flex gap-[10px]">
        <LogoutButton />
        <button
          className="cursor-pointer rounded-md p-1 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-300"
          onClick={() => setIsHelpOpen(true)}
          aria-label="도움말 열기"
        >
          <HelpIcon size="24px" />
        </button>
      </div>
      <HelpModal open={isHelpOpen} onClose={() => setIsHelpOpen(false)} />
    </div>
  );
};

export default Header;
