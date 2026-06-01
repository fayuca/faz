import type { InputHTMLAttributes } from "react";

type Props = InputHTMLAttributes<HTMLInputElement>;

function TextInput({ className, ...props }: Props) {
	const classes = ["faz-input", className].filter(Boolean).join(" ");
	return <input className={classes} {...props} />;
}

export default TextInput;
