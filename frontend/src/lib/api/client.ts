import axios from 'axios';

const baseURL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8081/api/v1';

export const apiClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

apiClient.interceptors.request.use(
  (config) => {
    // Generate trace correlation headers if not present
    if (!config.headers['X-Correlation-ID']) {
      config.headers['X-Correlation-ID'] = crypto.randomUUID();
    }
    if (!config.headers['X-Trace-ID']) {
      config.headers['X-Trace-ID'] = crypto.randomUUID();
    }
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const errorDetails = error.response?.data?.message || error.response?.data?.error || error.message;
    console.error(`[API Error] ${error.config?.method?.toUpperCase()} ${error.config?.url}:`, errorDetails);
    return Promise.reject(error);
  }
);
