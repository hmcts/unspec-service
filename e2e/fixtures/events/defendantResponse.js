const dataHelper = require('../../api/dataHelper');
const address = require('../address');

module.exports = {
  valid: {
    RespondentResponseType: {
      respondent1ClaimResponseType: 'FULL_DEFENCE'
    },
    Upload: {
      respondent1ClaimResponseDocument: {
        file: dataHelper.document('claimResponse.pdf')
      }
    },
    ConfirmNameAddress: {},
    ConfirmDetails: {
      respondent1: {
        type: 'ORGANISATION',
        organisationName: 'Test Defendant Org',
        primaryAddress: {
          AddressLine1: `${address.buildingAndStreet.lineOne + ' - defendant'}`,
          AddressLine2: address.buildingAndStreet.lineTwo,
          AddressLine3: address.buildingAndStreet.lineThree,
          PostTown: address.town,
          County: address.county,
          Country: address.country,
          PostCode: address.postcode
        }
      },
      solicitorReferences: {
        applicantSolicitor1Reference: 'Applicant test reference',
        respondentSolicitor1Reference: 'Respondent test reference'
      }
    },
    FileDirectionsQuestionnaire: {
      respondent1DQFileDirectionsQuestionnaire: {
        explainedToClient: ['CONFIRM'],
        oneMonthStayRequested: 'Yes',
        reactionProtocolCompliedWith: 'Yes'
      }
    },
    DisclosureOfElectronicDocuments: {
      respondent1DQDisclosureOfElectronicDocuments: {
        reachedAgreement: 'No',
        agreementLikely: 'Yes'
      }
    },
    DisclosureOfNonElectronicDocuments: {
      respondent1DQDisclosureOfNonElectronicDocuments: 'None'
    },
    Experts: {
      respondent1DQExperts: {
        expertRequired: 'Yes',
        exportReportsSent: 'NOT_OBTAINED',
        jointExpertSuitable: 'Yes',
        details: [{
          id: null,
          value: {
            name: 'John Doe',
            fieldOfExpertise: 'None',
            whyRequired: 'I don\'t',
            estimatedCost: '10000'
          }
        }]
      }
    },
    Witnesses: {
      respondent1DQWitnesses: {
        witnessesToAppear: 'Yes',
        details: [{
          id: null,
          value: {
            name: 'John Doe',
            reasonForWitness: 'None'
          }
        }]
      }
    },
    Hearing: {
      respondent1DQHearing: {
        hearingLength: 'MORE_THAN_DAY',
        hearingLengthDays: 5,
        unavailableDatesRequired: 'Yes',
        unavailableDates: [{
          id: null,
          value: {
            date: dataHelper.date(10),
            who: 'Foo Bar'
          }
        }]
      }
    },
    DraftDirections: {
      respondent1DQDraftDirections: dataHelper.document('draftDirections.pdf')
    },
    RequestedCourt: {
      respondent1DQRequestedCourt: {
        name: 'Example court',
        reasonForHearingAtSpecificCourt: 'No reasons',
        requestHearingAtSpecificCourt: 'Yes'
      }
    },
    HearingSupport: {},
    FurtherInformation: {
      respondent1DQFurtherInformation: {
        futureApplications: 'Yes',
        otherInformationForJudge: 'Nope',
        reasonForFutureApplications: 'Nothing'
      }
    },
    StatementOfTruth: {
      respondent1DQStatementOfTruth: {
        name: 'John Doe',
        role: 'Tester'
      }
    }
  },
  invalid: {
    Experts: {
      emptyDetails: {
        respondent1DQExperts: {
          details: [],
          expertRequired: 'Yes',
          exportReportsSent: 'NOT_OBTAINED',
          jointExpertSuitable: 'Yes'
        }
      }
    },
    Witnesses: {
      emptyDetails: {
        respondent1DQWitnesses: {
          witnessesToAppear: 'Yes',
          details: [{
            id: null,
            value: {
              name: 'John Doe',
              reasonForWitness: 'None'
            }
          }]
        }
      }
    },
    Hearing: {
      past: {
        respondent1DQHearing: {
          hearingLength: 'MORE_THAN_DAY',
          hearingLengthDays: 5,
          unavailableDatesRequired: 'Yes',
          unavailableDates: [{
            id: null,
            value: {
              date: dataHelper.date(-1),
              who: 'Foo Bar'
            }
          }]
        }
      },
      moreThanYear: {
        respondent1DQHearing: {
          hearingLength: 'MORE_THAN_DAY',
          hearingLengthDays: 5,
          unavailableDatesRequired: 'Yes',
          unavailableDates: [{
            id: null,
            value: {
              date: dataHelper.date(367),
              who: 'Foo Bar'
            }
          }]
        }
      }
    },
  }
};
