import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";

import { iniciarSesion } from "../../features/auth/authApi";
import type { AuthSession } from "../../features/auth/authTypes";
import { getApiErrorMessage } from "../../features/shared/apiError";
import { FormField } from "../../shared/ui/FormField";
import { PageHeader } from "../../shared/ui/PageHeader";
import { StatusBanner } from "../../shared/ui/StatusBanner";

type LoginFormState = {
    correo: string;
    contrasena: string;
};

const INITIAL_FORM_STATE: LoginFormState = {
    correo: "",
    contrasena: "",
};

const AUTH_SESSION_STORAGE_KEY = "ayudemos-auth-session";

export function LoginPage(): JSX.Element {
    const [formulario, setFormulario] = useState<LoginFormState>(INITIAL_FORM_STATE);
    const [mensaje, setMensaje] = useState<string>(
        "Backend verificado: ahora existe autenticación real por sesión para Usuario, Beneficiario y Repartidor.",
    );
    const [guardando, setGuardando] = useState<boolean>(false);
    const [sesionActiva, setSesionActiva] = useState<AuthSession | null>(leerSesionGuardada());

    function actualizarCampo<K extends keyof LoginFormState>(campo: K, valor: LoginFormState[K]): void {
        setFormulario((valorActual) => ({
            ...valorActual,
            [campo]: valor,
        }));
    }

    async function enviarFormulario(event: FormEvent<HTMLFormElement>): Promise<void> {
        event.preventDefault();

        const correoNormalizado = formulario.correo.trim();
        const contrasenaNormalizada = formulario.contrasena.trim();
        const faltanCredenciales = correoNormalizado.length === 0 || contrasenaNormalizada.length === 0;

        if (faltanCredenciales) {
            setMensaje("Completa correo y contraseña.");
            return;
        }

        setGuardando(true);

        try {
            const sesion = await iniciarSesion({
                correo: correoNormalizado,
                contrasena: contrasenaNormalizada,
            });

            window.sessionStorage.setItem(AUTH_SESSION_STORAGE_KEY, JSON.stringify(sesion));
            setSesionActiva(sesion);
            setMensaje(`Sesión iniciada correctamente como ${formatTipoUsuario(sesion.tipoUsuario)}.`);
        } catch (error) {
            setMensaje(getApiErrorMessage(error, "No se pudo iniciar sesión."));
        } finally {
            setGuardando(false);
        }
    }

    return (
        <div className="page-stack">
            <PageHeader
                title="Iniciar sesión"
                description="Pantalla conectada al backend real. YA NO es un placeholder."
            />

            <div className="content-grid content-grid--two-columns">
                <section className="info-card">
                    <div className="section-heading">
                        <h2>Acceso</h2>
                        <p>
                            El login usa `/auth/login` y abre una sesión HTTP real contra el backend de Spring Boot.
                        </p>
                    </div>

                    <form className="entity-form" onSubmit={(event) => void enviarFormulario(event)}>
                        <FormField label="Correo" htmlFor="login-correo">
                            <input
                                id="login-correo"
                                type="email"
                                className="text-input"
                                value={formulario.correo}
                                onChange={(event) => actualizarCampo("correo", event.target.value)}
                                placeholder="operador@ayudemos.uy"
                            />
                        </FormField>

                        <FormField label="Contraseña" htmlFor="login-contrasena">
                            <input
                                id="login-contrasena"
                                type="password"
                                className="text-input"
                                value={formulario.contrasena}
                                onChange={(event) => actualizarCampo("contrasena", event.target.value)}
                                placeholder="••••••••"
                            />
                        </FormField>

                        <div className="form-actions">
                            <button type="submit" className="button button--primary" disabled={guardando}>
                                {guardando ? "Ingresando..." : "Ingresar"}
                            </button>
                            <Link to="/usuarios" className="button button--secondary landing-link-button">
                                Crear usuario
                            </Link>
                        </div>
                    </form>
                </section>

                <section className="info-card">
                    <div className="section-heading">
                        <h2>Estado de implementación</h2>
                        <p>
                            Esto es IMPORTANTE: ahora hay autenticación real, pero todavía falta definir autorización
                            por rol y endpoint.
                        </p>
                    </div>

                    <StatusBanner tone="info" message={mensaje} />

                    {sesionActiva != null ? (
                        <div className="landing-note">
                            <h3>Sesión actual</h3>
                            <p>
                                {sesionActiva.nombre} ({sesionActiva.correo}) - {" "}
                                {formatTipoUsuario(sesionActiva.tipoUsuario)}
                            </p>
                        </div>
                    ) : null}

                    <div className="landing-note">
                        <h3>Siguiente paso técnico</h3>
                        <p>
                            Definir permisos por tipo de usuario. Login sin autorización completa NO es seguridad
                            terminada.
                        </p>
                    </div>
                </section>
            </div>
        </div>
    );
}

function leerSesionGuardada(): AuthSession | null {
    const valorGuardado = window.sessionStorage.getItem(AUTH_SESSION_STORAGE_KEY);

    if (valorGuardado == null || valorGuardado.trim().length === 0) {
        return null;
    }

    try {
        return JSON.parse(valorGuardado) as AuthSession;
    } catch {
        return null;
    }
}

function formatTipoUsuario(tipoUsuario: AuthSession["tipoUsuario"]): string {
    return tipoUsuario.toLowerCase().charAt(0).toUpperCase() + tipoUsuario.toLowerCase().slice(1);
}