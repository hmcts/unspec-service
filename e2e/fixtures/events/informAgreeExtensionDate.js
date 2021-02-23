const {date} = require('../../api/dataHelper');

module.exports = {
  valid: {
    ExtensionDate: {
      respondentSolicitor1claimResponseExtensionProposedDeadline: date(40)
    }
  },
  invalid: {
    ExtensionDate: {
      past: {
        respondentSolicitor1claimResponseExtensionProposedDeadline: date(-1)
      },
      beforeCurrentDeadline: {
        respondentSolicitor1claimResponseExtensionProposedDeadline: date(10)
      }
    }
  }
};
