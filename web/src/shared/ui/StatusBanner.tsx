type StatusBannerProps = Readonly<{
    tone: "success" | "error" | "info";
    message: string;
}>;

export function StatusBanner(props: StatusBannerProps): JSX.Element {
    return <div className={`status-banner status-banner--${props.tone}`}>{props.message}</div>;
}
