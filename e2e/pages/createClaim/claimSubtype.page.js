const {I} = inject();

module.exports = {

  fields: {
    claimSubtype: {
      id: '#claimSubtype',
      options: {
        roadAccident: 'Road accident'
      }
    },
  },

  async selectSubtype() {
    I.waitForElement(this.fields.claimSubtype.id);
    I.selectOption(this.fields.claimSubtype.id, this.fields.claimSubtype.options.roadAccident);
    await I.clickContinue();
  }
};

