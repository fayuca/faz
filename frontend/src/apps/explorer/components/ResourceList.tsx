import { Button, Panel } from "../../../ui";
import type { ApiResource } from "../../../api/manifest";

type Props = {
	resources: ApiResource[];
	activeId: string;
	onSelect: (id: string) => void;
};

export default function ResourceList({ resources, activeId, onSelect }: Props) {
	return (
		<Panel title="Resources">
			<ul className="explorer__resource-list">
				{resources.map(resource => (
					<li key={resource.id}>
						<Button
							className={
								resource.id === activeId ? "faz-btn--active" : undefined
							}
							onClick={() => onSelect(resource.id)}
						>
							{resource.label}
						</Button>
					</li>
				))}
			</ul>
		</Panel>
	);
}
