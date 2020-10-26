const dataHelper = require('../api/dataHelper.js');

module.exports = {
  valid: {
    ServedDocuments: {
      servedDocuments: ['CLAIM_FORM']
    },
    Upload: {
      servedDocumentFiles: {
        particularsOfClaim: [dataHelper.document('testDocument.pdf')]
      }
    },
    Method: {
      serviceMethodToRespondentSolicitor1: {
        type: 'POST'
      }
    },
    Location: {
      serviceLocationToRespondentSolicitor1: {
        location: 'RESIDENCE'
      }
    },
    Date: {
      serviceDateToRespondentSolicitor1: dataHelper.date()
    },
    StatementOfTruth: {
      applicant1ServiceStatementOfTruthToRespondentSolicitor1: {
        name: 'Foo Bar',
        role: 'Service Test Solicitor',
      }
    },
  },
  invalid: {
    Date: {
      yesterday: {
        serviceDateToRespondentSolicitor1: dataHelper.date(-1)
      },
      tomorrow: {
        serviceDateToRespondentSolicitor1: dataHelper.date(1)
      }
    }
  }
};
