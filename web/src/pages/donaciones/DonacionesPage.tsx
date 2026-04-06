import { InfoCard } from "../../shared/ui/InfoCard";
import { PageHeader } from "../../shared/ui/PageHeader";

export function DonacionesPage(): JSX.Element {
    return (
        <div className="page-stack">
            <PageHeader
                title="Donaciones"
                description="Pantalla base para registrar y actualizar donaciones respetando el subtipo existente."
            />
            <InfoCard title="Reglas que la UI debe respetar">
                <ul>
                    <li>El alta distingue alimento y artÃ­culo.</li>
                    <li>La ediciÃ³n no debe intentar convertir la donaciÃ³n entre subtipos.</li>
                </ul>
            </InfoCard>
        </div>
    );
}
