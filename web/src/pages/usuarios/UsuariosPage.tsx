import { useEffect, useMemo, useState, type FormEvent } from "react";

import { getApiErrorMessage, getApiFieldErrors } from "../../features/shared/apiError";
import { actualizarUsuario, crearUsuario, listarUsuarios } from "../../features/usuarios/usuarioApi";
import type { Usuario, UsuarioPayload } from "../../features/usuarios/usuarioTypes";
import { EmptyState } from "../../shared/ui/EmptyState";
import { FormField } from "../../shared/ui/FormField";
import { PageHeader } from "../../shared/ui/PageHeader";
import { StatusBanner } from "../../shared/ui/StatusBanner";

type UsuarioFormState = {
    nombre: string;
    correo: string;
    contrasena: string;
};

const INITIAL_FORM_STATE: UsuarioFormState = {
    nombre: "",
    correo: "",
    contrasena: "",
};

export function UsuariosPage(): JSX.Element {
    const [usuarios, setUsuarios] = useState<Usuario[]>([]);
    const [formulario, setFormulario] = useState<UsuarioFormState>(INITIAL_FORM_STATE);
    const [usuarioEnEdicionId, setUsuarioEnEdicionId] = useState<number | null>(null);
    const [cargando, setCargando] = useState<boolean>(true);
    const [guardando, setGuardando] = useState<boolean>(false);
    const [errorCarga, setErrorCarga] = useState<string>("");
    const [mensajeExito, setMensajeExito] = useState<string>("");
    const [erroresFormulario, setErroresFormulario] = useState<Record<string, string>>({});

    useEffect(() => {
        void cargarUsuarios();
    }, []);

    const tituloFormulario = useMemo(() => {
        if (usuarioEnEdicionId == null) {
            return "Nuevo usuario";
        }

        return `Editando usuario #${usuarioEnEdicionId}`;
    }, [usuarioEnEdicionId]);

    async function cargarUsuarios(): Promise<void> {
        setCargando(true);
        setErrorCarga("");

        try {
            const usuariosCargados = await listarUsuarios();
            setUsuarios(usuariosCargados);
        } catch (error) {
            setErrorCarga(getApiErrorMessage(error, "No se pudieron cargar los usuarios."));
        } finally {
            setCargando(false);
        }
    }

    function actualizarCampo<K extends keyof UsuarioFormState>(campo: K, valor: UsuarioFormState[K]): void {
        setFormulario((valorActual) => ({
            ...valorActual,
            [campo]: valor,
        }));
    }

    function limpiarFormulario(): void {
        setFormulario(INITIAL_FORM_STATE);
        setUsuarioEnEdicionId(null);
        setErroresFormulario({});
    }

    function editarUsuario(usuario: Usuario): void {
        setUsuarioEnEdicionId(usuario.id);
        setFormulario({
            nombre: usuario.nombre,
            correo: usuario.correo,
            contrasena: "",
        });
        setErroresFormulario({});
        setMensajeExito("");
    }

    async function enviarFormulario(event: FormEvent<HTMLFormElement>): Promise<void> {
        event.preventDefault();
        setGuardando(true);
        setMensajeExito("");
        setErroresFormulario({});
        setErrorCarga("");

        const payload: UsuarioPayload = usuarioEnEdicionId == null
            ? {
                  nombre: formulario.nombre.trim(),
                  correo: formulario.correo.trim(),
                  contrasena: formulario.contrasena.trim(),
              }
            : {
                  nombre: formulario.nombre.trim(),
                  correo: formulario.correo.trim(),
              };

        try {
            if (usuarioEnEdicionId == null) {
                await crearUsuario(payload);
                setMensajeExito("Usuario creado correctamente.");
            } else {
                await actualizarUsuario(usuarioEnEdicionId, payload);
                setMensajeExito("Usuario actualizado correctamente.");
            }

            limpiarFormulario();
            await cargarUsuarios();
        } catch (error) {
            setErroresFormulario(getApiFieldErrors(error));
            setErrorCarga(getApiErrorMessage(error, "No se pudo guardar el usuario."));
        } finally {
            setGuardando(false);
        }
    }

    return (
        <div className="page-stack">
            <PageHeader
                title="Usuarios"
                description="Alta, edición y consulta de usuarios base conectados directamente contra la API real."
            />

            {mensajeExito.length > 0 ? <StatusBanner tone="success" message={mensajeExito} /> : null}
            {errorCarga.length > 0 ? <StatusBanner tone="error" message={errorCarga} /> : null}

            <div className="content-grid content-grid--two-columns">
                <section className="info-card">
                    <div className="section-heading">
                        <h2>{tituloFormulario}</h2>
                        <p>Este formulario trabaja sobre `/usuarios` y mantiene la unicidad global del correo.</p>
                    </div>

                    <form className="entity-form" onSubmit={(event) => void enviarFormulario(event)}>
                        <FormField label="Nombre" htmlFor="usuario-nombre" error={erroresFormulario.nombre}>
                            <input
                                id="usuario-nombre"
                                className="text-input"
                                value={formulario.nombre}
                                onChange={(event) => actualizarCampo("nombre", event.target.value)}
                                placeholder="Ej. Ana Pérez"
                            />
                        </FormField>

                        <FormField label="Correo" htmlFor="usuario-correo" error={erroresFormulario.correo}>
                            <input
                                id="usuario-correo"
                                type="email"
                                className="text-input"
                                value={formulario.correo}
                                onChange={(event) => actualizarCampo("correo", event.target.value)}
                                placeholder="ana@ayudemos.uy"
                            />
                        </FormField>

                        {usuarioEnEdicionId == null ? (
                            <FormField
                                label="Contraseña"
                                htmlFor="usuario-contrasena"
                                error={erroresFormulario.contrasena}
                            >
                                <input
                                    id="usuario-contrasena"
                                    type="password"
                                    className="text-input"
                                    value={formulario.contrasena}
                                    onChange={(event) => actualizarCampo("contrasena", event.target.value)}
                                    placeholder="Mínimo 8 caracteres"
                                />
                            </FormField>
                        ) : null}

                        <div className="form-actions">
                            <button type="submit" className="button button--primary" disabled={guardando}>
                                {guardando
                                    ? "Guardando..."
                                    : usuarioEnEdicionId == null
                                      ? "Crear usuario"
                                      : "Guardar cambios"}
                            </button>
                            <button
                                type="button"
                                className="button button--secondary"
                                onClick={limpiarFormulario}
                                disabled={guardando}
                            >
                                Limpiar
                            </button>
                        </div>
                    </form>
                </section>

                <section className="info-card">
                    <div className="section-heading">
                        <h2>Listado actual</h2>
                        <p>Selecciona un usuario para cargarlo en el formulario y editarlo.</p>
                    </div>

                    {cargando ? <StatusBanner tone="info" message="Cargando usuarios..." /> : null}

                    {!cargando && usuarios.length === 0 ? (
                        <EmptyState
                            title="No hay usuarios todavía"
                            description="Crea el primer usuario base para comenzar a poblar el sistema."
                        />
                    ) : null}

                    {!cargando && usuarios.length > 0 ? (
                        <div className="table-wrapper">
                            <table className="data-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nombre</th>
                                        <th>Correo</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {usuarios.map((usuario) => (
                                        <tr key={usuario.id}>
                                            <td>{usuario.id}</td>
                                            <td>{usuario.nombre}</td>
                                            <td>{usuario.correo}</td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="button button--ghost"
                                                    onClick={() => editarUsuario(usuario)}
                                                >
                                                    Editar
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    ) : null}
                </section>
            </div>
        </div>
    );
}
