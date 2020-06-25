#!/usr/bin/env bash

# User used during the CCD import and ccd-role creation
./civil-unspecified-docker/bin/utils/ccd-add-role.sh "caseworker-cmc"

roles=("solicitor")
for role in "${roles[@]}"
do
  ./civil-unspecified-docker/bin/utils/ccd-add-role.sh "caseworker-cmc-${role}"
done

USER_EMAIL=solicitor@example.com
FORENAME=Example
SURNAME=Solicitor
PASSWORD=Password12
USER_ROLES='[{"code":"caseworker"},{"code":"caseworker-cmc-solicitor"},{"code":"caseworker-cmc"}]'


curl -XPOST -H 'Content-Type: application/json' "${IDAM_API_BASE_URL:-http://localhost:5000}"/testing-support/accounts -d '{
    "email": "'${USER_EMAIL}'",
    "forename": "'${FORENAME}'",
    "surname": "'${SURNAME}'",
    "roles": '${USER_ROLES}',
    "password": "'${PASSWORD}'"
}'

echo -e "\nCreated user with:\nUsername: ${USER_EMAIL}\nPassword: ${PASSWORD}\nFirstname: ${FORENAME}\nSurname: ${SURNAME}\nRoles: ${USER_ROLES}"
