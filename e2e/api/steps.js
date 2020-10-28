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
    const eventName = 'CREATE_CLAIM';
    await request.setupTokens(user);
    await request.startEvent(eventName);

    await assertValidData(eventName, 'References', data.createClaim.valid.References);
    await assertValidData(eventName, 'Court', data.createClaim.valid.Court);
    await assertValidData(eventName, 'Claimant', data.createClaim.valid.Claimant);
    await assertValidData(eventName, 'ClaimantLitigationFriend', data.createClaim.valid.ClaimantLitigationFriend);
    await assertValidData(eventName, 'Defendant', data.createClaim.valid.Defendant);
    await assertValidData(eventName, 'ClaimType', data.createClaim.valid.ClaimType);
    await assertValidData(eventName, 'PersonalInjuryType', data.createClaim.valid.PersonalInjuryType);
    await assertValidData(eventName, 'Upload', data.createClaim.valid.Upload);
    await assertValidData(eventName, 'ClaimValue', data.createClaim.valid.ClaimValue);
    await assertValidData(eventName, 'StatementOfTruth', data.createClaim.valid.StatementOfTruth);

    await assertSubmittedEvent(eventName, 'CREATED', {
      header: 'Your claim has been issued',
      body: 'Follow these steps to serve a claim'
    });
  },

  confirmService: async () => {
    const eventName = 'CONFIRM_SERVICE';
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent(eventName, caseId);

    deleteCaseFields('servedDocumentFiles');

    await assertValidData(eventName, 'ServedDocuments', data.confirmService.valid.ServedDocuments);
    await assertValidData(eventName, 'Upload', data.confirmService.valid.Upload);
    await assertValidData(eventName, 'Method', data.confirmService.valid.Method);
    await assertValidData(eventName, 'Location', data.confirmService.valid.Location);
    await assertValidData(eventName, 'Date', data.confirmService.valid.Date);
    await assertValidData(eventName, 'StatementOfTruth', data.confirmService.valid.StatementOfTruth);

    await assertCallbackError(eventName, 'ServedDocuments', data.confirmService.invalid.ServedDocuments.blankOtherDocuments,
      'CONTENT TBC: please enter a valid value for other documents');
    await assertCallbackError(eventName, 'Date', data.confirmService.invalid.Date.yesterday,
      'The date must not be before issue date of claim');
    await assertCallbackError(eventName, 'Date', data.confirmService.invalid.Date.tomorrow,
      'The date must not be in the future');

    await assertSubmittedEvent(eventName, 'CREATED', {
      header: 'You\'ve confirmed service',
      body: 'Deemed date of service'
    });
  },

  acknowledgeService: async () => {
    const eventName = 'ACKNOWLEDGE_SERVICE';
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent(eventName, caseId);

    await assertValidData(eventName, 'ConfirmNameAddress', data.acknowledgeService.valid.ConfirmNameAddress);
    await assertValidData(eventName, 'ConfirmDetails', data.acknowledgeService.valid.ConfirmDetails);
    await assertValidData(eventName, 'ResponseIntention', data.acknowledgeService.valid.ResponseIntention);

    await assertCallbackError(eventName, 'ConfirmDetails', data.acknowledgeService.invalid.ConfirmDetails.futureDateOfBirth,
      'The date entered cannot be in the future');

    await assertSubmittedEvent(eventName, 'CREATED', {
      header: 'You\'ve acknowledged service',
      body: 'You need to respond before'
    });
  },

  requestExtension: async () => {
    const eventName = 'REQUEST_EXTENSION';
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent(eventName, caseId);

    await assertValidData(eventName, 'ProposeDeadline', data.requestExtension.valid.ProposeDeadline);
    await assertValidData(eventName, 'ExtensionAlreadyAgreed', data.requestExtension.valid.ExtensionAlreadyAgreed);

    await assertCallbackError(eventName, 'ProposeDeadline', data.requestExtension.invalid.ProposeDeadline.past,
      'The proposed deadline must be a date in the future');
    await assertCallbackError(eventName, 'ProposeDeadline',data.requestExtension.invalid.ProposeDeadline.beforeCurrentDeadline,
      'The proposed deadline must be after the current deadline');

    await assertSubmittedEvent(eventName, 'CREATED', {
      header: 'You asked for extra time to respond',
      body: 'You asked if you can respond before 4pm on'
    });
  },

  respondExtension: async () => {
    const eventName = 'RESPOND_EXTENSION';
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent(eventName, caseId);

    await assertCallbackError(eventName, 'Counter', data.respondExtension.invalid.Counter.past,
      'The proposed deadline must be a date in the future');
    await assertCallbackError(eventName, 'Counter',data.respondExtension.invalid.Counter.beforeCurrentDeadline,
      'The proposed deadline must be after the current deadline');
    await assertValidData(eventName, 'Respond', data.respondExtension.valid.Respond);
    await assertValidData(eventName, 'Counter', data.respondExtension.valid.Counter);
    await assertValidData(eventName, 'Reason', data.respondExtension.valid.Reason);

    await assertSubmittedEvent(eventName, 'CREATED', {
      header: 'You\'ve responded to the request for more time',
      body: 'The defendant must respond before 4pm on'
    });
  },

  defendantResponse: async () => {
    const eventName = 'DEFENDANT_RESPONSE';
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent(eventName, caseId);

    deleteCaseFields('respondent1', 'solicitorReferences');
    await assertValidData(eventName, 'RespondentResponseType', data.defendantResponse.valid.RespondentResponseType);
    await assertValidData(eventName, 'Upload', data.defendantResponse.valid.Upload);
    await assertValidData(eventName, 'ConfirmNameAddress', data.defendantResponse.valid.ConfirmNameAddress);
    await assertValidData(eventName, 'ConfirmDetails', data.defendantResponse.valid.ConfirmDetails);
    await assertValidData(eventName, 'FileDirectionsQuestionnaire', data.defendantResponse.valid.FileDirectionsQuestionnaire);
    await assertValidData(eventName, 'DisclosureOfElectronicDocuments', data.defendantResponse.valid.DisclosureOfElectronicDocuments);
    await assertValidData(eventName, 'DisclosureOfNonElectronicDocuments', data.defendantResponse.valid.DisclosureOfNonElectronicDocuments);
    await assertValidData(eventName, 'Experts', data.defendantResponse.valid.Experts);
    await assertValidData(eventName, 'Witnesses', data.defendantResponse.valid.Witnesses);
    await assertValidData(eventName, 'Hearing', data.defendantResponse.valid.Hearing);
    await assertValidData(eventName, 'DraftDirections', data.defendantResponse.valid.DraftDirections);
    await assertValidData(eventName, 'RequestedCourt', data.defendantResponse.valid.RequestedCourt);
    await assertValidData(eventName, 'HearingSupport', data.defendantResponse.valid.HearingSupport);
    await assertValidData(eventName, 'FurtherInformation', data.defendantResponse.valid.FurtherInformation);
    await assertValidData(eventName, 'StatementOfTruth', data.defendantResponse.valid.StatementOfTruth);
    await assertCallbackError(eventName, 'ConfirmDetails', data.defendantResponse.invalid.ConfirmDetails.futureDateOfBirth,
      'The date entered cannot be in the future');
    await assertCallbackError(eventName, 'Hearing', data.defendantResponse.invalid.Hearing.past,
      'The date cannot be in the past and must not be more than a year in the future');
    await assertCallbackError(eventName, 'Hearing', data.defendantResponse.invalid.Hearing.moreThanYear,
      'The date cannot be in the past and must not be more than a year in the future');


    await assertSubmittedEvent(eventName, 'AWAITING_CLAIMANT_INTENTION', {
      header: 'You\'ve submitted your response',
      body: 'We will let you know when they respond.'
    });
  },

  claimantResponse: async () => {
    const eventName = 'CLAIMANT_RESPONSE';
    await testingSupport.resetBusinessProcess(caseId);
    await request.startEvent(eventName, caseId);

    await assertValidData(eventName, 'RespondentResponse', data.claimantResponse.valid.RespondentResponse);
    await assertValidData(eventName, 'DefenceResponseDocument', data.claimantResponse.valid.DefenceResponseDocument);

    await assertSubmittedEvent(eventName, 'AWAITING_CLAIMANT_INTENTION', {
      header: 'You\'ve decided to proceed with the claim',
      body: 'We\'ll review the case. We\'ll contact you to tell you what to do next.'
    });
  }
};

const assertValidData = async (eventName, pageId, eventData, expectedDataSetByCallback = {}) => {
  caseData = {...caseData, ...eventData};
  const response = await request.validatePage(eventName, pageId, caseData);
  const responseBody = await response.json();
  caseData = {...caseData, ...expectedDataSetByCallback};

  if (response.status != 200) {
    console.log(responseBody);
    console.log(responseBody.details.field_errors);
  }

  assert.equal(response.status, 200);
  assert.deepEqual(responseBody.data, caseData);
};

const assertCallbackError = async (eventName, pageId, eventData, expectedErrorMessage) => {
  const response = await request.validatePage(eventName, pageId, {...caseData, ...eventData});
  const responseBody = await response.json();

  assert.equal(response.status, 422);
  assert.equal(responseBody.message, 'Unable to proceed because there are one or more callback Errors or Warnings');
  assert.equal(responseBody.callbackErrors[0], expectedErrorMessage);
};

const assertSubmittedEvent = async (eventName, expectedState, submittedCallbackResponseContains) => {
  const response = await request.submitEvent(eventName, caseData, caseId);
  const responseBody = await response.json();

  if (response.status != 201) {
    console.log(responseBody);
  }

  assert.equal(response.status, 201);
  assert.equal(responseBody.state, expectedState);
  assert.equal(responseBody.callback_response_status_code, 200);
  assert.equal(responseBody.after_submit_callback_response.confirmation_header.includes(submittedCallbackResponseContains.header), true);
  assert.equal(responseBody.after_submit_callback_response.confirmation_body.includes(submittedCallbackResponseContains.body), true);

  caseData = {...caseData, ...responseBody.case_data};
  if (eventName === 'CREATE_CLAIM') {
    caseId = responseBody.id;
    console.log('Case created: ' + caseId);
  }
};

// Mid event will not return case fields that were already filled in another event if they're present on currently processed event.
// This happens until these case fields are set again as a part of current event (note that this data is not removed from the case).
// Therefore these case fields need to be removed from caseData, as caseData object is used to make assertions
const deleteCaseFields = (...caseFields) => {
  caseFields.forEach(caseField => delete caseData[caseField]);
};
