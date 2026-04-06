import type { ReactNode } from "react";

type InfoCardProps = Readonly<{
    title: string;
    children: ReactNode;
}>;

export function InfoCard(props: InfoCardProps): JSX.Element {
    return (
        <section className="info-card">
            <h2>{props.title}</h2>
            <div>{props.children}</div>
        </section>
    );
}
