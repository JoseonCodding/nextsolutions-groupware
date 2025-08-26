import ProfileIcon from './icon/ProfileIcon';

const ProfileImage = ({ src, alt, size = 128 }) => {
  if (!src) {
    // 이미지 없으면 SVG 아이콘 출력
    return (
      <div
        className="rounded-full overflow-hidden flex items-center justify-center bg-gray-200"
        style={{ width: size, height: size }}
      >
        <ProfileIcon />
      </div>
    );
  }

  return (
    <img
      src={src}
      alt={alt || '프로필 이미지'}
      className="rounded-full object-cover"
      style={{ width: size, height: size }}
    />
  );
};

export default ProfileImage;
