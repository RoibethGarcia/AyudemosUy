type PageHeaderProps = Readonly<{
    title: string;
    description: string;
}>;

export function PageHeader(props: PageHeaderProps): JSX.Element {
    return (
        <div className="page-header">
            <h1>{props.title}</h1>
            <p>{props.description}</p>
        </div>
    );
}
