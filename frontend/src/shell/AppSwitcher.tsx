import { Select } from "../ui";
import { type AppId, APPS } from "./types.ts";

type Props = {
    activeApp: AppId;
    onAppChange: (app: AppId) => void;
    version: string;
};

export function AppSwitcher({ activeApp, onAppChange, version }: Props) {
    return (
        <label htmlFor="app-switcher">
            <span>App</span>
            <Select id="app-switcher" value={activeApp} onChange={(e) => onAppChange(e.target.value as AppId)}>
                {APPS.map((app) => (
                    <option key={app.id} value={app.id}>{app.label}</option>
                ))}
            </Select>
            <span>v{version}</span>
        </label>
    )
}
