const {I} = inject();

module.exports = {

  fields: {
    directionsProposedForDisclosure: '#respondent1DQDisclosureOfNonElectronicDocuments',
  },

  async enterDirectionsProposedForDisclosure() {
    I.fillField(this.fields.directionsProposedForDisclosure, 'Reason for no agreement');

    await I.clickContinue();
  }
};
