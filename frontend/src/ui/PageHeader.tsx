type Props = {
	title: string;
	subtitle?: string;
	badge?: string;
};

function PageHeader({ title, subtitle, badge }: Props) {
	return (
		<header className="faz-page-header">
			<div className="faz-page-header__row">
				<h1>{title}</h1>
				{badge && <span className="faz-page-header__badge">{badge}</span>}
			</div>
			{subtitle && <p>{subtitle}</p>}
		</header>
	);
}

export default PageHeader;
