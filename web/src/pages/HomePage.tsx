import { Link } from "react-router-dom";

import { InfoCard } from "../shared/ui/InfoCard";

const FEATURE_ITEMS = [
    {
        title: "Ingreso ordenado al sistema",
        description: "La landing centraliza el acceso inicial para operadores, voluntarios y administradores.",
    },
    {
        title: "Alta de usuarios disponible",
        description: "El flujo de registro ya redirige al módulo real de usuarios conectado con la API.",
    },
    {
        title: "Escalable para próximos módulos",
        description: "Desde aquí podemos seguir sumando autenticación real, reportes y métricas del dominio.",
    },
];

const QUICK_ACCESS_ITEMS = [
    { to: "/usuarios", label: "Crear o administrar usuarios" },
    { to: "/beneficiarios", label: "Consultar beneficiarios" },
    { to: "/donaciones", label: "Registrar donaciones" },
    { to: "/distribuciones", label: "Planificar distribuciones" },
];

export function HomePage(): JSX.Element {
    return (
        <div className="page-stack">
            <section className="landing-hero">
                <div className="landing-hero__content">
                    <span className="landing-hero__eyebrow">Bienvenido a Ayudemos.uy</span>
                    <h1>Gestiona ayuda real con una entrada clara, simple y lista para crecer.</h1>
                    <p>
                        Centralizamos el acceso al sistema para que un operador pueda iniciar su jornada, crear un
                        usuario nuevo y navegar rápidamente hacia los módulos clave de la plataforma.
                    </p>

                    <div className="landing-hero__actions">
                        <Link to="/iniciar-sesion" className="button button--primary">
                            Iniciar sesión
                        </Link>
                        <Link to="/usuarios" className="button button--secondary">
                            Crear usuario
                        </Link>
                    </div>
                </div>

                <div className="landing-highlight">
                    <h2>Qué resuelve esta portada</h2>
                    <ul className="landing-highlight__list">
                        <li>Da un mensaje de bienvenida entendible desde el primer segundo.</li>
                        <li>Expone accesos concretos en vez de obligar al usuario a adivinar.</li>
                        <li>Prepara el terreno para conectar autenticación real cuando exista el backend.</li>
                    </ul>
                </div>
            </section>

            <section className="card-grid">
                {FEATURE_ITEMS.map((item) => (
                    <InfoCard key={item.title} title={item.title}>
                        <p>{item.description}</p>
                    </InfoCard>
                ))}
            </section>

            <section className="card-grid landing-secondary-grid">
                <InfoCard title="Accesos rápidos">
                    <ul className="link-list">
                        {QUICK_ACCESS_ITEMS.map((item) => (
                            <li key={item.to}>
                                <Link to={item.to}>{item.label}</Link>
                            </li>
                        ))}
                    </ul>
                </InfoCard>

                <InfoCard title="Estado actual del front">
                    <p>
                        El alta y edición de usuarios ya funciona sobre la API real. El inicio de sesión queda
                        preparado desde la UI, pero todavía necesita un endpoint de autenticación en backend para ser
                        real.
                    </p>
                </InfoCard>
            </section>
        </div>
    );
}
