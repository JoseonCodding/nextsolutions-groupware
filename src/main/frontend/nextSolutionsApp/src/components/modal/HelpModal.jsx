import { useEffect, useRef } from 'react';

const HelpModal = ({ open, onClose }) => {
  const dialogRef = useRef(null);
  const closeBtnRef = useRef(null);

  // ESC로 닫기 + 열릴 때 포커스
  useEffect(() => {
    if (!open) return;

    const onKeyDown = (e) => {
      if (e.key === 'Escape') onClose();
    };
    document.addEventListener('keydown', onKeyDown);

    // 첫 포커스(접근성)
    const t = setTimeout(() => {
      closeBtnRef.current?.focus();
    }, 0);

    return () => {
      document.removeEventListener('keydown', onKeyDown);
      clearTimeout(t);
    };
  }, [open, onClose]);

  if (!open) return null;

  // 오버레이 클릭 시 닫기 (콘텐츠 클릭은 무시)
  const handleOverlayClick = (e) => {
    if (e.target === dialogRef.current) onClose();
  };

  return (
    <div
      ref={dialogRef}
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
      onMouseDown={handleOverlayClick}
      role="dialog"
      aria-modal="true"
      aria-labelledby="help-modal-title"
    >
      <div className="w-full max-w-lg rounded-2xl bg-white p-6 shadow-xl">
        <div className="mb-4 flex items-start justify-between gap-4">
          <h2
            id="help-modal-title"
            className="text-xl font-semibold text-gray-900"
          >
            도움말
          </h2>
          <button
            ref={closeBtnRef}
            onClick={onClose}
            className="rounded-md px-3 py-2 text-sm font-medium text-gray-600 border-1 border-gray-300 hover:bg-gray-100 focus:outline-none focus:ring-1 focus:ring-gray-300 cursor-pointer"
            aria-label="도움말 닫기"
          >
            닫기
          </button>
        </div>

        <div className="prose max-w-none text-sm text-gray-700">
          <p className="mb-3">
            아래는 메인 기능 안내입니다. 더 자세한 도움말이 필요하면 관리자에게
            문의하세요.
          </p>

          <ul className="list-disc pl-5 space-y-1">
            <li>
              <b>일정관리</b>: 개인/팀 일정을 확인하고 등록합니다.
            </li>
            <li>
              <b>근태관리</b>: 출퇴근 기록과 연차/휴가 현황을 확인합니다.
            </li>
            <li>
              <b>프로젝트관리</b>: 진행 중인 프로젝트와 진행률을 확인합니다.
            </li>
            <li>
              <b>전자결재</b>: 기안/결재 문서를 작성하고 승인 흐름을 관리합니다.
            </li>
            <li>
              <b>게시판</b>: 공지사항 및 자유게시판을 열람/작성합니다.
            </li>
            <li>
              <b>사원관리</b>: 사원 정보 조회 및 권한 관리를 수행합니다.
            </li>
          </ul>

          <div className="mt-4 rounded-lg bg-gray-50 p-3">
            <p className="mb-1 font-medium text-gray-900">자주 묻는 질문</p>
            <details className="group">
              <summary className="cursor-pointer select-none text-gray-800">
                새로고침하면 로그인 풀려요
              </summary>
              <div className="mt-2 text-gray-600">
                세션 기반 로그인 확인 API(`/api/logInfo`)를 호출해 로그인 상태를
                복원하세요. Redux 상태는 새로고침 시 초기화되므로, 최초 렌더에서
                세션을 재조회하는 로직이 필요합니다.
              </div>
            </details>
            <details className="group mt-2">
              <summary className="cursor-pointer select-none text-gray-800">
                프록시 설정이 필요한가요?
              </summary>
              <div className="mt-2 text-gray-600">
                개발 환경에서는 Vite 프록시(`/api → 8080`)를 쓰고, 배포 시엔
                React 빌드를 Spring 정적 폴더에 넣어 동일 오리진으로
                서비스하세요.
              </div>
            </details>
          </div>
        </div>

        <div className="mt-6 flex justify-end">
          <button
            onClick={onClose}
            className="rounded-xl bg-gray-900 px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-gray-300 cursor-pointer"
          >
            확인
          </button>
        </div>
      </div>
    </div>
  );
};

export default HelpModal;
