import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from '../lib/axios'; //axios 인스턴스

const initialState = {
  user: null,
  loading: true,
  error: null,
};

// 로그인된 사원 정보 가져오기
export const fetchUserAsync = createAsyncThunk(
  'auth/fetchUserAsync',
  async (_, { rejectWithValue }) => {
    try {
      const res = await axios.get('/logInfo'); // 세션 기반 로그인 정보

      console.log(res.data);

      return res.data;
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.response?.data ||
        err.message ||
        '로그인 필요';
      return rejectWithValue(message);
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      state.user = null;
      state.error = null;
      state.loading = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUserAsync.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUserAsync.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
      })
      .addCase(fetchUserAsync.rejected, (state, action) => {
        state.loading = false;
        state.user = null;
        state.error = action.payload;
      });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;
