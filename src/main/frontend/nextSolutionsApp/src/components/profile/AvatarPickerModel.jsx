const avatarModules = import.meta.glob(
  '../../assets/images/**/*.{png,jpg,jpeg,webp,svg}',
  { eager: true }
);
const AVATAR_LIST = Object.values(avatarModules)
  .map((m) => (m && m.default) || m)
  .filter(Boolean);

const AVATAR_STORAGE_KEY = 'profile.avatar.url';

const AvatarPickerModal = ({ open, onClose, onSelect }) => {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/50" onClick={onClose} />
      <div className="relative z-10 w-[680px] max-w-[90vw] bg-white rounded-lg shadow-xl px-10 py-6">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-base font-medium text-gray-800">
            프로필 이미지 선택
          </h3>
          <button
            type="button"
            onClick={onClose}
            className="rounded-md px-3 py-2 text-sm font-medium text-gray-600 border-1 border-gray-300 hover:bg-gray-100 focus:outline-none focus:ring-1 focus:ring-gray-300 cursor-pointer"
          >
            닫기
          </button>
        </div>
        {AVATAR_LIST.length === 0 ? (
          <p className="text-sm text-gray-500">
            이미지를 찾지 못했어요. <code>src/assets/images</code> 경로에
            이미지를 추가해 주세요.
          </p>
        ) : (
          <div className="grid grid-cols-5 gap-3 max-h-[60vh] overflow-auto">
            {AVATAR_LIST.map((url, idx) => (
              <button
                key={`${url}-${idx}`}
                type="button"
                onClick={() => onSelect(url)}
                className="w-full aspect-square rounded-full overflow-hidden border border-gray-200 hover:border-blue-200 focus:outline-none focus:ring-2 focus:ring-blue-200 cursor-pointer"
              >
                <img src={url} className="w-full h-full object-cover" />
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default AvatarPickerModal;
