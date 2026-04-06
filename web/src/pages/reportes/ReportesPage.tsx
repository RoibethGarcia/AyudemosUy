import { InfoCard } from "../../shared/ui/InfoCard";
import { PageHeader } from "../../shared/ui/PageHeader";

export function ReportesPage(): JSX.Element {
    return (
        <div className="page-stack">
            <PageHeader
                title="Reportes"
                description="Base para consumir el ranking de zonas con mayor cantidad de distribuciones."
            />
            <InfoCard title="Reporte disponible">
                <ul>
                    <li>GET /reportes/zonas-mayor-distribuciones</li>
                </ul>
            </InfoCard>
        </div>
    );
}
