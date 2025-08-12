import { useSelector } from 'react-redux';
import ProfileImage from '../common/ProfileImage';
import SetIcon from '../common/icon/SetIcon';

const EmployeeProfile = () => {
  const { user } = useSelector((state) => state.auth);

  if (!user) return <div>프로필 정보가 없습니다.</div>;

  return (
    <div className="flex flex-col items-center">
      <ProfileImage
        src={user.photo} // 없으면 ProfileIcon 사용
        alt={user.empNm}
        size={128}
      />
      <h3 className="text-base font-bold">{user.empNm}</h3>
      <p className="flex items-center gap-1 text-sm text-gray-600">
        <span>{user.position}</span>
        <a href="/employee/edit">
          <SetIcon size="20px" />
        </a>
      </p>
    </div>
  );
};

export default EmployeeProfile;
