// src/features/auth/authSlice.js
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { jwtDecode } from "jwt-decode";

const API_BASE = "http://localhost:8000/api";

// 🔄 Refresh token
export const refreshToken = createAsyncThunk(
  "auth/refreshToken",
  async (_, { getState, rejectWithValue }) => {
    const { authToken } = getState().auth;
    if (!authToken?.refresh) return rejectWithValue("No refresh token");

    try {
      const res = await fetch(`${API_BASE}/token/refresh/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refresh: authToken.refresh }),
      });

      const data = await res.json();
      if (res.status === 200) {
        return {
          access: data.access,
          refresh: authToken.refresh,
        };
      } else {
        return rejectWithValue("Failed to refresh token");
      }
    } catch (err) {
      return rejectWithValue(err.message);
    }
  }
);

// 🟢 Login
export const loginUser = createAsyncThunk(
  "auth/loginUser",
  async ({ email, password }, { rejectWithValue }) => {
    try {
      const res = await fetch(`${API_BASE}/login/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await res.json();
      if (res.status === 200) return data;
      return rejectWithValue(data);
    } catch (err) {
      return rejectWithValue(err.message);
    }
  }
);

// 🟢 Register
export const registerUser = createAsyncThunk(
  "auth/registerUser",
  async ({ email, password }, { rejectWithValue }) => {
    try {
      const res = await fetch(`${API_BASE}/register/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (res.status === 201) {
        return true; // success
      } else {
        const data = await res.json();
        return rejectWithValue(data);
      }
    } catch (err) {
      return rejectWithValue(err.message);
    }
  }
);

const initialState = {
  authToken: localStorage.getItem("authToken")
    ? JSON.parse(localStorage.getItem("authToken"))
    : null,
  user: localStorage.getItem("authToken")
    ? jwtDecode(JSON.parse(localStorage.getItem("authToken")).access)
    : null,
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    logout: (state) => {
      state.authToken = null;
      state.user = null;
      localStorage.removeItem("authToken");
    },
    setTokens: (state, action) => {
    state.authToken = action.payload;
    state.user = action.payload.access ? jwtDecode(action.payload.access) : null;
    localStorage.setItem("authToken", JSON.stringify(action.payload));
  },
  },
  extraReducers: (builder) => {
    builder
      .addCase(loginUser.pending, (state) => {
        state.loading = true;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.loading = false;
        state.authToken = action.payload;
        state.user = jwtDecode(action.payload.access);
        localStorage.setItem("authToken", JSON.stringify(action.payload));
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Login failed";
      })

      .addCase(refreshToken.fulfilled, (state, action) => {
        state.authToken = action.payload;
        state.user = jwtDecode(action.payload.access);
        localStorage.setItem("authToken", JSON.stringify(action.payload));
      })
      .addCase(refreshToken.rejected, (state) => {
        state.authToken = null;
        state.user = null;
        localStorage.removeItem("authToken");
      });
  },
});

export const { logout , setTokens } = authSlice.actions;
export default authSlice.reducer;
