import useFetch from '../../hooks/useFetch';
import DoughnutChart from './DoughnutChart';

// const COLORS = { progress: '#60a5fa', pending: '#fbbf24', complete: '#34d399' };
const COLORS = {
  progress: '#4fe7af',
  pending: '#11c4d4',
  complete: '#0c81e4',
};

const ProgressChart = () => {
  const { data, loading, error } = useFetch('/projects');

  if (loading) return <p>로딩 중...</p>;
  if (error) return <p>에러: {error.message || '알 수 없는 에러'}</p>;
  if (!data) return <p>데이터가 없습니다.</p>;

  console.log('프로젝트 데이터:', data);

  const myApprovalTodo = data.myApprovalTodoCount ?? 0;
  const myProject = data.myProjectCount ?? 0;
  const progress = Number(data.progressCount ?? 0);
  const pending = Number(data.pendingCount ?? 0);
  const complete = Number(data.completeCount ?? 0);
  const total = Number(data.totalCount ?? progress + pending + complete) || 0;

  const pct = (v) => (total ? Math.round((v * 100) / total) : 0);

  const chartData = {
    labels: ['진행중', '대기', '완료'],
    datasets: [
      {
        label: '상태 분포',
        data: [progress, pending, complete], // 라벨 순서에 맞춤
        backgroundColor: [COLORS.progress, COLORS.pending, COLORS.complete],
        borderWidth: 0,
      },
    ],
  };

  const options = {
    cutout: '70%',
    plugins: {
      legend: {
        display: true,
        position: 'bottom',
        labels: {
          boxWidth: 12,
          boxHeight: 12,
          color: '#374151',
          font: { size: 12 },
        },
      },
      tooltip: {
        callbacks: {
          label: (ctx) => {
            const label = ctx.label || '';
            const val = ctx.parsed || 0;
            return `${label}: ${val}건 (${pct(val)}%)`;
          },
        },
      },
    },
  };

  return (
    <div className="flex flex-col gap-3 px-2 py-4">
      <div className="w-full">
        <DoughnutChart
          chartData={chartData}
          options={options}
          centerLabel={`총 ${total}건`}
        />
      </div>

      <div className="flex flex-col justify-center gap-3 text-sm text-gray-600">
        <Row
          label="진행중"
          value={progress}
          pct={pct(progress)}
          color={COLORS.progress}
        />
        <Row
          label="대기"
          value={pending}
          pct={pct(pending)}
          color={COLORS.pending}
        />
        <Row
          label="완료"
          value={complete}
          pct={pct(complete)}
          color={COLORS.complete}
        />
      </div>

      <div className="flex flex-col justify-center gap-3 text-sm text-gray-600">
        <Row label="내 참여 프로젝트" value={myProject} withBar={false} />
        <Row label="내 결재 프로젝트" value={myApprovalTodo} withBar={false} />
      </div>
    </div>
  );
};

const Row = ({ label, value, pct, color, withBar = true }) => (
  <dl className="p-3.5 border border-gray-200 rounded-md">
    {withBar ? (
      <>
        <div className="flex justify-between items-center mb-1">
          <dt className="text-sm">{label}</dt>
          <div className="text-right text-sm">
            {value}건 ({pct}%)
          </div>
        </div>
        <dd>
          <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
            <div
              className="h-full transition-[width] duration-300"
              style={{ width: `${pct}%`, backgroundColor: color }}
            />
          </div>
        </dd>
      </>
    ) : (
      <>
        <dt className="text-sm">{label}</dt>
        <dd className="flex items-center justify-between">
          <div className="text-gray-500 text-xs"></div>
          <div className="text-right text-sm font-medium">{value}건</div>
        </dd>
      </>
    )}
  </dl>
);

export default ProgressChart;
