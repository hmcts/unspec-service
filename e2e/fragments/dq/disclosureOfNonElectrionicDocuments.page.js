const {I} = inject();

module.exports = {

  fields: function (party) {
    return {
      directionsProposedForDisclosure: `#${party}DQDisclosureOfNonElectronicDocuments`,
    };
  },

  async enterDirectionsProposedForDisclosure(party) {
    I.fillField(this.fields(party).directionsProposedForDisclosure, 'Reason for no agreement');

    await I.clickContinue();
  }
};
