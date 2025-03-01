import axios from "axios";

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:9090/api";

// Create an axios instance with default config
const api = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add a request interceptor to add the auth token to every request
api.interceptors.request.use(
  (config) => {
    const token = document.cookie
      .split("; ")
      .find((row) => row.startsWith("token="))
      ?.split("=")[1];
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Add a response interceptor to handle token expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      document.cookie = "token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: async (username: string, password: string) => {
    const response = await api.post("/auth", { username, password });
    return response.data;
  },
};

// User API
export const userAPI = {
  // Get current user profile
  getMyProfile: async () => {
    const response = await api.get("/users/me");
    return response.data;
  },

  // Get user by username (admin only)
  getUserByUsername: async (username: string) => {
    const response = await api.get(`/users/${username}`);
    return response.data;
  },

  // Generate users
  generateUsers: async (count: number) => {
    const response = await api.get(`/users/generate?count=${count}`, {
      responseType: "blob",
    });
    return response.data;
  },

  // Batch import users
  batchImportUsers: async (file: File) => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await api.post("/users/batch", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },
};

export default api;
