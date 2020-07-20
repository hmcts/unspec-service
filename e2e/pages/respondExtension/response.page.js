const {I} = inject();

module.exports = {

  fields: {
    extensionResponse: '#defendantSolicitor1claimResponseExtensionResponse'
  },

  async enterResponse() {
    I.fillField(this.fields.extensionResponse, 'Response to extension');
    await I.clickContinue();
  }
};

