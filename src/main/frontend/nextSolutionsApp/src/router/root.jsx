import { createBrowserRouter } from 'react-router-dom';
import BasicLayout from '../layouts/BasicLayout';
import MainPage from '../pages/MainPage';

const root = createBrowserRouter([
  {
    path: '/rc/',
    element: <BasicLayout />,
    children: [
      {
        index: true,
        element: <MainPage />,
      },
    ],
  },
]);

export default root;
