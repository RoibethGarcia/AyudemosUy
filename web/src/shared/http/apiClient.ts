import axios from "axios";

const DEFAULT_API_BASE_URL = "http://localhost:8081/api";

export const apiClient = axios.create({
    baseURL: getApiBaseUrl(),
    withCredentials: true,
    headers: {
        "Content-Type": "application/json",
    },
    timeout: 10000,
});

function getApiBaseUrl(): string {
    const configuredValue = import.meta.env.VITE_API_BASE_URL;

    if (typeof configuredValue !== "string") {
        return DEFAULT_API_BASE_URL;
    }

    const trimmedValue = configuredValue.trim();

    if (trimmedValue.length == 0) {
        return DEFAULT_API_BASE_URL;
    }

    return trimmedValue;
}
