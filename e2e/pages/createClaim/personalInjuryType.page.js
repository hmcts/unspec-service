const {I} = inject();

module.exports = {

  fields: {
    claimType: '#personalInjuryType',
  },

  async selectPersonalInjuryType() {
    I.waitForElement(this.fields.claimType);
    I.selectOption(this.fields.claimType, 'Road accident');
    await I.clickContinue();
  }
};

