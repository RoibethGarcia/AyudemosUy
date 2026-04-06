import { NavLink } from "react-router-dom";

const NAV_ITEMS = [
    { to: "/", label: "Inicio", end: true },
    { to: "/iniciar-sesion", label: "Iniciar sesión" },
    { to: "/usuarios", label: "Usuarios" },
    { to: "/beneficiarios", label: "Beneficiarios" },
    { to: "/donaciones", label: "Donaciones" },
    { to: "/distribuciones", label: "Distribuciones" },
    { to: "/repartidores", label: "Repartidores" },
    { to: "/reportes", label: "Reportes" },
];

export function AppNav(): JSX.Element {
    return (
        <header className="app-header">
            <div className="brand-block">
                <span className="brand-title">Ayudemos.uy</span>
                <span className="brand-subtitle">Gestión de donaciones</span>
            </div>
            <nav className="nav-list" aria-label="Principal">
                {NAV_ITEMS.map((item) => (
                    <NavLink
                        key={item.to}
                        to={item.to}
                        end={item.end}
                        className={({ isActive }) =>
                            isActive ? "nav-link nav-link--active" : "nav-link"
                        }
                    >
                        {item.label}
                    </NavLink>
                ))}
            </nav>
        </header>
    );
}
