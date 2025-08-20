import Calendar from '../components/schedule/Calendar';
import ProgressChart from '../components/project/ProgressChart';
import EmployeeProfile from '../components/profile/EmployeeProfile';
import ScheduleAlert from '../components/alert/ScheduleAlert';
import BoardList from '../components/board/BoardList';
import ApprovalList from '../components/approval/ApprovalList';
import SectionWithMore from '../components/SectionWithMore';
import CalendarIcon from '../components/common/icon/CalendarIcon';
import ProjectIcon from '../components/common/icon/ProjectIcon';
import TableIcon from '../components/common/icon/TableIcon';

const MainPage = () => {
  return (
    <div className="flex flex-col gap-6 p-6 border border-gray-200 rounded-md">
      <div className="flex gap-6">
        <div className="grow p-4 bg-white border border-gray-50 rounded-md shadow-xl">
          <SectionWithMore
            icon={CalendarIcon}
            title="일정관리"
            moreHref="http://localhost:8080/schedule"
          />
          <Calendar />
        </div>
        <div className="w-[240px] flex-none p-4 bg-white border border-gray-50 rounded-md shadow-xl">
          <SectionWithMore
            icon={ProjectIcon}
            title="프로젝트"
            moreHref="http://localhost:8080/pjtMng/getPjtList"
          />
          <div className="mt-0.5">
            <ProgressChart />
          </div>
        </div>
        <div className=" w-[160px] flex-none flex flex-col gap-6">
          <div className="p-4 bg-white border border-gray-50 rounded-md shadow-xl">
            <EmployeeProfile />
          </div>
          <div className="p-4 bg-white border border-gray-50 rounded-md shadow-xl grow">
            <ScheduleAlert />
          </div>
        </div>
      </div>
      <div className="flex gap-6">
        <div className="grow p-4 bg-white border border-gray-50 rounded-md shadow-xl">
          <SectionWithMore
            icon={TableIcon}
            title="공지사항"
            moreHref="http://localhost:8080/board/notice"
          />
          <div className="mt-4">
            <BoardList />
          </div>
        </div>
        <div className="w-[620px] p-4 bg-white border border-gray-50 rounded-md shadow-xl">
          <SectionWithMore
            icon={TableIcon}
            title="전자결제"
            moreHref="http://localhost:8080/approval/main"
          />
          <div className="mt-4">
            <ApprovalList />
          </div>
        </div>
      </div>
    </div>
  );
};

export default MainPage;
