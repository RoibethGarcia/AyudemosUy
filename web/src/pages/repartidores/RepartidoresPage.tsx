import { useEffect, useMemo, useState, type FormEvent } from "react";

import { getApiErrorMessage, getApiFieldErrors } from "../../features/shared/apiError";
import {
    actualizarRepartidor,
    crearRepartidor,
    listarRepartidores,
} from "../../features/repartidores/repartidorApi";
import type { Repartidor, RepartidorPayload } from "../../features/repartidores/repartidorTypes";
import { EmptyState } from "../../shared/ui/EmptyState";
import { FormField } from "../../shared/ui/FormField";
import { PageHeader } from "../../shared/ui/PageHeader";
import { StatusBanner } from "../../shared/ui/StatusBanner";

type RepartidorFormState = {
    nombre: string;
    correo: string;
    contrasena: string;
    numeroLicencia: string;
};

const INITIAL_FORM_STATE: RepartidorFormState = {
    nombre: "",
    correo: "",
    contrasena: "",
    numeroLicencia: "",
};

export function RepartidoresPage(): JSX.Element {
    const [repartidores, setRepartidores] = useState<Repartidor[]>([]);
    const [formulario, setFormulario] = useState<RepartidorFormState>(INITIAL_FORM_STATE);
    const [repartidorEnEdicionId, setRepartidorEnEdicionId] = useState<number | null>(null);
    const [cargando, setCargando] = useState<boolean>(true);
    const [guardando, setGuardando] = useState<boolean>(false);
    const [errorVista, setErrorVista] = useState<string>("");
    const [mensajeExito, setMensajeExito] = useState<string>("");
    const [erroresFormulario, setErroresFormulario] = useState<Record<string, string>>({});

    useEffect(() => {
        void cargarRepartidores();
    }, []);

    const tituloFormulario = useMemo(() => {
        if (repartidorEnEdicionId == null) {
            return "Nuevo repartidor";
        }

        return `Editando repartidor #${repartidorEnEdicionId}`;
    }, [repartidorEnEdicionId]);

    async function cargarRepartidores(): Promise<void> {
        setCargando(true);
        setErrorVista("");

        try {
            const repartidoresCargados = await listarRepartidores();
            setRepartidores(repartidoresCargados);
        } catch (error) {
            setErrorVista(getApiErrorMessage(error, "No se pudieron cargar los repartidores."));
        } finally {
            setCargando(false);
        }
    }

    function actualizarCampo<K extends keyof RepartidorFormState>(campo: K, valor: RepartidorFormState[K]): void {
        setFormulario((valorActual) => ({
            ...valorActual,
            [campo]: valor,
        }));
    }

    function limpiarFormulario(): void {
        setFormulario(INITIAL_FORM_STATE);
        setRepartidorEnEdicionId(null);
        setErroresFormulario({});
    }

    function editarRepartidor(repartidor: Repartidor): void {
        setRepartidorEnEdicionId(repartidor.id);
        setFormulario({
            nombre: repartidor.nombre,
            correo: repartidor.correo,
            contrasena: "",
            numeroLicencia: repartidor.numeroLicencia,
        });
        setMensajeExito("");
        setErroresFormulario({});
    }

    async function enviarFormulario(event: FormEvent<HTMLFormElement>): Promise<void> {
        event.preventDefault();
        setGuardando(true);
        setMensajeExito("");
        setErroresFormulario({});
        setErrorVista("");

        const payload: RepartidorPayload = repartidorEnEdicionId == null
            ? {
                  nombre: formulario.nombre.trim(),
                  correo: formulario.correo.trim(),
                  contrasena: formulario.contrasena.trim(),
                  numeroLicencia: formulario.numeroLicencia.trim(),
              }
            : {
                  nombre: formulario.nombre.trim(),
                  correo: formulario.correo.trim(),
                  numeroLicencia: formulario.numeroLicencia.trim(),
              };

        try {
            if (repartidorEnEdicionId == null) {
                await crearRepartidor(payload);
                setMensajeExito("Repartidor creado correctamente.");
            } else {
                await actualizarRepartidor(repartidorEnEdicionId, payload);
                setMensajeExito("Repartidor actualizado correctamente.");
            }

            limpiarFormulario();
            await cargarRepartidores();
        } catch (error) {
            setErroresFormulario(getApiFieldErrors(error));
            setErrorVista(getApiErrorMessage(error, "No se pudo guardar el repartidor."));
        } finally {
            setGuardando(false);
        }
    }

    return (
        <div className="page-stack">
            <PageHeader
                title="Repartidores"
                description="Alta, edición y consulta de repartidores conectados al subtipo explícito del backend."
            />

            {mensajeExito.length > 0 ? <StatusBanner tone="success" message={mensajeExito} /> : null}
            {errorVista.length > 0 ? <StatusBanner tone="error" message={errorVista} /> : null}

            <div className="content-grid content-grid--two-columns">
                <section className="info-card">
                    <div className="section-heading">
                        <h2>{tituloFormulario}</h2>
                        <p>La UI debe respetar unicidad global de correo y unicidad propia de número de licencia.</p>
                    </div>

                    <form className="entity-form" onSubmit={(event) => void enviarFormulario(event)}>
                        <FormField label="Nombre" htmlFor="repartidor-nombre" error={erroresFormulario.nombre}>
                            <input
                                id="repartidor-nombre"
                                className="text-input"
                                value={formulario.nombre}
                                onChange={(event) => actualizarCampo("nombre", event.target.value)}
                            />
                        </FormField>

                        <FormField label="Correo" htmlFor="repartidor-correo" error={erroresFormulario.correo}>
                            <input
                                id="repartidor-correo"
                                type="email"
                                className="text-input"
                                value={formulario.correo}
                                onChange={(event) => actualizarCampo("correo", event.target.value)}
                            />
                        </FormField>

                        {repartidorEnEdicionId == null ? (
                            <FormField
                                label="Contraseña"
                                htmlFor="repartidor-contrasena"
                                error={erroresFormulario.contrasena}
                            >
                                <input
                                    id="repartidor-contrasena"
                                    type="password"
                                    className="text-input"
                                    value={formulario.contrasena}
                                    onChange={(event) => actualizarCampo("contrasena", event.target.value)}
                                    placeholder="Mínimo 8 caracteres"
                                />
                            </FormField>
                        ) : null}

                        <FormField
                            label="Número de licencia"
                            htmlFor="repartidor-licencia"
                            error={erroresFormulario.numeroLicencia}
                        >
                            <input
                                id="repartidor-licencia"
                                className="text-input"
                                value={formulario.numeroLicencia}
                                onChange={(event) => actualizarCampo("numeroLicencia", event.target.value)}
                            />
                        </FormField>

                        <div className="form-actions">
                            <button type="submit" className="button button--primary" disabled={guardando}>
                                {guardando
                                    ? "Guardando..."
                                    : repartidorEnEdicionId == null
                                      ? "Crear repartidor"
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
                        <p>Desde aquí puedes elegir un repartidor para editarlo sin tocar rutas manuales.</p>
                    </div>

                    {cargando ? <StatusBanner tone="info" message="Cargando repartidores..." /> : null}

                    {!cargando && repartidores.length === 0 ? (
                        <EmptyState
                            title="No hay repartidores todavía"
                            description="Crea el primer repartidor para poder asignarlo luego a distribuciones."
                        />
                    ) : null}

                    {!cargando && repartidores.length > 0 ? (
                        <div className="table-wrapper">
                            <table className="data-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nombre</th>
                                        <th>Correo</th>
                                        <th>Licencia</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {repartidores.map((repartidor) => (
                                        <tr key={repartidor.id}>
                                            <td>{repartidor.id}</td>
                                            <td>{repartidor.nombre}</td>
                                            <td>{repartidor.correo}</td>
                                            <td>{repartidor.numeroLicencia}</td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="button button--ghost"
                                                    onClick={() => editarRepartidor(repartidor)}
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
