const AvatarCircle = ({ src, name = '사용자', size = 60, onClick }) => {
  const px = `${size}px`;
  return (
    <button
      type="button"
      onClick={onClick}
      className="rounded-full overflow-hidden border border-gray-300 bg-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-100 cursor-pointer"
      style={{ width: px, height: px }}
      aria-label="프로필 이미지 변경"
      title="프로필 이미지 변경"
    >
      {src ? (
        <img
          src={src}
          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
        />
      ) : (
        <span className="w-full h-full flex items-center justify-center text-gray-600 text-xl font-semibold">
          {name?.[0] || 'N'}
        </span>
      )}
    </button>
  );
};

export default AvatarCircle;
