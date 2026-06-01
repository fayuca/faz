import type { TextareaHTMLAttributes } from "react";

type Props = TextareaHTMLAttributes<HTMLTextAreaElement>;

function TextArea({ className, ...props }: Props) {
	const classes = ["faz-textarea", className].filter(Boolean).join(" ");
	return <textarea className={classes} {...props} />;
}

export default TextArea;
