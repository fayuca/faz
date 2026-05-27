#!/bin/sh
set -eu

export PORT="${PORT:-80}"
export BACKEND_HOSTPORT="${BACKEND_HOSTPORT:-backend:8080}"

envsubst '${PORT} ${BACKEND_HOSTPORT}' \
	< /etc/nginx/templates/default.conf.template \
	> /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
