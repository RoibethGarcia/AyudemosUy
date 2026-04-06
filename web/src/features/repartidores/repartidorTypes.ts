export type Repartidor = Readonly<{
    id: number;
    nombre: string;
    correo: string;
    numeroLicencia: string;
}>;

export type RepartidorPayload = Readonly<{
    nombre: string;
    correo: string;
    contrasena?: string;
    numeroLicencia: string;
}>;
