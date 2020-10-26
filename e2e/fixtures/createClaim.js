const dataHelper = require('../api/dataHelper');
const config = require('../config');

module.exports = {
  valid: {
    References: {
      solicitorReferences: {
        applicantSolicitor1Reference: 'Applicant test reference',
        respondentSolicitor1Reference: 'Respondent test reference'
      }
    },
    Court: {
      courtLocation: {
        applicantPreferredCourt: 'Test Preferred Court'
      }
    },
    Claimant: {
      applicant1: {
        type: 'COMPANY',
        companyName: 'Test Inc',
        primaryAddress: {
          AddressLine1: `${config.address.buildingAndStreet.lineOne + ' - claimant'}`,
          AddressLine2: config.address.buildingAndStreet.lineTwo,
          AddressLine3: config.address.buildingAndStreet.lineThree,
          PostTown: config.address.town,
          County: config.address.county,
          Country: config.address.country,
          PostCode: config.address.postcode
        }
      }
    },
    ClaimantLitigationFriend: {
      applicant1LitigationFriend: {
        required: 'Yes',
        fullName: 'Bob the litigant friend',
        hasSameAddressAsLitigant: 'No',
        primaryAddress: {
          AddressLine1: `${config.address.buildingAndStreet.lineOne + ' - litigant friend'}`,
          AddressLine2: config.address.buildingAndStreet.lineTwo,
          AddressLine3: config.address.buildingAndStreet.lineThree,
          PostTown: config.address.town,
          County: config.address.county,
          Country: config.address.country,
          PostCode: config.address.postcode
        }
      }
    },
    Defendant: {
      respondent1: {
        type: 'ORGANISATION',
        organisationName: 'Test Defendant Org',
        primaryAddress: {
          AddressLine1: `${config.address.buildingAndStreet.lineOne + ' - defendant'}`,
          AddressLine2: config.address.buildingAndStreet.lineTwo,
          AddressLine3: config.address.buildingAndStreet.lineThree,
          PostTown: config.address.town,
          County: config.address.county,
          Country: config.address.country,
          PostCode: config.address.postcode
        }
      }
    },
    ClaimType: {
      claimType: 'PERSONAL_INJURY'
    },
    PersonalInjuryType: {
      personalInjuryType: 'ROAD_ACCIDENT'
    },
    Upload: {
      servedDocumentFiles: {
        particularsOfClaim: [dataHelper.document('testDocument.pdf')]
      }
    },
    ClaimValue: {
      claimValue: {
        statementOfValueInPennies: '500'
      }
    },
    StatementOfTruth: {
      applicantSolicitor1ClaimStatementOfTruth: {
        name: 'John Doe',
        role: 'Test Solicitor'
      }
    }
  },
};
