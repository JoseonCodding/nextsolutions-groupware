import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  flexRender,
} from '@tanstack/react-table';
import { useEffect, useMemo, useState } from 'react';
import dayjs from 'dayjs';
import axios from '../../lib/axios';

const StatusBadge = ({ value }) => {
  const map = {
    progress: 'bg-blue-100 text-blue-700',
    pending: 'bg-gray-100 text-gray-700',
    rejected: 'bg-red-100 text-red-700',
    approved: 'bg-green-100 text-green-700',
  };
  return (
    <span
      className={`px-2 py-0.5 rounded text-xs ${
        map[value] || 'bg-gray-100 text-gray-700'
      }`}
    >
      {value}
    </span>
  );
};

const ApprovalList = () => {
  const [rows, setRows] = useState([]);
  const [sorting, setSorting] = useState([{ id: 'createdAt', desc: true }]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    axios
      .get('/mainapproval?limit=10')
      .then((res) => {
        const list = Array.isArray(res.data) ? res.data : [];
        const normalized = list.map((item, idx) => ({
          order: idx + 1,
          formType: item.docType,
          title: item.title,
          createdAt: item.createdAt,
          status: item.status,
          docId: item.docId,
        }));
        normalized.sort(
          (a, b) => dayjs(b.createdAt).valueOf() - dayjs(a.createdAt).valueOf()
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
          const docId = row.original.docId;
          return (
            <a
              href={`/approval/viewer?docId=${encodeURIComponent(
                docId
              )}&page=1&type=&status=`}
              className="underline underline-offset-2 hover:opacity-80"
              title="상세 보기"
            >
              {getValue()}
            </a>
          );
        },
      },
      {
        accessorKey: 'formType',
        header: '결재양식',
        cell: (info) => info.getValue(),
      },
      {
        accessorKey: 'createdAt',
        header: '작성일',
        sortingFn: (a, b, col) =>
          dayjs(a.getValue(col)).valueOf() - dayjs(b.getValue(col)).valueOf(),
        cell: (info) => dayjs(info.getValue()).format('YYYY-MM-DD'),
      },
      {
        accessorKey: 'status',
        header: '결재상태',
        cell: ({ getValue }) => <StatusBadge value={getValue()} />,
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
                내 결재 문서가 없습니다.
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

export default ApprovalList;
