#!/usr/bin/env bash

definition_input_dir=$(realpath 'ccd-definition')
definition_output_file="$(realpath ".")/build/ccd-development-config/ccd-unspec-dev.xlsx"
params="$@"

echo "${CCD_CONFIGURER_IMPORTER_USERNAME}"
echo "${CCD_CONFIGURER_IMPORTER_PASSWORD}"
./civil-unspecified-docker/bin/import-ccd-definition.sh "${definition_input_dir}" "${definition_output_file}" "${params}"
