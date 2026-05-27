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
            <select id="app-switcher" value={activeApp} onChange={(e) => onAppChange(e.target.value as AppId)}>
                {APPS.map((app) => (
                    <option key={app.id} value={app.id}>{app.label}</option>
                ))}
            </select>
            <span>v{version}</span>
        </label>
    )
}
