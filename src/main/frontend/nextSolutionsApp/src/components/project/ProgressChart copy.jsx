import useFetchWithUserId from '../../hooks/useFetchWithUserId';
import DoughnutChart from './DoughnutChart';

const ProgressChart = () => {
  const { data, loading, error } = useFetchWithUserId('/projects');

  if (loading) return <p>로딩 중...</p>;
  if (error) return <p>에러: {error.message || '알 수 없는 에러'}</p>;
  if (!data || data.length === 0) return <p>데이터가 없습니다.</p>;

  // console.log(data);

  return (
    <div className="flex flex-col gap-4">
      {data.slice(0, 3).map((project) => (
        <div
          key={project.id}
          className="flex gap-4 p-4 border border-gray-200 rounded-md"
        >
          <div className="w-[50%]">
            <DoughnutChart
              chartData={{
                labels: [],
                datasets: [
                  {
                    label: '진행률',
                    data: [project.progress, 100 - project.progress],
                    backgroundColor: ['#36A2EB', '#E5E5E5'], // 진행률과 나머지 부분
                    borderWidth: 1,
                  },
                ],
              }}
              options={{
                cutout: '70%',
                plugins: {
                  legend: {
                    display: false,
                  },
                },
              }}
              percentage={project.progress}
            />
          </div>
          <dl className="flex flex-col justify-center">
            <dt className="mb-1 text-sm font-bold break-keep">
              {project.title}
            </dt>
            <dd className="text-sm">{project.startDate}</dd>
            <dd className="text-sm">{project.endDate}</dd>
          </dl>
        </div>
      ))}
    </div>
  );
};

export default ProgressChart;
