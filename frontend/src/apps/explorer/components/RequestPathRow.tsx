import { Button, HttpMethodBadge } from "../../../ui";
import type { HttpMethod } from "../../../ui";
import { buildApiPath, type ApiVersion } from "../../../api/paths";

type Props = {
	method: HttpMethod;
	resourcePath: string;
	pathSuffix?: string;
	versions: readonly ApiVersion[];
	activeVersion: ApiVersion;
	onVersionChange: (version: ApiVersion) => void;
};

export default function RequestPathRow({
	method,
	resourcePath,
	pathSuffix,
	versions,
	activeVersion,
	onVersionChange,
}: Props) {
	const displayPath = `${buildApiPath(activeVersion, resourcePath)}${pathSuffix ?? ""}`;

	return (
		<div className="explorer__request-line">
			<div className="explorer__request-line-main">
				<HttpMethodBadge method={method} />
				<code className="explorer__path">{displayPath}</code>
			</div>
			<div
				className="explorer__version-bar"
				role="group"
				aria-label="API version"
			>
				{versions.map(version => (
					<Button
						key={version}
						type="button"
						className={
							version === activeVersion
								? "faz-btn--active explorer__version-btn"
								: "explorer__version-btn"
						}
						onClick={() => onVersionChange(version)}
						aria-pressed={version === activeVersion}
					>
						{version}
					</Button>
				))}
			</div>
		</div>
	);
}
