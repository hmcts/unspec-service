const assert = require('assert').strict;

const request = require('../api/request.js');

const createClaimData = require('../fixtures/createClaim.js');
const confirmServiceData = require('../fixtures/confirmService.js');

let caseId;
let data;

module.exports = {
  createClaim: async (user) => {
    data = {};
    await request.setupTokens(user);
    await request.startEvent('CREATE_CLAIM');

    await assertValidData('CREATE_CLAIM', 'References', createClaimData.valid.references);
    await assertValidData('CREATE_CLAIM', 'Court', createClaimData.valid.court);
    await assertValidData('CREATE_CLAIM', 'Claimant', createClaimData.valid.claimant);
    await assertValidData('CREATE_CLAIM', 'Defendant', createClaimData.valid.defendant);
    await assertValidData('CREATE_CLAIM', 'ClaimType', createClaimData.valid.claimType);
    await assertValidData('CREATE_CLAIM', 'PersonalInjuryType', createClaimData.valid.personalInjuryType);
    await assertValidData('CREATE_CLAIM', 'Upload', createClaimData.valid.upload);
    await assertCallbackError('CREATE_CLAIM', 'ClaimValue', createClaimData.invalid.claimValue,
      'CONTENT TBC: Higher value must not be lower than the lower value.');
    await assertValidData('CREATE_CLAIM', 'ClaimValue', createClaimData.valid.claimValue, {allocatedTrack: 'SMALL_CLAIM'});
    await assertValidData('CREATE_CLAIM', 'StatementOfTruth', createClaimData.valid.statementOfTruth);

    await submitCreateClaim();
  },

  confirmService: async () => {
    data = {};
    await request.startEvent('CONFIRM_SERVICE', caseId);

    // await assertValidData('CONFIRM_SERVICE', 'ServedDocuments', confirmServiceData.valid.servedDocuments);
    // await assertValidData('CONFIRM_SERVICE', 'Upload', confirmServiceData.valid.upload);
    // await assertValidData('CONFIRM_SERVICE', 'Method', confirmServiceData.valid.method);
    // await assertValidData('CONFIRM_SERVICE', 'Location', confirmServiceData.valid.location);
    // await assertCallbackError('CONFIRM_SERVICE', 'Date', confirmServiceData.invalid.date.yesterday,
    //   'The date must not be before issue date of claim');
    // await assertCallbackError('CONFIRM_SERVICE', 'Date', confirmServiceData.invalid.date.tomorrow,
    //   'The date must not be in the future');
    // await assertValidData('CONFIRM_SERVICE', 'Date', confirmServiceData.valid.date);
    // await assertValidData('CONFIRM_SERVICE', 'StatementOfTruth', confirmServiceData.valid.statementOfTruth);

    // await submitConfirmService();
  }
};

const assertValidData = async (eventName, pageId, caseData, additionalCallbackData = {}) => {
  data = Object.assign(data, caseData);
  const response = await request.validatePage(eventName, pageId, data);

  const responseBody = await response.json();
  assert.equal(response.status, 200);
  assert.deepEqual(responseBody.data, Object.assign(data, additionalCallbackData));
};

const assertCallbackError = async (eventName, pageId, caseData, expectedErrorMessage) => {
  data = Object.assign(data, caseData);
  console.log(data);
  const response = await request.validatePage(eventName, pageId, data);

  const responseBody = await response.json();
  console.log(responseBody);
  assert.equal(response.status, 422);
  assert.equal(responseBody.message, 'Unable to proceed because there are one or more callback Errors or Warnings');
  assert.equal(responseBody.callbackErrors[0], expectedErrorMessage);
};

// const submitEvent = async (eventName, caseData, expectedState) => {
//   let response = await request.submitEvent(eventName, caseData);
//   const responseBody = await response.json();
//
//   assert.equal(response.status, 201);
//   assert.equal(Object.prototype.hasOwnProperty.call(responseBody, 'id'), true);
//   assert.equal(responseBody.state, expectedState);
//   //TODO: validate case_data returned
//   assert.equal(responseBody.callback_response_status_code, 200);
//   assert.equal(responseBody.after_submit_callback_response.confirmation_header.includes('# Your claim has been issued\n## Claim number'), true);
//   assert.equal(responseBody.after_submit_callback_response.confirmation_body.includes('Follow these steps to serve a claim'), true);
// };

const submitCreateClaim = async () => {
  let response = await request.submitEvent('CREATE_CLAIM', data);

  const responseBody = await response.json();
  assert.equal(response.status, 201);
  assert.equal(Object.prototype.hasOwnProperty.call(responseBody, 'id'), true);
  assert.equal(responseBody.state, 'CREATED');
  //TODO: validate case_data returned
  assert.equal(responseBody.callback_response_status_code, 200);
  assert.equal(responseBody.after_submit_callback_response.confirmation_header.includes('# Your claim has been issued\n## Claim number'), true);
  assert.equal(responseBody.after_submit_callback_response.confirmation_body.includes('Follow these steps to serve a claim'), true);

  caseId = responseBody.id;
  console.log('CREATED CASE ID: ' + responseBody.id);
};
