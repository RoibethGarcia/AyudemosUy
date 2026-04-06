import { InfoCard } from "../../shared/ui/InfoCard";
import { PageHeader } from "../../shared/ui/PageHeader";

export function DistribucionesPage(): JSX.Element {
    return (
        <div className="page-stack">
            <PageHeader
                title="Distribuciones"
                description="Base para crear distribuciones, consultar su detalle y filtrar por estado o zona."
            />
            <InfoCard title="Siguientes integraciones recomendadas">
                <ul>
                    <li>Listado con filtros por estado y zona.</li>
                    <li>Formulario de alta con selecciÃ³n de donaciones y beneficiario.</li>
                    <li>EdiciÃ³n de estado y fechas del flujo logÃ­stico.</li>
                </ul>
            </InfoCard>
        </div>
    );
}
