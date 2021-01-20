FROM hmctspublic.azurecr.io/base/node:12-alpine

USER root

COPY ./ /opt/app/

RUN apk add --no-cache  chromium --repository=http://dl-cdn.alpinelinux.org/alpine/v3.10/main

RUN yarn install

ENTRYPOINT [ "yarn" ]
