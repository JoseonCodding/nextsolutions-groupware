import useFetchWithUserId from '../../hooks/useFetchWithUserId';
import DoughnutChart from './DoughnutChart';

const ProgressChart = () => {
  const {
    data: summary,
    loading,
    error,
  } = useFetchWithUserId('/projects/status-summary');

  if (loading) return <p>로딩 중...</p>;
  if (error) return <p>에러: {error.message || '알 수 없는 에러'}</p>;
  if (!summary) return <p>데이터가 없습니다.</p>;

  const total = summary.total ?? 0;
  const progress = summary.progress ?? 0;
  const complete = summary.complete ?? 0;
  const pending = summary.pending ?? 0;

  const chartData = {
    labels: ['진행중', '완료', '대기'],
    datasets: [
      {
        label: '상태 분포',
        data: [progress, complete, pending],
        backgroundColor: ['#60a5fa', '#34d399', '#fbbf24'], // 파랑/초록/노랑
        borderWidth: 1,
      },
    ],
  };

  const options = {
    cutout: '70%',
    plugins: {
      legend: { display: true, position: 'bottom' },
      tooltip: {
        callbacks: {
          label: (ctx) => {
            const label = ctx.label || '';
            const val = ctx.parsed || 0;
            const pct = total ? Math.round((val * 1000) / total) / 10 : 0; // 소수 1자리
            return `${label}: ${val}건 (${pct}%)`;
          },
        },
      },
    },
  };

  // 백엔드에서 percent도 내려주면 우측 요약에 사용 가능
  const pct = summary.percent || { progress: 0, complete: 0, pending: 0 };

  return (
    <div className="flex flex-col gap-4 p-4 border border-gray-200 rounded-md bg-white">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* 도넛 차트: 중앙에 총 건수 */}
        <div className="w-full">
          <DoughnutChart
            chartData={chartData}
            options={options}
            centerLabel={`총 ${total}건`}
          />
        </div>

        {/* 우측 요약 */}
        <dl className="flex flex-col justify-center text-sm text-gray-700">
          <Row label="진행중" value={progress} pct={pct.progress} />
          <Row label="완료" value={complete} pct={pct.complete} />
          <Row label="대기" value={pending} pct={pct.pending} />
        </dl>
      </div>
    </div>
  );
};

function Row({ label, value = 0, pct = 0 }) {
  return (
    <div className="flex items-center justify-between py-1">
      <dt className="font-bold">{label}</dt>
      <dd>
        {value}건 • {pct}%
      </dd>
    </div>
  );
}

export default ProgressChart;
