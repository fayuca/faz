export type AppId = "planner" | "explorer";

export const APPS: { id: AppId; label: string }[] = [
    { id: "planner", label: "Budget planner" },
    { id: "explorer", label: "API explorer" },
];
