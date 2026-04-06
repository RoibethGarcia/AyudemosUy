import { useEffect, useMemo, useState, type FormEvent } from "react";

import { getApiErrorMessage, getApiFieldErrors } from "../../features/shared/apiError";
import {
    actualizarBeneficiario,
    crearBeneficiario,
    listarBeneficiarios,
} from "../../features/beneficiarios/beneficiarioApi";
import {
    BARRIOS,
    ESTADOS_BENEFICIARIO,
    type Barrio,
    type Beneficiario,
    type BeneficiarioPayload,
    type EstadoBeneficiario,
} from "../../features/beneficiarios/beneficiarioTypes";
import { EmptyState } from "../../shared/ui/EmptyState";
import { FormField } from "../../shared/ui/FormField";
import { PageHeader } from "../../shared/ui/PageHeader";
import { StatusBanner } from "../../shared/ui/StatusBanner";

type BeneficiarioFormState = {
    nombre: string;
    correo: string;
    contrasena: string;
    direccion: string;
    fechaNacimiento: string;
    estado: EstadoBeneficiario;
    barrio: Barrio;
};

const INITIAL_FORM_STATE: BeneficiarioFormState = {
    nombre: "",
    correo: "",
    contrasena: "",
    direccion: "",
    fechaNacimiento: "",
    estado: "ACTIVO",
    barrio: "CIUDAD_VIEJA",
};

export function BeneficiariosPage(): JSX.Element {
    const [beneficiarios, setBeneficiarios] = useState<Beneficiario[]>([]);
    const [formulario, setFormulario] = useState<BeneficiarioFormState>(INITIAL_FORM_STATE);
    const [beneficiarioEnEdicionId, setBeneficiarioEnEdicionId] = useState<number | null>(null);
    const [filtroZona, setFiltroZona] = useState<string>("");
    const [filtroEstado, setFiltroEstado] = useState<string>("");
    const [cargando, setCargando] = useState<boolean>(true);
    const [guardando, setGuardando] = useState<boolean>(false);
    const [errorVista, setErrorVista] = useState<string>("");
    const [mensajeExito, setMensajeExito] = useState<string>("");
    const [erroresFormulario, setErroresFormulario] = useState<Record<string, string>>({});

    useEffect(() => {
        void cargarBeneficiarios();
    }, [filtroEstado, filtroZona]);

    const tituloFormulario = useMemo(() => {
        if (beneficiarioEnEdicionId == null) {
            return "Nuevo beneficiario";
        }

        return `Editando beneficiario #${beneficiarioEnEdicionId}`;
    }, [beneficiarioEnEdicionId]);

    async function cargarBeneficiarios(): Promise<void> {
        setCargando(true);
        setErrorVista("");

        try {
            const beneficiariosCargados = await listarBeneficiarios({
                zona: parseBarrioFilter(filtroZona),
                estado: parseEstadoFilter(filtroEstado),
            });
            setBeneficiarios(beneficiariosCargados);
        } catch (error) {
            setErrorVista(getApiErrorMessage(error, "No se pudieron cargar los beneficiarios."));
        } finally {
            setCargando(false);
        }
    }

    function actualizarCampo<K extends keyof BeneficiarioFormState>(
        campo: K,
        valor: BeneficiarioFormState[K],
    ): void {
        setFormulario((valorActual) => ({
            ...valorActual,
            [campo]: valor,
        }));
    }

    function limpiarFormulario(): void {
        setFormulario(INITIAL_FORM_STATE);
        setBeneficiarioEnEdicionId(null);
        setErroresFormulario({});
    }

    function editarBeneficiario(beneficiario: Beneficiario): void {
        setBeneficiarioEnEdicionId(beneficiario.id);
        setFormulario({
            nombre: beneficiario.nombre,
            correo: beneficiario.correo,
            contrasena: "",
            direccion: beneficiario.direccion,
            fechaNacimiento: beneficiario.fechaNacimiento,
            estado: beneficiario.estado,
            barrio: beneficiario.barrio,
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

        const payload: BeneficiarioPayload = beneficiarioEnEdicionId == null
            ? {
                  nombre: formulario.nombre.trim(),
                  correo: formulario.correo.trim(),
                  contrasena: formulario.contrasena.trim(),
                  direccion: formulario.direccion.trim(),
                  fechaNacimiento: formulario.fechaNacimiento,
                  estado: formulario.estado,
                  barrio: formulario.barrio,
              }
            : {
                  nombre: formulario.nombre.trim(),
                  correo: formulario.correo.trim(),
                  direccion: formulario.direccion.trim(),
                  fechaNacimiento: formulario.fechaNacimiento,
                  estado: formulario.estado,
                  barrio: formulario.barrio,
              };

        try {
            if (beneficiarioEnEdicionId == null) {
                await crearBeneficiario(payload);
                setMensajeExito("Beneficiario creado correctamente.");
            } else {
                await actualizarBeneficiario(beneficiarioEnEdicionId, payload);
                setMensajeExito("Beneficiario actualizado correctamente.");
            }

            limpiarFormulario();
            await cargarBeneficiarios();
        } catch (error) {
            setErroresFormulario(getApiFieldErrors(error));
            setErrorVista(getApiErrorMessage(error, "No se pudo guardar el beneficiario."));
        } finally {
            setGuardando(false);
        }
    }

    return (
        <div className="page-stack">
            <PageHeader
                title="Beneficiarios"
                description={
                    "Alta, ediciÃ³n y listado con filtros por barrio y estado usando los endpoints reales del backend."
                }
            />

            {mensajeExito.length > 0 ? <StatusBanner tone="success" message={mensajeExito} /> : null}
            {errorVista.length > 0 ? <StatusBanner tone="error" message={errorVista} /> : null}

            <div className="content-grid content-grid--two-columns">
                <section className="info-card">
                    <div className="section-heading">
                        <h2>{tituloFormulario}</h2>
                        <p>En altas, el backend completa el estado con `ACTIVO` si no se envÃ­a valor.</p>
                    </div>

                    <form className="entity-form" onSubmit={(event) => void enviarFormulario(event)}>
                        <FormField label="Nombre" htmlFor="beneficiario-nombre" error={erroresFormulario.nombre}>
                            <input
                                id="beneficiario-nombre"
                                className="text-input"
                                value={formulario.nombre}
                                onChange={(event) => actualizarCampo("nombre", event.target.value)}
                            />
                        </FormField>

                        <FormField label="Correo" htmlFor="beneficiario-correo" error={erroresFormulario.correo}>
                            <input
                                id="beneficiario-correo"
                                type="email"
                                className="text-input"
                                value={formulario.correo}
                                onChange={(event) => actualizarCampo("correo", event.target.value)}
                            />
                        </FormField>

                        {beneficiarioEnEdicionId == null ? (
                            <FormField
                                label="Contraseña"
                                htmlFor="beneficiario-contrasena"
                                error={erroresFormulario.contrasena}
                            >
                                <input
                                    id="beneficiario-contrasena"
                                    type="password"
                                    className="text-input"
                                    value={formulario.contrasena}
                                    onChange={(event) => actualizarCampo("contrasena", event.target.value)}
                                    placeholder="Mínimo 8 caracteres"
                                />
                            </FormField>
                        ) : null}

                        <FormField
                            label="DirecciÃ³n"
                            htmlFor="beneficiario-direccion"
                            error={erroresFormulario.direccion}
                        >
                            <input
                                id="beneficiario-direccion"
                                className="text-input"
                                value={formulario.direccion}
                                onChange={(event) => actualizarCampo("direccion", event.target.value)}
                            />
                        </FormField>

                        <FormField
                            label="Fecha de nacimiento"
                            htmlFor="beneficiario-fecha"
                            error={erroresFormulario.fechaNacimiento}
                        >
                            <input
                                id="beneficiario-fecha"
                                type="date"
                                className="text-input"
                                value={formulario.fechaNacimiento}
                                onChange={(event) => actualizarCampo("fechaNacimiento", event.target.value)}
                            />
                        </FormField>

                        <div className="form-row">
                            <FormField label="Estado" htmlFor="beneficiario-estado" error={erroresFormulario.estado}>
                                <select
                                    id="beneficiario-estado"
                                    className="text-input"
                                    value={formulario.estado}
                                    onChange={(event) =>
                                        actualizarCampo("estado", event.target.value as EstadoBeneficiario)
                                    }
                                >
                                    {ESTADOS_BENEFICIARIO.map((estado) => (
                                        <option key={estado} value={estado}>
                                            {formatEnumLabel(estado)}
                                        </option>
                                    ))}
                                </select>
                            </FormField>

                            <FormField label="Barrio" htmlFor="beneficiario-barrio" error={erroresFormulario.barrio}>
                                <select
                                    id="beneficiario-barrio"
                                    className="text-input"
                                    value={formulario.barrio}
                                    onChange={(event) => actualizarCampo("barrio", event.target.value as Barrio)}
                                >
                                    {BARRIOS.map((barrio) => (
                                        <option key={barrio} value={barrio}>
                                            {formatEnumLabel(barrio)}
                                        </option>
                                    ))}
                                </select>
                            </FormField>
                        </div>

                        <div className="form-actions">
                            <button type="submit" className="button button--primary" disabled={guardando}>
                                {guardando
                                    ? "Guardando..."
                                    : beneficiarioEnEdicionId == null
                                      ? "Crear beneficiario"
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
                        <h2>Listado y filtros</h2>
                        <p>Los filtros consumen `/beneficiarios?zona=...&estado=...`.</p>
                    </div>

                    <div className="form-row">
                        <FormField label="Filtrar por barrio" htmlFor="filtro-zona">
                            <select
                                id="filtro-zona"
                                className="text-input"
                                value={filtroZona}
                                onChange={(event) => setFiltroZona(event.target.value)}
                            >
                                <option value="">Todos</option>
                                {BARRIOS.map((barrio) => (
                                    <option key={barrio} value={barrio}>
                                        {formatEnumLabel(barrio)}
                                    </option>
                                ))}
                            </select>
                        </FormField>

                        <FormField label="Filtrar por estado" htmlFor="filtro-estado">
                            <select
                                id="filtro-estado"
                                className="text-input"
                                value={filtroEstado}
                                onChange={(event) => setFiltroEstado(event.target.value)}
                            >
                                <option value="">Todos</option>
                                {ESTADOS_BENEFICIARIO.map((estado) => (
                                    <option key={estado} value={estado}>
                                        {formatEnumLabel(estado)}
                                    </option>
                                ))}
                            </select>
                        </FormField>
                    </div>

                    {cargando ? <StatusBanner tone="info" message="Cargando beneficiarios..." /> : null}

                    {!cargando && beneficiarios.length === 0 ? (
                        <EmptyState
                            title="No hay beneficiarios para este criterio"
                            description="Ajusta los filtros o crea un nuevo beneficiario."
                        />
                    ) : null}

                    {!cargando && beneficiarios.length > 0 ? (
                        <div className="table-wrapper">
                            <table className="data-table">
                                <thead>
                                    <tr>
                                        <th>Nombre</th>
                                        <th>Barrio</th>
                                        <th>Estado</th>
                                        <th>Nacimiento</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {beneficiarios.map((beneficiario) => (
                                        <tr key={beneficiario.id}>
                                            <td>
                                                <strong>{beneficiario.nombre}</strong>
                                                <div className="table-secondary-text">{beneficiario.correo}</div>
                                            </td>
                                            <td>{formatEnumLabel(beneficiario.barrio)}</td>
                                            <td>{formatEnumLabel(beneficiario.estado)}</td>
                                            <td>{beneficiario.fechaNacimiento}</td>
                                            <td>
                                                <button
                                                    type="button"
                                                    className="button button--ghost"
                                                    onClick={() => editarBeneficiario(beneficiario)}
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

function parseBarrioFilter(value: string): Barrio | undefined {
    if (BARRIOS.includes(value as Barrio)) {
        return value as Barrio;
    }

    return undefined;
}

function parseEstadoFilter(value: string): EstadoBeneficiario | undefined {
    if (ESTADOS_BENEFICIARIO.includes(value as EstadoBeneficiario)) {
        return value as EstadoBeneficiario;
    }

    return undefined;
}

function formatEnumLabel(value: string): string {
    return value
        .toLowerCase()
        .split("_")
        .map((segment) => segment.charAt(0).toUpperCase() + segment.slice(1))
        .join(" ");
}
