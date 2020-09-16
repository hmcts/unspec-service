//TODO: find another assert library
const assert = require('assert').strict;

const config = require('../config.js');
const request = require('../api/request');

let caseId;

const fakeDocumentData = filename => {
  return {
    document_url: `${config.url.dmStore}/documents/fakeUrl`,
    document_filename: filename,
    document_binary_url: `${config.url.dmStore}/documents/fakeUrl/binary`,
  };
};

module.exports = {
  createClaim: async (user) => {
    await request.setupTokens(user);
    await request.startEvent('CREATE_CLAIM');

    //reference page validation
    await assertValidData();
    await assertUnknownField();
    await assertInvalidStructure();

    await submitCreateClaim();
  },

  confirmService: async () => {
    //TODO: extract case data to separate files

    // TODO: validate responses for
    //  - start event
    //  - validate pages
    //  - submit event

    await request.startEvent('CONFIRM_SERVICE', caseId);

    await submitConfirmService();
  }
};

const assertValidData = async () => {
  const caseData = {
    solicitorReferences: {
      applicantSolicitor1Reference: 'qwe',
      respondentSolicitor1Reference: 'asd'
    }
  };

  const response = await request.validatePage('CREATE_CLAIM', 'References', caseData);
  const responseBody = await response.json();

  assert.equal(response.status, 200);
  assert.deepEqual(responseBody.data, caseData);
};

const assertUnknownField = async () => {
  const caseData = {
    solicitorReferences: {
      invalidProperty: 'test'
    }
  };

  const response = await request.validatePage('CREATE_CLAIM', 'References', caseData);
  const responseBody = await response.json();

  assert.equal(response.status, 422);
  assert.equal(responseBody.message, 'Case data validation failed');
  assert.equal(responseBody.details.field_errors[0].id, 'solicitorReferences.invalidProperty');
  assert.equal(responseBody.details.field_errors[0].message, 'Field is not recognised');
};

const assertInvalidStructure = async () => {
  const caseData = {
    solicitorReferences: {
      respondentSolicitor1Reference: {
        invalidProperty: ' test'
      }
    }
  };

  const response = await request.validatePage('CREATE_CLAIM', 'References', caseData);
  const responseBody = await response.json();

  assert.equal(response.status, 422);
  assert.equal(responseBody.message, 'Case data validation failed');
  assert.equal(responseBody.details.field_errors[0].id, 'solicitorReferences.respondentSolicitor1Reference');
  assert.equal(responseBody.details.field_errors[0].message, 'object is not a string');
};

const submitCreateClaim = async () => {
  let caseData = {
    solicitorReferences: {
      applicantSolicitor1Reference: 'claimant_solicitor_reference_1/c',
      respondentSolicitor1Reference: 'defendant_solicitor_reference_1/d'
    },
    courtLocation: {
      applicantPreferredCourt: 'Royal Courts of Justice, London'
    },
    applicant1: {
      type: 'COMPANY',
      companyName: 'Test Inc',
      individualDateOfBirth: '2020-01-12'
    },
    respondent1: {
      type: 'COMPANY',
      companyName: 'Test Defendant Inc',
      individualDateOfBirth: '2020-02-12'
    },
    claimType: 'PERSONAL_INJURY',
    personalInjuryType: 'ROAD_ACCIDENT',
    claimValue: {
      lowerValue: '100',
      higherValue: '500'
    },
    applicantSolicitor1ClaimStatementOfTruth: {
      name: 'john doe',
      role: 'test'
    },
  };

  let response = await request.submitEvent('CREATE_CLAIM', caseData);
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

const submitConfirmService = async () => {
  await request.submitEvent('CONFIRM_SERVICE', {
    'servedDocuments': [
      'CLAIM_FORM',
      'PARTICULARS_OF_CLAIM'
    ],
    'servedDocumentFiles': {
      'particularsOfClaim': [
        {
          'id': '74a17e06-aa49-406e-ae82-1fd40ee42b01',
          'value': fakeDocumentData('test.pdf')
        }
      ]
    },
    'serviceMethodToRespondentSolicitor1': {
      'type': 'POST'
    },
    'serviceLocationToRespondentSolicitor1': {
      'location': 'RESIDENCE'
    },
    'serviceDateToRespondentSolicitor1': '2020-09-15',
    'applicant1ServiceStatementOfTruthToRespondentSolicitor1': {
      'name': 'mr bloggs',
      'role': 'super solitior'
    }
  }, caseId);
};
