type EmptyStateProps = Readonly<{
    title: string;
    description: string;
}>;

export function EmptyState(props: EmptyStateProps): JSX.Element {
    return (
        <div className="empty-state">
            <strong>{props.title}</strong>
            <p>{props.description}</p>
        </div>
    );
}
