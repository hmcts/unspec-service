const dataHelper = require('../../api/dataHelper');

module.exports = {
  valid: {
    RespondentResponse: {
      applicant1ProceedWithClaim: 'Yes'
    },
    DefenceResponseDocument: {
      applicant1DefenceResponseDocument: {
        file: dataHelper.document('defenceResponse.pdf')
      }
    }
  },
  invalid: {}
};
