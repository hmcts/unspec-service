const {I} = inject();

module.exports = {

  fields: function (party) {
    return {
      bespokeDirectionsRequired: {
        id: `#${party}DQDisclosureOfNonElectronicDocuments_bespokeDirectionsRequired`,
        options: {
          yes: 'Yes',
          no: 'No'
        }
      },
      bespokeDirections: `#${party}DQDisclosureOfNonElectronicDocuments_bespokeDirections`
    };
  },

  async enterDirectionsProposedForDisclosure(party) {
    I.waitForElement(this.fields(party).bespokeDirectionsRequired);
    await within(this.fields(party).bespokeDirectionsRequired.id, () => {
      I.click(this.fields(party).bespokeDirectionsRequired.options.yes);
    });
    I.fillField(this.fields(party).bespokeDirections, 'Bespoke directions');

    await I.clickContinue();
  }
};
