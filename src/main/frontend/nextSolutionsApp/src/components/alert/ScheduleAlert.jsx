import { useEffect, useState } from 'react';
import useFetch from '../../hooks/useFetch';
import AlertIcon from '../common/icon/AlertIcon';

const ScheduleAlert = () => {
  const { data, loading, error } = useFetch('/schedulealert');
  const [rows, setRows] = useState([]);

  useEffect(() => {
    if (Array.isArray(data)) {
      setRows(
        data.slice(0, 10).map((item) => ({
          title: item.title ?? '(제목 없음)',
          msg: item.msg ?? '',
          id: item.scheduleId ?? null,
        }))
      );
    }
  }, [data]);

  if (loading) return <p className="px-3 py-4 text-gray-500">로딩 중...</p>;
  if (error)
    return <p className="px-3 py-4 text-red-600">에러: {error.message}</p>;
  if (!rows.length)
    return <p className="px-3 py-4 text-gray-500">일정알림이 없습니다.</p>;

  return (
    <div role="region" aria-label="일정 알림">
      <ul className="divide-y divide-gray-200">
        {rows.map((item, idx) => (
          <li key={item.id ?? idx} className="p-1.5 hover:bg-gray-50">
            <div className="flex items-start gap-2">
              <AlertIcon fillColor="fill-orange-600" size="20px" />
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-600 truncate">
                  {item.title}
                </p>
                <p className="mt-0.5 text-xs text-gray-500">{item.msg}</p>
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ScheduleAlert;
