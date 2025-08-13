import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Doughnut } from 'react-chartjs-2';

// Chart.js에서 필요한 요소 등록
ChartJS.register(ArcElement, Tooltip, Legend);

const DoughnutChart = ({ chartData, options, centerLabel }) => {
  return (
    <div className="relative">
      <Doughnut data={chartData} options={options} />
      <div className="absolute top-[50%] left-[50%] translate-x-[-50%] translate-y-[-175%] text-xl font-bold">
        {centerLabel}
      </div>
    </div>
  );
};

export default DoughnutChart;
