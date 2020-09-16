const request = require('../api/request');
const assert = require('assert').strict;

module.exports = {
  createClaim: async (user) => {
    const tokens = await request.getTokens(user);
    tokens.ccdEvent = await request.getCreateClaimToken(tokens);

    await validateReferences(tokens);
    await createClaim(tokens);
  }
};

const validateReferences = async apiData => {
  await assertValidData(apiData);
  await assertUnknownField(apiData);
  await assertInvalidStructure(apiData);
};

const assertValidData = async apiData => {
  const caseData = {
    solicitorReferences: {
      applicantSolicitor1Reference: 'qwe',
      respondentSolicitor1Reference: 'asd'
    }
  };

  const response = await request.validate(apiData, 'CREATE_CLAIMReferences', caseData);
  const responseBody = await response.json();

  assert.equal(response.status, 200);
  assert.deepEqual(responseBody.data, caseData);
};

const assertUnknownField = async apiData => {
  const caseData = {
    solicitorReferences: {
      invalidProperty: 'test'
    }
  };

  const response = await request.validate(apiData, 'CREATE_CLAIMReferences', caseData);
  const responseBody = await response.json();

  assert.equal(response.status, 422);
  assert.equal(responseBody.message, 'Case data validation failed');
  assert.equal(responseBody.details.field_errors[0].id, 'solicitorReferences.invalidProperty');
  assert.equal(responseBody.details.field_errors[0].message, 'Field is not recognised');
};

const assertInvalidStructure = async apiData => {
  const caseData = {
    solicitorReferences: {
      respondentSolicitor1Reference: {
        invalidProperty: ' test'
      }
    }
  };

  const response = await request.validate(apiData, 'CREATE_CLAIMReferences', caseData);
  const responseBody = await response.json();

  assert.equal(response.status, 422);
  assert.equal(responseBody.message, 'Case data validation failed');
  assert.equal(responseBody.details.field_errors[0].id, 'solicitorReferences.respondentSolicitor1Reference');
  assert.equal(responseBody.details.field_errors[0].message, 'object is not a string');
};

const createClaim = async apiData => {
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

  let response = await request.createClaim(apiData, caseData);
  const responseBody = await response.json();

  assert.equal(response.status, 201);
  assert.equal(Object.prototype.hasOwnProperty.call(responseBody, 'id'), true);
  assert.equal(responseBody.state, 'CREATED');
  //TODO: validate case_data
  assert.equal(responseBody.callback_response_status_code, 200);
  assert.equal(responseBody.after_submit_callback_response.confirmation_header.includes('# Your claim has been issued\n## Claim number'), true);
  assert.equal(responseBody.after_submit_callback_response.confirmation_body.includes('Follow these steps to serve a claim'), true);

  //TODO: assert expected behaviour for invalid case data
  console.log('CREATED CASE ID: ' + responseBody.id);
};
