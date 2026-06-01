import { Button, HttpMethodBadge } from "../../../ui";
import type { HttpMethod } from "../../../ui";

type Props = {
	verbs: HttpMethod[];
	activeVerb: HttpMethod;
	onSelect: (verb: HttpMethod) => void;
};

export default function VerbBar({ verbs, activeVerb, onSelect }: Props) {
	return (
		<div className="explorer__verb-bar" role="tablist" aria-label="HTTP verbs">
			{verbs.map(verb => (
				<Button
					key={verb}
					className={
						verb === activeVerb ? "faz-btn--active explorer__verb" : "explorer__verb"
					}
					onClick={() => onSelect(verb)}
					role="tab"
					aria-selected={verb === activeVerb}
				>
					<HttpMethodBadge method={verb} />
				</Button>
			))}
		</div>
	);
}
