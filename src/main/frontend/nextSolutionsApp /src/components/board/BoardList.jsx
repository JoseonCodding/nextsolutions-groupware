import {
  useReactTable,
  getCoreRowModel,
  flexRender,
} from '@tanstack/react-table';
import { useEffect, useMemo, useState } from 'react';
import dayjs from 'dayjs';
import useFetch from '../../hooks/useFetch';

const BoardList = () => {
  const { data, loading, error } = useFetch('/notices');

  const [rows, setRows] = useState([]);

  console.log('공지사항:', data);

  useEffect(() => {
    if (!data) return;

    const list = Array.isArray(data) ? data : [];
    const normalized = list.map((item, idx) => ({
      order: idx + 1,
      title: item.title ?? '(제목 없음)',
      author: item.empNm ?? '-',
      regDate: item.createdAt ?? null,
      views: item.viewCount ?? 0,
      likes: item.likeCount ?? 0,
      id: item.postId ?? null,
    }));

    setRows(normalized);
  }, [data]);

  const columns = useMemo(
    () => [
      {
        accessorKey: 'order',
        header: 'NO',
        cell: (info) => <div className="text-center">{info.getValue()}</div>,
        enableSorting: false,
        size: 50,
        minSize: 40,
        maxSize: 60,
      },
      {
        accessorKey: 'title',
        header: '제목',
        cell: ({ row, getValue }) => {
          const id = row.original.id;

          if (id == null) {
            console.warn('식별자 없음 행(링크 미생성):', row.original);
            return <span className="text-gray-500">{getValue()}</span>;
          }

          return (
            <a
              href={`/board/notice/detail?postId=${encodeURIComponent(
                String(id)
              )}`}
              className="underline underline-offset-4 decoration-1 decoration-gray-300 hover:opacity-80"
              title="상세 보기"
            >
              {getValue()}
            </a>
          );
        },
        size: 150,
        minSize: 140,
        maxSize: 180,
      },
      {
        accessorKey: 'author',
        header: '작성자',
        cell: (info) => <div className="text-center">{info.getValue()}</div>,
        size: 80,
        minSize: 60,
        maxSize: 100,
      },
      {
        accessorKey: 'regDate',
        header: '작성일',
        enableSorting: false, // 날짜 컬럼 정렬 비활성화
        cell: (info) => (
          <div className="text-center">
            {info.getValue()
              ? dayjs(info.getValue()).format('YYYY-MM-DD')
              : '-'}
          </div>
        ),
        size: 100,
        minSize: 80,
        maxSize: 120,
      },
      {
        accessorKey: 'views',
        header: '조회수',
        cell: (info) => <div className="text-center">{info.getValue()}</div>,
        size: 80,
        minSize: 60,
        maxSize: 100,
      },
      {
        accessorKey: 'likes',
        header: '추천수',
        cell: (info) => <div className="text-center">{info.getValue()}</div>,
        size: 80,
        minSize: 60,
        maxSize: 100,
      },
    ],
    []
  );

  const table = useReactTable({
    data: rows,
    columns,
    getCoreRowModel: getCoreRowModel(), // 정렬 기능 제거
    defaultColumn: { size: 150, minSize: 80, maxSize: 800 },
  });

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>에러 발생: {error?.message || '불러오기 실패'}</div>;

  return (
    <div className="border border-gray-300 rounded-lg overflow-auto">
      <table className="w-full text-sm table-fixed">
        <thead className="bg-gray-50">
          {table.getHeaderGroups().map((hg) => (
            <tr key={hg.id}>
              {hg.headers.map((header) => {
                const w = header.getSize();
                return (
                  <th
                    key={header.id}
                    scope="col"
                    aria-sort="none"
                    className="border-b border-gray-300 px-3 py-2 text-center font-medium text-cyan-800 select-none overflow-hidden whitespace-nowrap text-ellipsis"
                    style={{
                      width: `${w}px`,
                      minWidth: `${w}px`,
                      maxWidth: `${w}px`,
                    }}
                  >
                    {flexRender(
                      header.column.columnDef.header,
                      header.getContext()
                    )}
                  </th>
                );
              })}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.length === 0 ? (
            <tr>
              <td
                colSpan={columns.length}
                className="px-3 py-6 text-center text-gray-500"
              >
                공지사항이 없습니다.
              </td>
            </tr>
          ) : (
            table.getRowModel().rows.map((row) => (
              <tr
                key={row.original.id ?? row.id} // 데이터 식별자 우선
                className="border-b border-gray-300 last:border-b-0 hover:bg-gray-50"
              >
                {row.getVisibleCells().map((cell) => {
                  const w = cell.column.getSize();
                  return (
                    <td
                      key={cell.id}
                      className="px-3 py-2 overflow-hidden whitespace-nowrap text-ellipsis align-middle"
                      style={{
                        width: `${w}px`,
                        minWidth: `${w}px`,
                        maxWidth: `${w}px`,
                      }}
                    >
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </td>
                  );
                })}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default BoardList;
