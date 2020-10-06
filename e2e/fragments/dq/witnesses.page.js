const {I} = inject();

module.exports = {

  fields: {
    witnessesToAppear: {
      id: '#respondent1DQWitnesses_witnessesToAppear',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    witnessDetails: {
      id: '#respondent1DQWitnesses_details',
      element: {
        name: '#respondent1DQWitnesses_details_0_name',
        reasonForWitness: '#respondent1DQWitnesses_details_0_fieldOfExpertise',
      }
    },
  },

  async enterWitnessInformation() {
    await within (this.fields.witnessesToAppear.id, () => {
      I.click(this.fields.witnessesToAppear.options.yes);
    });

    await this.addWitness();
    await I.clickContinue();
  },

  async addWitness() {
    await I.addAnotherElementToCollection();
    I.fillField(this.fields.witnessDetails.element.name, 'John Smith');
    I.fillField(this.fields.witnessDetails.element.reasonForWitness, 'Reason for witness');
  },
};
