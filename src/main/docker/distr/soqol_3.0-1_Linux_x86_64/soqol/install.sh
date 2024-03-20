#!/usr/bin/env bash

# script dir and sudo 
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

DISTR_ROOT=$SCRIPT_DIR

OUTPUT=$( id 2>/dev/null | grep uid=0 )
if [ -z "$OUTPUT" ]; then
    echo Use sudo root rights to run the script
    exit 1
fi

# script
set -euo pipefail

if ! getent group soqol >/dev/null; then
    addgroup soqol --system>/dev/null
fi
if ! getent passwd soqol >/dev/null; then
    adduser soqol --system --ingroup soqol --disabled-login --no-create-home --home /var/lib/soqol --shell /bin/false --gecos "LINTER-SOQOL Server"
fi

install -d -m 0750 -o soqol -g soqol /var/lib/soqol

install -p -m 0644 -o soqol -g soqol -t /var/lib/soqol $DISTR_ROOT/soqol_config.yml

install -d -m 0755 -o root -g root /opt/soqol
install -d -m 0755 -o root -g root /opt/soqol/bin
install -d -m 0755 -o root -g root /opt/soqol/doc

install -p -m 0755 -o root -g root -t /opt/soqol/bin $DISTR_ROOT/bin/vsql_*
install -p -m 0755 -o root -g root -t /opt/soqol/bin $DISTR_ROOT/bin/*.so
install -p -m 0644 -o root -g root -t /opt/soqol/bin $DISTR_ROOT/bin/*.jar
install -p -m 0644 -o root -g root -t /opt/soqol/doc $DISTR_ROOT/doc/*
install -p -m 0644 -o root -g root -t /opt/soqol/ $DISTR_ROOT/license.txt
install -p -m 0644 -o root -g root -t /opt/soqol/ $DISTR_ROOT/readme.txt
install -p -m 0755 -o root -g root -t /opt/soqol/ $DISTR_ROOT/install/uninstall.sh

if [ "--no-service" = "$1" ]; then
    exit 0
fi

install -p -m 0644 -o root -g root -t /lib/systemd/system $DISTR_ROOT/install/systemd/soqol.service
systemctl --system daemon-reload
systemctl enable soqol.service
systemctl start soqol.service
