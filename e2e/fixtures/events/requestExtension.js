const dataHelper = require('../../api/dataHelper');

module.exports = {
  valid: {
    ProposeDeadline: {
      respondentSolicitor1claimResponseExtensionProposedDeadline: dataHelper.date(31)
    },
    ExtensionAlreadyAgreed: {
      respondentSolicitor1claimResponseExtensionAlreadyAgreed: 'No',
      respondentSolicitor1claimResponseExtensionReason: 'Because I say so'
    }
  },
  invalid: {
    ProposeDeadline: {
      past: {
        respondentSolicitor1claimResponseExtensionProposedDeadline: dataHelper.date(-1)
      },
      beforeCurrentDeadline: {
        respondentSolicitor1claimResponseExtensionProposedDeadline: dataHelper.date(10)
      }
    }
  }
};
