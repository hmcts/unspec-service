FROM hmctspublic.azurecr.io/base/node:14-buster-slim-262ae7342a91df1908d8cbd2b3e7107716300edc

COPY ./ /opt/app/
RUN yarn install

ENTRYPOINT [ "yarn" ]
