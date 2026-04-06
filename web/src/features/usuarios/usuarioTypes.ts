export type Usuario = Readonly<{
    id: number;
    nombre: string;
    correo: string;
}>;

export type UsuarioPayload = Readonly<{
    nombre: string;
    correo: string;
    contrasena?: string;
}>;
