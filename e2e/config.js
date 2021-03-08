/*global process*/

const defaultPassword = 'Password12!';

module.exports = {
  proxyServer: 'proxyout.reform.hmcts.net:8080',
  idamStub: {
    enabled: process.env.IDAM_STUB_ENABLED || false,
    url: 'http://localhost:5555'
  },
  url: {
    manageCase: 'https://manage-case.aat.platform.hmcts.net',
    authProviderApi: 'http://rpe-service-auth-provider-aat.service.core-compute-aat.internal',
    ccdDataStore: 'http://ccd-data-store-api-aat.service.core-compute-aat.internal',
    dmStore: 'http://dm-store-aat.service.core-compute-aat.internal',
    idamApi: 'https://idam-api.aat.platform.hmcts.net',
    unspecService: 'http://unspec-service-aat.service.core-compute-aat.internal',
  },
  s2s: {
    microservice: 'unspec_service',
    secret: process.env.S2S_SECRET || 'AABBCCDDEEFFGGHH'
  },
  solicitorUser: {
    password: defaultPassword,
    email: 'civil.damages.claims+organisation.1.solicitor.1@gmail.com'
  },
  defendantSolicitorUser: {
    password: defaultPassword,
    email: 'civil.damages.claims+organisation.2.solicitor.1@gmail.com'
  },
  definition: {
    jurisdiction: 'CIVIL',
    caseType: 'UNSPECIFIED_CLAIMS',
  }
};
