import type { ReactNode } from "react";
import { AppSwitcher } from "./AppSwitcher";
import type { AppId } from "./types";
import "./shell.css";
import "./themes.css";

type Props = {
    activeApp: AppId;
    onAppChange: (app: AppId) => void;
    version: string;
    children: ReactNode;
};

export function AppShell({ activeApp, onAppChange, version, children }: Props) {
    return (
        <div className={`faz-shell faz-shell--${activeApp}`}>
            <header className="faz-shell__meta">
                <AppSwitcher activeApp={activeApp} onAppChange={onAppChange} version={version} />
            </header>
            <main className="faz-shell__main">{children}</main>
        </div>
    )
}
