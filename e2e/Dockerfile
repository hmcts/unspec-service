FROM hmctspublic.azurecr.io/base/node:12-alpine

USER root

COPY ./ /opt/app/

RUN apk add --no-cache udev ttf-freefont chromium git

ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD true
ENV PUPPETEER_EXECUTABLE_PATH /usr/bin/chromium-browser

RUN yarn install --frozen-lockfile

ENTRYPOINT [ "yarn" ]
