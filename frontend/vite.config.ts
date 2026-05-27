import { readFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import babel from '@rolldown/plugin-babel'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

const __dirname = dirname(fileURLToPath(import.meta.url))
const pkg = JSON.parse(
	readFileSync(join(__dirname, 'package.json'), 'utf-8')
) as { version: string }

// https://vite.dev/config/
export default defineConfig({
	define: {
		__FAZ_VERSION__: JSON.stringify(pkg.version),
	},
	plugins: [
		react(),
		babel({ presets: [reactCompilerPreset()] })
	],
	server: {
		proxy: {
			"/api": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
		},
	},
})
