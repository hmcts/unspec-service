const {I} = inject();

module.exports = {

  fields: {
    extensionResponse: '#extensionResponse'
  },

  async enterResponse() {
    I.fillField(this.fields.extensionResponse, 'Response to extension');
    await I.clickContinue();
  }
};

