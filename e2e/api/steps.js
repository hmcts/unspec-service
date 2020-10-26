const assert = require('assert').strict;

const request = require('./request.js');
const testingSupport = require('./testingSupport.js');

const data = {
 createClaim: require('../fixtures/events/createClaim.js'),
 confirmService: require('../fixtures/events/confirmService.js'),
 acknowledgeService: require('../fixtures/events/acknowledgeService.js'),
 requestExtension: require('../fixtures/events/requestExtension.js'),
 respondExtension: require('../fixtures/events/respondExtension.js'),
 defendantResponse: require('../fixtures/events/defendantResponse.js'),
 claimantResponse: require('../fixtures/events/claimantResponse.js'),
};

let caseId;
let caseData = {};

module.exports = {
  createClaim: async (user) => {
    await request.setupTokens(user);
    await request.startEvent('CREATE_CLAIM');

    await assertValidData('CREATE_CLAIM', 'References', data.createClaim.valid.References);
    await assertValidData('CREATE_CLAIM', 'Court', data.createClaim.valid.Court);
    await assertValidData('CREATE_CLAIM', 'Claimant', data.createClaim.valid.Claimant);
    await assertValidData('CREATE_CLAIM', 'ClaimantLitigationFriend', data.createClaim.valid.ClaimantLitigationFriend);
    await assertValidData('CREATE_CLAIM', 'Defendant', data.createClaim.valid.Defendant);
    await assertValidData('CREATE_CLAIM', 'ClaimType', data.createClaim.valid.ClaimType);
    await assertValidData('CREATE_CLAIM', 'PersonalInjuryType', data.createClaim.valid.PersonalInjuryType);
    await assertValidData('CREATE_CLAIM', 'Upload', data.createClaim.valid.Upload);
    await assertValidData('CREATE_CLAIM', 'ClaimValue', data.createClaim.valid.ClaimValue);
    await assertValidData('CREATE_CLAIM', 'StatementOfTruth', data.createClaim.valid.StatementOfTruth);

    await assertSubmittedEvent('CREATE_CLAIM', 'CREATED', {
      header: 'Your claim has been issued',
      body: 'Follow these steps to serve a claim'
    });
  },

  confirmService: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('CONFIRM_SERVICE', caseId);

    delete caseData.servedDocumentFiles;
    await assertValidData('CONFIRM_SERVICE', 'ServedDocuments', data.confirmService.valid.ServedDocuments);
    await assertValidData('CONFIRM_SERVICE', 'Upload', data.confirmService.valid.Upload);
    await assertValidData('CONFIRM_SERVICE', 'Method', data.confirmService.valid.Method);
    await assertValidData('CONFIRM_SERVICE', 'Location', data.confirmService.valid.Location);
    await assertCallbackError('CONFIRM_SERVICE', 'Date', data.confirmService.invalid.Date.yesterday,
      'The date must not be before issue date of claim');
    await assertCallbackError('CONFIRM_SERVICE', 'Date', data.confirmService.invalid.Date.tomorrow,
      'The date must not be in the future');
    await assertValidData('CONFIRM_SERVICE', 'Date', data.confirmService.valid.Date);
    await assertValidData('CONFIRM_SERVICE', 'StatementOfTruth', data.confirmService.valid.StatementOfTruth);

    await assertSubmittedEvent('CONFIRM_SERVICE', 'CREATED', {
      header: 'You\'ve confirmed service',
      body: 'Deemed date of service'
    });
  },

  acknowledgeService: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('ACKNOWLEDGE_SERVICE', caseId);

    await assertValidData('ACKNOWLEDGE_SERVICE', 'ConfirmNameAddress', data.acknowledgeService.valid.ConfirmNameAddress);
    await assertValidData('ACKNOWLEDGE_SERVICE', 'ConfirmDetails', data.acknowledgeService.valid.ConfirmDetails);
    await assertValidData('ACKNOWLEDGE_SERVICE', 'ResponseIntention', data.acknowledgeService.valid.ResponseIntention);

    await assertSubmittedEvent('ACKNOWLEDGE_SERVICE', 'CREATED', {});
  },

  requestExtension: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('REQUEST_EXTENSION', caseId);

    await assertValidData('REQUEST_EXTENSION', 'ProposeDeadline', data.requestExtension.valid.ProposeDeadline);
    await assertValidData('REQUEST_EXTENSION', 'ExtensionAlreadyAgreed', data.requestExtension.valid.ExtensionAlreadyAgreed);

    await assertSubmittedEvent('REQUEST_EXTENSION', 'CREATED', {});
  },

  respondExtension: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('RESPOND_EXTENSION', caseId);

    await assertValidData('RESPOND_EXTENSION', 'Respond', data.respondExtension.valid.Respond);
    await assertValidData('RESPOND_EXTENSION', 'Counter', data.respondExtension.valid.Counter);
    await assertValidData('RESPOND_EXTENSION', 'Reason', data.respondExtension.valid.Reason);

    await assertSubmittedEvent('RESPOND_EXTENSION', 'CREATED', {});
  },

  defendantResponse: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('DEFENDANT_RESPONSE', caseId);

    //TODO: check if ccd api allows validating hidden pages
    await assertValidData('DEFENDANT_RESPONSE', 'RespondentResponseType', data.defendantResponse.valid.RespondentResponseType);
    await assertValidData('DEFENDANT_RESPONSE', 'Upload', data.defendantResponse.valid.Upload);
    await assertValidData('DEFENDANT_RESPONSE', 'ConfirmNameAddress', data.defendantResponse.valid.ConfirmNameAddress);
    await assertValidData('DEFENDANT_RESPONSE', 'ConfirmDetails', data.defendantResponse.valid.ConfirmDetails);
    await assertValidData('DEFENDANT_RESPONSE', 'FileDirectionsQuestionnaire', data.defendantResponse.valid.FileDirectionsQuestionnaire);
    await assertValidData('DEFENDANT_RESPONSE', 'DisclosureOfElectronicDocuments', data.defendantResponse.valid.DisclosureOfElectronicDocuments);
    await assertValidData('DEFENDANT_RESPONSE', 'DisclosureOfNonElectronicDocuments', data.defendantResponse.valid.DisclosureOfNonElectronicDocuments);
    await assertValidData('DEFENDANT_RESPONSE', 'DisclosureReport', data.defendantResponse.valid.DisclosureReport);
    await assertValidData('DEFENDANT_RESPONSE', 'Experts', data.defendantResponse.valid.Experts);
    await assertValidData('DEFENDANT_RESPONSE', 'Witnesses', data.defendantResponse.valid.Witnesses);
    await assertValidData('DEFENDANT_RESPONSE', 'Hearing', data.defendantResponse.valid.Hearing);
    await assertValidData('DEFENDANT_RESPONSE', 'DraftDirections', data.defendantResponse.valid.DraftDirections);
    await assertValidData('DEFENDANT_RESPONSE', 'RequestedCourt', data.defendantResponse.valid.RequestedCourt);
    await assertValidData('DEFENDANT_RESPONSE', 'HearingSupport', data.defendantResponse.valid.HearingSupport);
    await assertValidData('DEFENDANT_RESPONSE', 'FurtherInformation', data.defendantResponse.valid.FurtherInformation);
    await assertValidData('DEFENDANT_RESPONSE', 'StatementOfTruth', data.defendantResponse.valid.StatementOfTruth);

    await assertSubmittedEvent('DEFENDANT_RESPONSE', 'AWAITING_CLAIMANT_INTENTION', {});
  },

  claimantResponse: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('CLAIMANT_RESPONSE', caseId);

    await assertValidData('CLAIMANT_RESPONSE', 'RespondentResponse', data.claimantResponse.valid.RespondentResponse);
    await assertValidData('CLAIMANT_RESPONSE', 'DefenceResponseDocument', data.claimantResponse.valid.DefenceResponseDocument);

    await assertSubmittedEvent('AWAITING_CLAIMANT_INTENTION', 'CREATED', {});
  }
};

const assertValidData = async (eventName, pageId, eventData, expectedDataSetByCallback = {}) => {
  caseData = Object.assign(caseData, eventData);
  const response = await request.validatePage(eventName, pageId, caseData);
  const responseBody = await response.json();
  caseData = Object.assign(caseData, expectedDataSetByCallback);

  assert.equal(response.status, 200);
  assert.deepEqual(responseBody.data, caseData);
};

const assertCallbackError = async (eventName, pageId, eventData, expectedErrorMessage) => {
  caseData = Object.assign(caseData, eventData);
  const response = await request.validatePage(eventName, pageId, caseData);
  const responseBody = await response.json();

  assert.equal(response.status, 422);
  assert.equal(responseBody.message, 'Unable to proceed because there are one or more callback Errors or Warnings');
  assert.equal(responseBody.callbackErrors[0], expectedErrorMessage);
};

const assertSubmittedEvent = async (eventName, expectedState, submittedCallbackResponse) => {
  const response = await request.submitEvent(eventName, caseData, caseId);
  const responseBody = await response.json();

  assert.equal(response.status, 201);
  assert.equal(responseBody.state, expectedState);
  assert.equal(responseBody.callback_response_status_code, 200);
  assert.equal(responseBody.after_submit_callback_response.confirmation_header.includes(submittedCallbackResponse.header), true);
  assert.equal(responseBody.after_submit_callback_response.confirmation_body.includes(submittedCallbackResponse.body), true);

  caseData = Object.assign(caseData, responseBody.case_data);
  if (eventName === 'CREATE_CLAIM') {
    caseId = responseBody.id;
    console.log('Case created: ' + caseId);
  }
};
