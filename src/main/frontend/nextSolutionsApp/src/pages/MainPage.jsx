import Calendar from '../components/schedule/Calendar';
import ProgressChart from '../components/project/ProgressChart';
import ProgressChartDummy from '../components/project/ProgressChartDummy';
import EmployeeProfile from '../components/profile/EmployeeProfile';
import ScheduleAlert from '../components/alert/ScheduleAlert';
import BoardList from '../components/board/BoardList';
import ApprovalList from '../components/approval/ApprovalList';
import MoreButton from '../components/common/button/moreButton';
import CalendarIcon from '../components/common/icon/CalendarIcon';
import ProjectIcon from '../components/common/icon/ProjectIcon';
import TableIcon from '../components/common/icon/TableIcon';

const MainPage = () => {
  return (
    <div className="flex flex-col gap-[20px] p-8 border border-gray-200 rounded-md">
      <div className="flex gap-[20px]">
        <div className="grow p-4 bg-white border border-gray-50 rounded-md shadow-xl">
          <div className="flex justify-between mb-4">
            <div className="flex">
              <CalendarIcon size="24px" />
              <h3 className="text-gray-500">일정관리</h3>
            </div>
            <MoreButton href="/schedule" />
          </div>
          <Calendar />
        </div>
        <div className="w-[300px] p-4 bg-white border border-gray-50 rounded-md shadow-xl">
          <div className="flex justify-between mb-4">
            <div className="flex">
              <ProjectIcon size="24px" />
              <h3 className="text-gray-500">프로젝트</h3>
            </div>
            <MoreButton href="/pjtMng/getPjtList" />
          </div>
          {/* <ProgressChart /> */}
          <ProgressChartDummy />
        </div>
        <div className="flex flex-col gap-[20px] w-[300px] ">
          <div className="p-4 bg-white border border-gray-50 rounded-md shadow-xl">
            <EmployeeProfile />
          </div>
          <div className="p-4 bg-white border border-gray-50 rounded-md shadow-xl">
            <ScheduleAlert />
          </div>
        </div>
      </div>
      <div className="flex gap-[20px]">
        <div className="grow p-4 bg-white border border-gray-50 rounded-md shadow-xl">
          <div className="flex justify-between mb-4">
            <div className="flex">
              <TableIcon size="24px" />
              <h3 className="text-gray-500">공지사항</h3>
            </div>
            <MoreButton href="/board/notice" />
          </div>
          <BoardList />
        </div>
        <div className="w-[620px] p-4 bg-white border border-gray-50 rounded-md shadow-xl">
          <div className="flex justify-between mb-4">
            <div className="flex">
              <TableIcon size="24px" />
              <h3 className="text-gray-500">전자결제</h3>
            </div>
            <MoreButton href="/approval/main" />
          </div>

          <ApprovalList />
        </div>
      </div>
    </div>
  );
};

export default MainPage;
