import type { ButtonHTMLAttributes } from "react";

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
	variant?: "default" | "primary" | "danger";
};

function Button({ variant = "default", className, type = "button", ...props }: Props) {
	const classes = [
		"faz-btn",
		variant === "primary" && "faz-btn--primary",
		variant === "danger" && "faz-btn--danger",
		className,
	]
		.filter(Boolean)
		.join(" ");

	return <button type={type} className={classes} {...props} />;
}

export default Button;
