const { I } = inject();

module.exports = {

  fields: {
    respondentResponseDocument: {
      id: '#respondentResponseDocument_responseDocument'
    }
  },

  async uploadResponseDocuments (file) {
    I.waitForElement(this.fields.respondentResponseDocument.id);
    await I.attachFile(this.fields.respondentResponseDocument.id, file);
    await I.waitForInvisible(locate('.error-message').withText('Uploading...'));

    await I.clickContinue();
  },
};

