import type { SelectHTMLAttributes } from "react";

type Props = SelectHTMLAttributes<HTMLSelectElement>;

function Select({ className, ...props }: Props) {
	const classes = ["faz-select", className].filter(Boolean).join(" ");
	return <select className={classes} {...props} />;
}

export default Select;
