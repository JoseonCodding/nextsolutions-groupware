import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  flexRender,
} from '@tanstack/react-table';
import { useEffect, useMemo, useState } from 'react';
import dayjs from 'dayjs';
import axios from '../../lib/axios';

const BoardList = () => {
  const [rows, setRows] = useState([]);
  const [sorting, setSorting] = useState([{ id: 'regDate', desc: true }]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    axios
      .get('/mainnotices?limit=10')
      .then((res) => {
        const list = Array.isArray(res.data) ? res.data : [];
        const normalized = list.map((item, idx) => ({
          order: idx + 1,
          title: item.title,
          author: item.employeeId,
          regDate: item.regDate,
          views: item.viewCnt,
          likes: item.likeCnt,
          id: item.id,
        }));
        normalized.sort(
          (a, b) => dayjs(b.regDate).valueOf() - dayjs(a.regDate).valueOf()
        );
        setRows(normalized.slice(0, 5));
      })
      .catch((e) => setError(e?.message || '불러오기 실패'))
      .finally(() => setLoading(false));
  }, []);

  const columns = useMemo(
    () => [
      {
        accessorKey: 'order',
        header: 'NO',
        cell: (info) => info.getValue(),
        enableSorting: false,
        size: 60,
      },
      {
        accessorKey: 'title',
        header: '제목',
        cell: ({ row, getValue }) => {
          const id = row.original.id;
          return (
            <a
              href={`/board/notice/view?id=${encodeURIComponent(id)}&page=1`}
              className="underline underline-offset-2 hover:opacity-80"
              title="상세 보기"
            >
              {getValue()}
            </a>
          );
        },
      },
      {
        accessorKey: 'author',
        header: '작성자',
        cell: (info) => info.getValue(),
      },

      {
        accessorKey: 'regDate',
        header: '작성일',
        sortingFn: (a, b, col) =>
          dayjs(a.getValue(col)).valueOf() - dayjs(b.getValue(col)).valueOf(),
        cell: (info) => dayjs(info.getValue()).format('YYYY-MM-DD'),
      },
      {
        accessorKey: 'views',
        header: '조회수',
        cell: (info) => info.getValue(),
      },
      {
        accessorKey: 'likes',
        header: '추천수',
        cell: (info) => info.getValue(),
      },
    ],
    []
  );

  const table = useReactTable({
    data: rows,
    columns,
    state: { sorting },
    onSortingChange: setSorting,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
  });

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>에러 발생: {error}</div>;

  return (
    <div className="border border-gray-300 rounded-lg overflow-hidden">
      <table className="w-full text-sm">
        <thead className="bg-gray-50">
          {table.getHeaderGroups().map((hg) => (
            <tr key={hg.id}>
              {hg.headers.map((header) => (
                <th
                  key={header.id}
                  onClick={header.column.getToggleSortingHandler()}
                  className="cursor-pointer border-b border-gray-300 px-3 py-2 text-left select-none"
                >
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
                key={row.id}
                className="border-b border-gray-300 last:border-b-0 hover:bg-gray-50"
              >
                {row.getVisibleCells().map((cell) => (
                  <td key={cell.id} className="px-3 py-2">
                    {flexRender(cell.column.columnDef.cell, cell.getContext())}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default BoardList;
