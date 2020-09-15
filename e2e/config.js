/*global process*/

const defaultPassword = 'Password12';

module.exports = {
  proxyServer: process.env.PROXY_SERVER,
  ccdDataStoreUrl: process.env.CCD_DATA_STORE_URL || 'http://localhost:4452',
  s2s: {
    microservice: 'unspec_service',
    secret: process.env.S2S_SECRET || 'AABBCCDDEEFFGGHH',
    authProviderUrl: process.env.SERVICE_AUTH_PROVIDER_API_BASE_URL || 'http://localhost:4502',
  },
  solicitorUser: {
    password: defaultPassword,
    email: 'solicitor@example.com'
  },
  definition: {
    jurisdiction: 'CIVIL',
    caseType: 'UNSPECIFIED_CLAIMS',
  },
  address: {
    buildingAndStreet: {
      lineOne: 'Flat 2',
      lineTwo: 'Caversham House 15-17',
      lineThree: 'Church Road',
    },
    town: 'Reading',
    county: 'Kent',
    country: 'United Kingdom',
    postcode: 'RG4 7AA',
  },
  testFile: './e2e/fixtures/examplePDF.pdf'
};
