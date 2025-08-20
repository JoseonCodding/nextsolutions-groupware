import {
  useReactTable,
  getCoreRowModel,
  flexRender,
} from '@tanstack/react-table';
import { useEffect, useMemo, useState } from 'react';
import dayjs from 'dayjs';
import useFetch from '../../hooks/useFetch';

const ApprovalListt = () => {
  const { data, loading, error } = useFetch('/approval');

  const [rows, setRows] = useState([]);

  useEffect(() => {
    if (!data) return;

    const list = Array.isArray(data) ? data : [];
    const normalized = list.map((item, idx) => ({
      order: idx + 1,
      regDate: item.createdAt ?? null,
      docType: item.docType ?? null,
      title: item.title ?? '(제목 없음)',
      author: item.writer ?? '-',
      deptName: item.deptName ?? null,
      status: item.status ?? null,
      id: item.docId ?? null,
    }));

    setRows(normalized);
  }, [data]);

  const columns = useMemo(
    () => [
      {
        accessorKey: 'order',
        header: 'NO',
        cell: (info) => info.getValue(),
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
          if (id == null)
            return <span className="text-gray-500">{getValue()}</span>;
          return (
            <a
              href={`/approval/viewer?docId=${encodeURIComponent(String(id))}`}
              className="underline underline-offset-2 hover:opacity-80"
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
        cell: (info) => info.getValue(),
        size: 80,
        minSize: 60,
        maxSize: 100,
      },
      // {
      //   accessorKey: 'deptName',
      //   header: '부서',
      //   cell: (info) => info.getValue(),
      //   size: 100,
      //   minSize: 80,
      //   maxSize: 120,
      // },
      {
        accessorKey: 'regDate',
        header: '작성일',
        enableSorting: false,
        cell: (info) =>
          info.getValue() ? dayjs(info.getValue()).format('YYYY-MM-DD') : '-',
        size: 100,
        minSize: 80,
        maxSize: 120,
      },
      {
        accessorKey: 'docType',
        header: '결재종류',
        cell: (info) => info.getValue(),
        size: 80,
        minSize: 60,
        maxSize: 100,
      },
      {
        accessorKey: 'status',
        header: '결재상태',
        cell: (info) => info.getValue(),
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
    defaultColumn: { size: 150, minSize: 80, maxSize: 800 }, // 기본 컬럼 한계치 (명시 안 한 컬럼에 적용)
  });

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>에러 발생: {error?.message || '불러오기 실패'}</div>;

  return (
    <div className="border border-gray-300 rounded-lg overflow-auto">
      {/* table-fixed로 레이아웃 안정화 */}
      <table className="w-full text-sm table-fixed">
        <thead className="bg-gray-50">
          {table.getHeaderGroups().map((hg) => (
            <tr key={hg.id}>
              {hg.headers.map((header) => {
                const w = header.getSize(); // ← 컬럼 폭(px)
                return (
                  <th
                    key={header.id}
                    scope="col"
                    aria-sort="none"
                    className="border-b border-gray-300 px-3 py-2 text-left select-none overflow-hidden whitespace-nowrap text-ellipsis"
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
                결재내역이 없습니다.
              </td>
            </tr>
          ) : (
            table.getRowModel().rows.map((row) => (
              <tr
                key={row.original.id ?? row.id}
                className="border-b border-gray-300 last:border-b-0 hover:bg-gray-50"
              >
                {row.getVisibleCells().map((cell) => {
                  const w = cell.column.getSize(); // ← 컬럼 폭(px)
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

export default ApprovalListt;
