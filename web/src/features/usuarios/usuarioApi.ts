import { apiClient } from "../../shared/http/apiClient";
import type { Usuario, UsuarioPayload } from "./usuarioTypes";

const USUARIOS_ENDPOINT = "/usuarios";

export async function listarUsuarios(): Promise<Usuario[]> {
    const response = await apiClient.get<Usuario[]>(USUARIOS_ENDPOINT);
    return response.data;
}

export async function crearUsuario(payload: UsuarioPayload): Promise<Usuario> {
    const response = await apiClient.post<Usuario>(USUARIOS_ENDPOINT, payload);
    return response.data;
}

export async function actualizarUsuario(id: number, payload: UsuarioPayload): Promise<Usuario> {
    const response = await apiClient.put<Usuario>(`${USUARIOS_ENDPOINT}/${id}`, payload);
    return response.data;
}
