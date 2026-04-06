import { createBrowserRouter } from "react-router-dom";

import { LoginPage } from "./pages/acceso/LoginPage";
import { HomePage } from "./pages/HomePage";
import { BeneficiariosPage } from "./pages/beneficiarios/BeneficiariosPage";
import { DistribucionesPage } from "./pages/distribuciones/DistribucionesPage";
import { DonacionesPage } from "./pages/donaciones/DonacionesPage";
import { RepartidoresPage } from "./pages/repartidores/RepartidoresPage";
import { ReportesPage } from "./pages/reportes/ReportesPage";
import { UsuariosPage } from "./pages/usuarios/UsuariosPage";
import { AppLayout } from "./shared/layout/AppLayout";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <AppLayout />,
        children: [
            {
                index: true,
                element: <HomePage />,
            },
            {
                path: "iniciar-sesion",
                element: <LoginPage />,
            },
            {
                path: "usuarios",
                element: <UsuariosPage />,
            },
            {
                path: "beneficiarios",
                element: <BeneficiariosPage />,
            },
            {
                path: "donaciones",
                element: <DonacionesPage />,
            },
            {
                path: "distribuciones",
                element: <DistribucionesPage />,
            },
            {
                path: "repartidores",
                element: <RepartidoresPage />,
            },
            {
                path: "reportes",
                element: <ReportesPage />,
            },
        ],
    },
]);
