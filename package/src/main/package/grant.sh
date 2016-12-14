#!/bin/bash
# ======================================================================
# NAMI Grant Tools
# ======================================================================

FONTCOLOR="\033[33m"
END="\033[0m"

CURRENT_DIR="$(cd "$(dirname "$0")" && pwd -P)"

. ${CURRENT_DIR}/java/set_env.sh

echo -ne             "*********************************************************\n"
echo -ne "${FONTCOLOR}****************** NAMI Server Management ***************${END}\n"
echo -ne "${FONTCOLOR}********************* NAMI Grant Tools ******************${END}\n"
echo -ne             "*********************************************************\n"
echo

chmod +x ${CURRENT_DIR}/*.sh
chmod +x ${CURRENT_DIR}/**/**/bin/*
chmod +x ${CURRENT_DIR}/**/**/**/bin/*

echo -ne             "*********************************************************\n"
echo -ne "${FONTCOLOR}************************** Finished *********************${END}\n"
