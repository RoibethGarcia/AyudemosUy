import { Outlet } from "react-router-dom";

import { AppNav } from "../ui/AppNav";

export function AppLayout(): JSX.Element {
    return (
        <div className="app-shell">
            <AppNav />
            <main className="app-content">
                <Outlet />
            </main>
        </div>
    );
}
