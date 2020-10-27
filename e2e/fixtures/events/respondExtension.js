const dataHelper = require('../../api/dataHelper');

module.exports = {
  valid: {
    Respond: {
      respondentSolicitor1claimResponseExtensionAccepted: 'No'
    },
    Counter: {
      respondentSolicitor1claimResponseExtensionCounter: 'Yes',
      respondentSolicitor1claimResponseExtensionCounterDate: dataHelper.date(31)
    },
    Reason: {
      respondentSolicitor1claimResponseExtensionRejectionReason: 'Respond faster'
    }
  },
  invalid: {
    Counter: {
      past: {
        respondentSolicitor1claimResponseExtensionCounter: 'Yes',
        respondentSolicitor1claimResponseExtensionCounterDate: dataHelper.date(-1)
      },
      beforeCurrentDeadline: {
        respondentSolicitor1claimResponseExtensionCounter: 'Yes',
        respondentSolicitor1claimResponseExtensionCounterDate: dataHelper.date(10)
      }
    }
  }
};
