import { useEffect, useState } from 'react';
import axios from '../lib/axios';

const useFetch = (endpoint, params) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);

    axios
      .get(endpoint, { params })
      .then((res) => alive && setData(res.data))
      .catch(
        (err) => alive && setError(err?.response?.data || err?.message || err)
      )
      .finally(() => alive && setLoading(false));

    return () => {
      alive = false;
    };
  }, [endpoint, JSON.stringify(params || null)]);

  return { data, loading, error };
};

export default useFetch;
