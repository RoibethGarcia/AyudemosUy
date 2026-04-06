export const BARRIOS = [
    "CIUDAD_VIEJA",
    "CORDON",
    "PARQUE_RODO",
    "CENTRO",
    "PALERMO",
] as const;

export const ESTADOS_BENEFICIARIO = [
    "ACTIVO",
    "SUSPENDIDO",
] as const;

export type Barrio = (typeof BARRIOS)[number];
export type EstadoBeneficiario = (typeof ESTADOS_BENEFICIARIO)[number];

export type Beneficiario = Readonly<{
    id: number;
    nombre: string;
    correo: string;
    direccion: string;
    fechaNacimiento: string;
    estado: EstadoBeneficiario;
    barrio: Barrio;
}>;

export type BeneficiarioPayload = Readonly<{
    nombre: string;
    correo: string;
    contrasena?: string;
    direccion: string;
    fechaNacimiento: string;
    estado: EstadoBeneficiario;
    barrio: Barrio;
}>;

export type BeneficiarioFiltros = Readonly<{
    zona?: Barrio;
    estado?: EstadoBeneficiario;
}>;
