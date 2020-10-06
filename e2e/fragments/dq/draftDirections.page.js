const {I} = inject();

module.exports = {

  fields: {
    draftDirections: '#respondent1DQDraftDirections',
  },

  async enterDraftDirections() {
    I.fillField(this.fields.draftDirections, 'Draft directions');

    await I.clickContinue();
  }
};
