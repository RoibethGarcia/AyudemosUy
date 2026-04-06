export type AuthSession = Readonly<{
    id: number;
    nombre: string;
    correo: string;
    tipoUsuario: "USUARIO" | "BENEFICIARIO" | "REPARTIDOR";
}>;

export type LoginPayload = Readonly<{
    correo: string;
    contrasena: string;
}>;
