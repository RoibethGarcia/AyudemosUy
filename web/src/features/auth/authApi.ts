import { apiClient } from "../../shared/http/apiClient";
import type { AuthSession, LoginPayload } from "./authTypes";

const AUTH_ENDPOINT = "/auth";

export async function iniciarSesion(payload: LoginPayload): Promise<AuthSession> {
    const response = await apiClient.post<AuthSession>(`${AUTH_ENDPOINT}/login`, payload);
    return response.data;
}

export async function obtenerSesionActual(): Promise<AuthSession> {
    const response = await apiClient.get<AuthSession>(`${AUTH_ENDPOINT}/sesion`);
    return response.data;
}

export async function cerrarSesion(): Promise<void> {
    await apiClient.post(`${AUTH_ENDPOINT}/logout`);
}
