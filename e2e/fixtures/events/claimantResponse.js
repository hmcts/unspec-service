const {document} = require('../../api/dataHelper');

module.exports = {
  valid: {
    RespondentResponse: {
      applicant1ProceedWithClaim: 'Yes'
    },
    DefenceResponseDocument: {
      applicant1DefenceResponseDocument: {
        file: document('defenceResponse.pdf')
      }
    }
  },
  invalid: {}
};
