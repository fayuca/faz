import type { ReactNode } from "react";

type Props = {
	title: ReactNode;
	children: ReactNode;
};

function Panel({ title, children }: Props) {
	return (
		<section className="faz-panel">
			<header className="faz-panel__header">{title}</header>
			<div className="faz-panel__body">{children}</div>
		</section>
	);
}

export default Panel;
