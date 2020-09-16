const config = require('../config');

 const createClaimReferences = require('../data/createClaimReferences');
 const createClaimCourt = require('../data/createClaimCourt');
 const createClaimClaimant = require('../data/createClaimClaimant');
 const createClaimDefendant = require('../data/createClaimDefendant');
 const createClaimType = require('../data/createClaimType');
 const createClaimSubtype = require('../data/createClaimSubtype');
 const createClaimParticularsOfClaimDocument = require('../data/createClaimParticularsOfClaimDocument');
 const createClaimMinMaxValue = require('../data/createClaimMinMaxValue');
 const createClaimStatementOfTruth = require('../data/createClaimStatementOfTruth');
 const createClaimAllData = require('../data/createClaimAllData');

 const confirmServiceServedDocuments = require('../data/confirmServiceServedDocuments');
 const confirmServiceUploadDocuments = require('../data/confirmServiceUploadDocuments');
 const confirmServiceMethod = require('../data/confirmServiceMethod');
 const confirmServiceLocation = require('../data/confirmServiceLocation');
 const confirmServiceDate = require('../data/confirmServiceDate');
 const confirmServiceStatementOfTruth = require('../data/confirmServiceStatementOfTruth');
 const confirmServiceAllData = require('../data/confirmServiceAllData');

Feature('Local Authority test');

BeforeSuite(async(I) => {
  // Claimant Solicitor - create claim and submit
  await I.setupTokensForUser(config.solicitorUser.email, config.solicitorUser.password);
  await I.createEvent('CREATE_CLAIM');
  await I.validateData('CREATE_CLAIMReferences', createClaimReferences);
  await I.validateData('CREATE_CLAIMCourt', createClaimCourt);
  await I.validateData('CREATE_CLAIMClaimant', createClaimClaimant);
  await I.validateData('CREATE_CLAIMDefendant', createClaimDefendant);
  await I.validateData('CREATE_CLAIMClaimType', createClaimType);
  await I.validateData('CREATE_CLAIMPersonalInjuryType', createClaimSubtype);
  await I.validateData('CREATE_CLAIMUpload', createClaimParticularsOfClaimDocument);
  await I.validateData('CREATE_CLAIMClaimValue', createClaimMinMaxValue);
  await I.validateData('CREATE_CLAIMStatementOfTruth', createClaimStatementOfTruth);
  await I.submitEvent(createClaimAllData);

  await I.createEvent('CONFIRM_SERVICE');
  await I.validateData('CONFIRM_SERVICEServedDocuments', confirmServiceServedDocuments);
  await I.validateData('CONFIRM_SERVICEUpload', confirmServiceUploadDocuments);
  await I.validateData('CONFIRM_SERVICEMethod', confirmServiceMethod);
  await I.validateData('CONFIRM_SERVICELocation', confirmServiceLocation);
  await I.validateData('CONFIRM_SERVICEDate', confirmServiceDate);
  await I.validateData('CONFIRM_SERVICEStatementOfTruth', confirmServiceStatementOfTruth);
  await I.submitEvent(confirmServiceAllData);
});

//Before(async I => await I.navigateToCaseDetails(caseId));
/* eslint no-unused-vars: ["error", { "args": "none" }] */
Scenario('testing', async (I) => {
  console.log('testing...');
});
