const {I} = inject();

module.exports = {

  fields: function (party) {
    return {
      draftDirections: `#${party}DQDraftDirections`,
    };
  },

  async enterDraftDirections(party) {
    I.fillField(this.fields(party).draftDirections, 'Draft directions');

    await I.clickContinue();
  }
};
