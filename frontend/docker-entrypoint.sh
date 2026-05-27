#!/bin/sh
set -eu

export PORT="${PORT:-80}"
export BACKEND_PROXY_TARGET="${BACKEND_PROXY_TARGET:-http://backend:8080}"

envsubst '${PORT} ${BACKEND_PROXY_TARGET}' \
	< /etc/nginx/templates/default.conf.template \
	> /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
