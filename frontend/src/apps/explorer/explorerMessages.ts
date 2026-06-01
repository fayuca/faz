export const ExplorerMessages = {
	validationSummary: "Some fields need attention.",

	jsonInvalid: "The request body is not valid JSON.",
	requestFailed: "The request could not be completed. Please try again.",

	paramRequired: (name: string) => `${name} is required.`,
	paramInvalid: (name: string) => `Enter a valid ${name}.`,
} as const;
