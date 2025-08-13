import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  flexRender,
} from '@tanstack/react-table';
import { useState, useEffect } from 'react';
import useFetchWithUserId from '../../hooks/useFetchWithUserId'; // 공통 훅 임포트
import dayjs from 'dayjs'; // 날짜 정렬을 위해 dayjs 사용
import clsx from 'clsx';
import style from '../../styles/components/scheduleAlert.module.scss';
import AlertIcon from '../common/icon/AlertIcon';

// 컬럼 정의
const defaultColumns = [
  {
    accessorKey: 'title', // 데이터 키
    header: '알림', // 컬럼 헤더
    cell: (info) => info.getValue(), // 셀 값 렌더링
  },
];

const ScheduleAlert = () => {
  const [data, setData] = useState([]);
  const [columns] = useState(() => [...defaultColumns]);
  const [sorting, setSorting] = useState([]);

  // useFetchWithUserId 훅을 통해 로그인된 사용자의 ID를 기준으로 데이터를 불러옴
  const {
    data: fetchedData,
    loading,
    error,
  } = useFetchWithUserId(
    '/notices' // json-server 엔드포인트
  );

  useEffect(() => {
    if (fetchedData) {
      // 최신순 정렬 (작성일 기준)
      const sortedData = [...fetchedData].sort(
        (a, b) => dayjs(b.date).valueOf() - dayjs(a.date).valueOf()
      );

      // 상위 5개만 추출
      const limitedData = sortedData.slice(0, 5);

      // 게시글 순서 추가
      const dataWithOrder = limitedData.map((item, index) => ({
        ...item,
        order: index + 1, // 순서 추가
      }));

      setData(dataWithOrder); // API에서 가져온 데이터를 테이블 데이터로 설정
    }
  }, [fetchedData]);

  // 테이블 인스턴스 생성
  const table = useReactTable({
    data,
    columns,
    state: {
      sorting, // 정렬 상태
    },
    onSortingChange: setSorting, // 정렬 상태 변경 핸들러
    getCoreRowModel: getCoreRowModel(), // 기본 로우 모델
    getSortedRowModel: getSortedRowModel(), // 정렬 모델
  });

  // 로딩 및 에러 상태 처리
  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>에러 발생: {error}</div>;

  return (
    <div className={clsx(style.scheduleAlert)}>
      <table className="border border-gray-400">
        <thead>
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <th
                  key={header.id}
                  onClick={header.column.getToggleSortingHandler()} // 클릭 시 정렬
                  className="cursor-pointer border px-2 py-1 flex font-medium text-gray-600 t"
                >
                  <AlertIcon size="24px" />
                  {flexRender(
                    header.column.columnDef.header,
                    header.getContext()
                  )}
                  {{
                    asc: ' 🔼',
                    desc: ' 🔽',
                  }[header.column.getIsSorted()] ?? null}
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map((row) => (
            <tr key={row.id}>
              {row.getVisibleCells().map((cell) => (
                <td key={cell.id} className="border px-2 py-1">
                  {flexRender(cell.column.columnDef.cell, cell.getContext())}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ScheduleAlert;
