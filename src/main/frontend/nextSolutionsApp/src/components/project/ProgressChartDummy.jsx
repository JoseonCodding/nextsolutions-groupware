import DoughnutChart from './DoughnutChart';

// 더미 데이터 (백엔드에서 내려올 구조와 동일하게 작성)
const dummySummary = {
  total: 25,
  progress: 10,
  complete: 12,
  pending: 3,
  percent: {
    progress: 40,
    complete: 48,
    pending: 12,
  },
};

const ProgressChartDummy = () => {
  const total = dummySummary.total;
  const progress = dummySummary.progress;
  const complete = dummySummary.complete;
  const pending = dummySummary.pending;

  const chartData = {
    labels: ['진행중', '완료', '대기'],
    datasets: [
      {
        label: '상태 분포',
        data: [progress, complete, pending],
        backgroundColor: ['#60a5fa', '#34d399', '#fbbf24'],
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
          color: '#374151', // Tailwind Gray-700
          font: {
            size: 12,
          },
          generateLabels: (chart) => {
            const datasets = chart.data.datasets[0];
            return chart.data.labels.map((label, i) => ({
              text: `${label}`,
              fillStyle: datasets.backgroundColor[i],
              strokeStyle: datasets.backgroundColor[i],
              lineWidth: 0,
            }));
          },
        },
      },
      tooltip: {
        callbacks: {
          label: (ctx) => {
            const label = ctx.label || '';
            const val = ctx.parsed || 0;
            const pct = total ? Math.round((val * 1000) / total) / 10 : 0;
            return `${label}: ${val}건 (${pct}%)`;
          },
        },
      },
    },
  };

  const pct = dummySummary.percent;

  return (
    <div className="flex flex-col gap-4 p-4 ">
      <div className="w-full">
        <DoughnutChart
          chartData={chartData}
          options={options}
          centerLabel={`총 ${total}건`}
        />
      </div>

      <div className="flex flex-col justify-center gap-4 text-sm text-gray-700">
        <Row
          label="진행중"
          value={progress}
          pct={pct.progress}
          color="#60a5fa"
        />
        <Row label="대기" value={pending} pct={pct.pending} color="#fbbf24" />
        <Row label="완료" value={complete} pct={pct.complete} color="#34d399" />
      </div>
    </div>
  );
};

const Row = ({ label, value, pct, color }) => {
  return (
    <dl className="h-[50px] flex items-center gap-2 border border-gray-50 rounded-md shadow-xl">
      <dt className="w-12 text-sm font-semibold">{label}</dt>
      <dd className="flex-1 bg-gray-200 rounded-full h-3 overflow-hidden">
        <div
          className="h-full"
          style={{
            width: `${pct}%`,
            backgroundColor: color,
          }}
        ></div>
      </dd>
      <dd className="w-20 text-right text-sm">
        {value}건 ({pct}%)
      </dd>
    </dl>
  );
};

export default ProgressChartDummy;
