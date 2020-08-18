const {I} = inject();

module.exports = {

  fields: {
    respondentResponseType: {
      id: '#respondentResponseType',
      options: {
        fullDefence: 'Rejects all of the claim',
        fullAdmission: 'Admits all of the claim',
        partAdmission: 'Admits part of the claim',
        counterClaim: 'Reject all of the claim and wants to counterclaim'
      }
    }
  },

  async selectFullDefence() {
    I.waitForElement(this.fields.respondentResponseType.id);
    await within(this.fields.respondentResponseType.id, () => {
      I.click(this.fields.respondentResponseType.options.fullDefence);
    });

    await I.clickContinue();
  }
};

