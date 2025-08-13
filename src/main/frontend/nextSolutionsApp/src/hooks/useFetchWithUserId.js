import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from '../lib/axios';

/**
 * @param {string} endpoint - '/employees', '/projects' 등. '/api'는 쓰지 않음
 * @param {string} paramName - 쿼리 파라미터 이름 (기본값 'userId')
 */
const useFetchWithUserId = (endpoint, paramName = 'userId') => {
  const { user } = useSelector((state) => state.auth); // Redux에서 로그인 유저 가져오기
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // 로그인 정보가 없으면 요청 안 함
    /*
    if (!user?.id) {
      setLoading(false);
      return;
    }*/

    let ignore = false;

    const fetchData = async () => {
      try {
        const res = await axios.get(endpoint, {
          params: { [paramName]: user.employeeId },
        });
        if (!ignore) setData(res.data);
      } catch (err) {
        if (!ignore) setError(err);
      } finally {
        if (!ignore) setLoading(false);
      }
    };

    fetchData();

    return () => {
      ignore = true;
    };
  }, [endpoint, paramName, user?.id]);

  return { data, loading, error };
};

export default useFetchWithUserId;
