#!/usr/bin/env bash

# script dir and sudo 
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

OUTPUT=$( id 2>/dev/null | grep uid=0 )
if [ -z "$OUTPUT" ]; then
    echo Use sudo root rights to run the script
    exit 1
fi

# script
set -euo pipefail

systemctl stop soqol.service || true
systemctl disable soqol.service || true

rm -f /lib/systemd/system/soqol.service
systemctl --system daemon-reload

if getent passwd soqol >/dev/null; then
deluser soqol --system
fi

if getent group soqol >/dev/null; then
delgroup soqol --system --only-if-empty
fi

if [ -n "$SCRIPT_DIR" -a "$SCRIPT_DIR" != "/" ]; then
rm -rf $SCRIPT_DIR
fi
