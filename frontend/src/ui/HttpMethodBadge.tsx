export type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

type Props = {
	method: HttpMethod;
};

const VARIANT: Record<HttpMethod, string> = {
	GET: "faz-badge--get",
	POST: "faz-badge--post",
	PUT: "faz-badge--put",
	DELETE: "faz-badge--delete",
};

function HttpMethodBadge({ method }: Props) {
	return (
		<span className={`faz-badge ${VARIANT[method]}`}>{method}</span>
	);
}

export default HttpMethodBadge;
