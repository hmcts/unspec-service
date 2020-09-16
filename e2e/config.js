const defaultPassword = 'Password12';

module.exports = {
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
  testFile: './e2e/fixtures/examplePDF.pdf',
  idam: {
    grant_type: 'password',
    redirect_url: 'https://fpl-case-service-aat.service.core-compute-aat.internal/oauth2/callback',
    scope: 'openid profile roles',
    client_id: 'unspec-service',
    client_secret: 'p"2sHCgu4R5;~,;=',
  },
  rpe: {service_id: 'unspec_service'},
};
