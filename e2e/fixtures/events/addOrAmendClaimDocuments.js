const {document, element} = require('../../api/dataHelper');

module.exports = {
  valid: {
    Upload: {
      servedDocumentFiles: {
        particularsOfClaimFile: [element(document('particularsOfClaim.pdf'))]
      }
    },
  },
  invalid: {
    Upload: {
      duplicateError: {
        servedDocumentFiles: {
          particularsOfClaimFile: [element(document('particularsOfClaim.pdf'))],
          particularsOfClaimText: 'Some text'
        }
      },
      nullError: {
        servedDocumentFiles: {}
      }
    }
  }
};
