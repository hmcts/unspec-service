const {I} = inject();

module.exports = {

  fields: function (party) {
    return {
      directionsForDisclosureProposed: {
        id: `#${party}DQDisclosureOfNonElectronicDocuments_directionsForDisclosureProposed`,
        options: {
          yes: 'Yes',
          no: 'No'
        }
      },
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
    I.waitForElement(this.fields(party).directionsForDisclosureProposed);
    await within(this.fields(party).directionsForDisclosureProposed.id, () => {
      I.click(this.fields(party).directionsForDisclosureProposed.options.yes);
    });
    await within(this.fields(party).bespokeDirectionsRequired.id, () => {
      I.click(this.fields(party).bespokeDirectionsRequired.options.yes);
    });
    I.fillField(this.fields(party).bespokeDirections, 'Bespoke directions');

    await I.clickContinue();
  }
};
