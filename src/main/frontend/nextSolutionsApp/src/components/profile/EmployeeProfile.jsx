import useFetch from '../../hooks/useFetch';
import Logo from '../common/Logo';
import ProfileImage from '../common/ProfileImage';

const EmployeeProfile = () => {
  const { data, loading, error } = useFetch('/employees'); // 필요 시 params 전달

  const user = data;

  console.log('프로필 :', data);

  if (loading) return <div>불러오는 중...</div>;
  if (error) return <div>오류 발생: {error.message}</div>;

  if (!user) return <div>프로필 정보가 없습니다.</div>;

  return (
    <div className="flex flex-col items-center">
     <ProfileImage src={user.photo} alt={user.empNm} size={128} /> 
      {/*  <div className="flex flex-col items-center gap-0.5 mb-6">
        <div className="w-[60px] h-[60px] p-2 bg-gray-300 rounded-full">
          <Logo fillColor="fill-white" />
        </div>
        <h3 className="text-sm uppercase tracking-tight text-gray-300">
          nextSolutions
        </h3>
      </div>
      */}
      <div>
        <h3 className="text-center text-base font-medium text-gray-600">
          {user.empNm}
        </h3>
        <p className="text-center text-sm text-gray-600">{user.position}</p>
      </div>
    </div>
  );
};

export default EmployeeProfile;
