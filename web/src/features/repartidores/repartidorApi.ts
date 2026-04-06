import { apiClient } from "../../shared/http/apiClient";
import type { Repartidor, RepartidorPayload } from "./repartidorTypes";

const REPARTIDORES_ENDPOINT = "/repartidores";

export async function listarRepartidores(): Promise<Repartidor[]> {
    const response = await apiClient.get<Repartidor[]>(REPARTIDORES_ENDPOINT);
    return response.data;
}

export async function crearRepartidor(payload: RepartidorPayload): Promise<Repartidor> {
    const response = await apiClient.post<Repartidor>(REPARTIDORES_ENDPOINT, payload);
    return response.data;
}

export async function actualizarRepartidor(id: number, payload: RepartidorPayload): Promise<Repartidor> {
    const response = await apiClient.put<Repartidor>(`${REPARTIDORES_ENDPOINT}/${id}`, payload);
    return response.data;
}
