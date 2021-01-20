/*global process*/

const defaultPassword = 'Password12!';

module.exports = {
  proxyServer: process.env.PROXY_SERVER,
  idamStub: {
    enabled: process.env.IDAM_STUB_ENABLED || false,
    url: 'http://host.docker.internal:5555'
  },
  url: {
    manageCase: process.env.URL || 'http://host.docker.internal:3333',
    authProviderApi: process.env.SERVICE_AUTH_PROVIDER_API_BASE_URL || 'http://host.docker.internal:4502',
    ccdDataStore: process.env.CCD_DATA_STORE_URL || 'http://host.docker.internal:4452',
    dmStore: process.env.DM_STORE_URL || 'http://dm-store:8080',
    idamApi: process.env.IDAM_API_URL || 'http://host.docker.internal:5000',
    unspecService: process.env.UNSPEC_SERVICE_URL || 'http://host.docker.internal:4000',
  },
  s2s: {
    microservice: 'unspec_service',
    secret: process.env.S2S_SECRET || 'AABBCCDDEEFFGGHH'
  },
  solicitorUser: {
    password: defaultPassword,
    email: 'claimantsolicitor@gmail.com'
  },
  definition: {
    jurisdiction: 'CIVIL',
    caseType: 'UNSPECIFIED_CLAIMS',
  }
};
