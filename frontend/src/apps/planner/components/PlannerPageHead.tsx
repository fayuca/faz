import PlannerNav from "./PlannerNav";

type Props = {
	title: string;
	subtitle?: string;
};

export default function PlannerPageHead({ title, subtitle }: Props) {
	return (
		<header className="planner__page-head">
			<div className="planner__page-head-text">
				<h1>{title}</h1>
				{subtitle && <p>{subtitle}</p>}
			</div>
			<PlannerNav />
		</header>
	);
}
