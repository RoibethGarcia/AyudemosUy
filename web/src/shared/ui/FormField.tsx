import type { ReactNode } from "react";

type FormFieldProps = Readonly<{
    label: string;
    htmlFor: string;
    error?: string;
    hint?: string;
    children: ReactNode;
}>;

export function FormField(props: FormFieldProps): JSX.Element {
    return (
        <label className="form-field" htmlFor={props.htmlFor}>
            <span className="form-field__label">{props.label}</span>
            {props.children}
            {typeof props.hint === "string" && props.hint.length > 0 ? (
                <span className="form-field__hint">{props.hint}</span>
            ) : null}
            {typeof props.error === "string" && props.error.length > 0 ? (
                <span className="form-field__error">{props.error}</span>
            ) : null}
        </label>
    );
}
