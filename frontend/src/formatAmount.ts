/** US-style amount: comma thousands separator, dot decimal, two fraction digits. */
export function formatUsdAmount(amount: number): string {
	return new Intl.NumberFormat("en-US", {
		minimumFractionDigits: 2,
		maximumFractionDigits: 2,
	}).format(amount);
}
