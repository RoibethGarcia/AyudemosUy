import { apiClient } from "../../shared/http/apiClient";
import type { Beneficiario, BeneficiarioFiltros, BeneficiarioPayload } from "./beneficiarioTypes";

const BENEFICIARIOS_ENDPOINT = "/beneficiarios";

export async function listarBeneficiarios(filtros: BeneficiarioFiltros): Promise<Beneficiario[]> {
    const response = await apiClient.get<Beneficiario[]>(BENEFICIARIOS_ENDPOINT, {
        params: buildParams(filtros),
    });
    return response.data;
}

export async function crearBeneficiario(payload: BeneficiarioPayload): Promise<Beneficiario> {
    const response = await apiClient.post<Beneficiario>(BENEFICIARIOS_ENDPOINT, payload);
    return response.data;
}

export async function actualizarBeneficiario(id: number, payload: BeneficiarioPayload): Promise<Beneficiario> {
    const response = await apiClient.put<Beneficiario>(`${BENEFICIARIOS_ENDPOINT}/${id}`, payload);
    return response.data;
}

function buildParams(filtros: BeneficiarioFiltros): Record<string, string> {
    const params: Record<string, string> = {};

    if (typeof filtros.zona === "string" && filtros.zona.length > 0) {
        params.zona = filtros.zona;
    }

    if (typeof filtros.estado === "string" && filtros.estado.length > 0) {
        params.estado = filtros.estado;
    }

    return params;
}
