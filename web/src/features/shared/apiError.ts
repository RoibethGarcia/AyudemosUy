import axios from "axios";

export type ApiErrorResponse = Readonly<{
    code: string;
    message: string;
    details?: unknown;
    timestamp?: string;
}>;

type ErrorDetailsMap = Record<string, string>;

export function getApiErrorMessage(error: unknown, fallbackMessage: string): string {
    if (!axios.isAxiosError<ApiErrorResponse>(error)) {
        return fallbackMessage;
    }

    const responseData = error.response?.data;

    if (typeof responseData?.message === "string" && responseData.message.trim().length > 0) {
        return responseData.message;
    }

    if (typeof error.message === "string" && error.message.trim().length > 0) {
        return error.message;
    }

    return fallbackMessage;
}

export function getApiFieldErrors(error: unknown): ErrorDetailsMap {
    if (!axios.isAxiosError<ApiErrorResponse>(error)) {
        return {};
    }

    const details = error.response?.data?.details;

    if (!isErrorDetailsMap(details)) {
        return {};
    }

    return details;
}

function isErrorDetailsMap(value: unknown): value is ErrorDetailsMap {
    if (value == null || typeof value !== "object" || Array.isArray(value)) {
        return false;
    }

    const detailValues = Object.values(value);

    return detailValues.every((detailValue) => typeof detailValue === "string");
}
