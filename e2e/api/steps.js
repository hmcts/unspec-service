const assert = require('assert').strict;

const request = require('./request.js');
const testingSupport = require('./testingSupport.js');

const createClaimData = require('../fixtures/createClaim.js');
const confirmServiceData = require('../fixtures/confirmService.js');
const acknowledgeServiceData = require('../fixtures/acknowledgeService.js');
const requestExtensionData = require('../fixtures/requestExtension.js');
const respondExtensionData = require('../fixtures/respondExtension.js');
const defendantResponseData = require('../fixtures/defendantResponse.js');
const claimantResponseData = require('../fixtures/claimantResponse.js');

let caseId;
let caseData = {};

module.exports = {
  createClaim: async (user) => {
    await request.setupTokens(user);
    await request.startEvent('CREATE_CLAIM');

    await assertValidData('CREATE_CLAIM', 'References', createClaimData.valid.References);
    await assertValidData('CREATE_CLAIM', 'Court', createClaimData.valid.Court);
    await assertValidData('CREATE_CLAIM', 'Claimant', createClaimData.valid.Claimant);
    await assertValidData('CREATE_CLAIM', 'ClaimantLitigationFriend', createClaimData.valid.ClaimantLitigationFriend);
    await assertValidData('CREATE_CLAIM', 'Defendant', createClaimData.valid.Defendant);
    await assertValidData('CREATE_CLAIM', 'ClaimType', createClaimData.valid.ClaimType);
    await assertValidData('CREATE_CLAIM', 'PersonalInjuryType', createClaimData.valid.PersonalInjuryType);
    await assertValidData('CREATE_CLAIM', 'Upload', createClaimData.valid.Upload);
    await assertValidData('CREATE_CLAIM', 'ClaimValue', createClaimData.valid.ClaimValue);
    await assertValidData('CREATE_CLAIM', 'StatementOfTruth', createClaimData.valid.StatementOfTruth);

    await assertSubmittedEvent('CREATE_CLAIM', 'CREATED', {
      header: 'Your claim has been issued',
      body: 'Follow these steps to serve a claim'
    });
  },

  confirmService: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('CONFIRM_SERVICE', caseId);

    delete caseData.servedDocumentFiles;
    await assertValidData('CONFIRM_SERVICE', 'ServedDocuments', confirmServiceData.valid.ServedDocuments);
    await assertValidData('CONFIRM_SERVICE', 'Upload', confirmServiceData.valid.Upload);
    await assertValidData('CONFIRM_SERVICE', 'Method', confirmServiceData.valid.Method);
    await assertValidData('CONFIRM_SERVICE', 'Location', confirmServiceData.valid.Location);
    await assertCallbackError('CONFIRM_SERVICE', 'Date', confirmServiceData.invalid.Date.yesterday,
      'The date must not be before issue date of claim');
    await assertCallbackError('CONFIRM_SERVICE', 'Date', confirmServiceData.invalid.Date.tomorrow,
      'The date must not be in the future');
    await assertValidData('CONFIRM_SERVICE', 'Date', confirmServiceData.valid.Date);
    await assertValidData('CONFIRM_SERVICE', 'StatementOfTruth', confirmServiceData.valid.StatementOfTruth);

    await assertSubmittedEvent('CONFIRM_SERVICE', 'CREATED', {
      header: 'You\'ve confirmed service',
      body: 'Deemed date of service'
    });
  },

  acknowledgeService: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('ACKNOWLEDGE_SERVICE', caseId);

    await assertValidData('ACKNOWLEDGE_SERVICE', 'ConfirmNameAddress', acknowledgeServiceData.valid.ConfirmNameAddress);
    await assertValidData('ACKNOWLEDGE_SERVICE', 'ConfirmDetails', acknowledgeServiceData.valid.ConfirmDetails);
    await assertValidData('ACKNOWLEDGE_SERVICE', 'ResponseIntention', acknowledgeServiceData.valid.ResponseIntention);

    await assertSubmittedEvent('ACKNOWLEDGE_SERVICE', 'CREATED', {});
  },

  requestExtension: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('REQUEST_EXTENSION', caseId);

    await assertValidData('REQUEST_EXTENSION', 'ProposeDeadline', requestExtensionData.valid.ProposeDeadline);
    await assertValidData('REQUEST_EXTENSION', 'ExtensionAlreadyAgreed', requestExtensionData.valid.ExtensionAlreadyAgreed);

    await assertSubmittedEvent('REQUEST_EXTENSION', 'CREATED', {});
  },

  respondExtension: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('RESPOND_EXTENSION', caseId);

    await assertValidData('RESPOND_EXTENSION', 'Respond', respondExtensionData.valid.Respond);
    await assertValidData('RESPOND_EXTENSION', 'Counter', respondExtensionData.valid.Counter);
    await assertValidData('RESPOND_EXTENSION', 'Reason', respondExtensionData.valid.Reason);

    await assertSubmittedEvent('RESPOND_EXTENSION', 'CREATED', {});
  },

  defendantResponse: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('DEFENDANT_RESPONSE', caseId);

    //TODO: check if ccd api allows validating hidden pages
    await assertValidData('DEFENDANT_RESPONSE', 'RespondentResponseType', defendantResponseData.valid.RespondentResponseType);
    await assertValidData('DEFENDANT_RESPONSE', 'Upload', defendantResponseData.valid.Upload);
    await assertValidData('DEFENDANT_RESPONSE', 'ConfirmNameAddress', defendantResponseData.valid.ConfirmNameAddress);
    await assertValidData('DEFENDANT_RESPONSE', 'ConfirmDetails', defendantResponseData.valid.ConfirmDetails);
    await assertValidData('DEFENDANT_RESPONSE', 'FileDirectionsQuestionnaire', defendantResponseData.valid.FileDirectionsQuestionnaire);
    await assertValidData('DEFENDANT_RESPONSE', 'DisclosureOfElectronicDocuments', defendantResponseData.valid.DisclosureOfElectronicDocuments);
    await assertValidData('DEFENDANT_RESPONSE', 'DisclosureOfNonElectronicDocuments', defendantResponseData.valid.DisclosureOfNonElectronicDocuments);
    await assertValidData('DEFENDANT_RESPONSE', 'DisclosureReport', defendantResponseData.valid.DisclosureReport);
    await assertValidData('DEFENDANT_RESPONSE', 'Experts', defendantResponseData.valid.Experts);
    await assertValidData('DEFENDANT_RESPONSE', 'Witnesses', defendantResponseData.valid.Witnesses);
    await assertValidData('DEFENDANT_RESPONSE', 'Hearing', defendantResponseData.valid.Hearing);
    await assertValidData('DEFENDANT_RESPONSE', 'DraftDirections', defendantResponseData.valid.DraftDirections);
    await assertValidData('DEFENDANT_RESPONSE', 'RequestedCourt', defendantResponseData.valid.RequestedCourt);
    await assertValidData('DEFENDANT_RESPONSE', 'HearingSupport', defendantResponseData.valid.HearingSupport);
    await assertValidData('DEFENDANT_RESPONSE', 'FurtherInformation', defendantResponseData.valid.FurtherInformation);
    await assertValidData('DEFENDANT_RESPONSE', 'StatementOfTruth', defendantResponseData.valid.StatementOfTruth);

    await assertSubmittedEvent('DEFENDANT_RESPONSE', 'AWAITING_CLAIMANT_INTENTION', {});
  },

  claimantResponse: async () => {
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent('CLAIMANT_RESPONSE', caseId);

    await assertValidData('CLAIMANT_RESPONSE', 'RespondentResponse', claimantResponseData.valid.RespondentResponse);
    await assertValidData('CLAIMANT_RESPONSE', 'DefenceResponseDocument', claimantResponseData.valid.DefenceResponseDocument);

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
