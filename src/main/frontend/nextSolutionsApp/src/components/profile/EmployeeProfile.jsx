import { useState, useEffect } from 'react';
import useFetch from '../../hooks/useFetch';
import AvatarCircle from '../profile/AvatarCircle';
import AvatarPickerModal from '../profile/AvatarPickerModel';

const getAvatarKey = (employeeId) =>
  `profile.avatar.url:${employeeId ?? 'guest'}`;

const EmployeeProfile = () => {
  const { data, loading, error } = useFetch('/employees'); // 필요 시 params 전달

  const [avatarUrl, setAvatarUrl] = useState(null);
  const [pickerOpen, setPickerOpen] = useState(false);

  const user = data;

  useEffect(() => {
    if (!user?.employeeId) return;
    const saved = localStorage.getItem(getAvatarKey(user.employeeId));
    if (saved) setAvatarUrl(saved);
  }, [user?.employeeId]);

  const handleSelectAvatar = (url) => {
    setAvatarUrl(url);
    if (user?.employeeId) {
      localStorage.setItem(getAvatarKey(user.employeeId), url);
    }
    setPickerOpen(false);
  };

  console.log('프로필 :', data);

  if (loading) return <div>불러오는 중...</div>;
  if (error) return <div>오류 발생: {error.message}</div>;

  if (!user) return <div>프로필 정보가 없습니다.</div>;

  return (
    <div className="flex flex-col items-center">
      <div className="mb-2">
        <AvatarCircle
          src={avatarUrl}
          name={user.empNm}
          size={100}
          onClick={() => setPickerOpen(true)}
        />
      </div>
      <div>
        <h3 className="text-center text-base font-medium text-gray-600">
          {user.empNm}
        </h3>
        <p className="text-center text-sm text-gray-600">{user.position}</p>
      </div>
      <AvatarPickerModal
        open={pickerOpen}
        onClose={() => setPickerOpen(false)}
        onSelect={handleSelectAvatar}
      />
    </div>
  );
};

export default EmployeeProfile;
