import { useEffect, useState } from 'react';
import axios from '../lib/axios';

const useSessionUser = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let ignore = false;
    const run = async () => {
      try {
        const res = await axios.get('/logInfo');
        if (!ignore) setUser(res.data);
      } catch (e) {
        if (!ignore) setError(e);
      } finally {
        if (!ignore) setLoading(false);
      }
    };
    run();
    return () => {
      ignore = true;
    };
  }, []);

  return { user, loading, error };
};

export default useSessionUser;
