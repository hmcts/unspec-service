const {I} = inject();

module.exports = {

  fields: {
    expertRequired: {
      id: '#respondent1DQExperts_expertRequired',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    exportReportsSent: {
      id: '#respondent1DQExperts_exportReportsSent',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    jointExpertSuitable: {
      id: '#respondent1DQExperts_jointExpertSuitable',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    expertDetails: {
      id: '#respondent1DQExperts_details',
      element: {
        name: '#respondent1DQExperts_details_0_name',
        fieldOfExpertise: '#respondent1DQExperts_details_0_fieldOfExpertise',
        whyRequired: '#respondent1DQExperts_details_0_whyRequired',
        estimatedCost: '#respondent1DQExperts_details_0_estimatedCost',
      }
    },
  },

  async enterExpertInformation() {
    await within (this.fields.expertRequired.id, () => {
      I.click(this.fields.expertRequired.options.yes);
    });

    await within (this.fields.exportReportsSent.id, () => {
      I.click(this.fields.exportReportsSent.options.yes);
    });

    await within (this.fields.jointExpertSuitable.id, () => {
      I.click(this.fields.jointExpertSuitable.options.yes);
    });

    await this.addExpert();
    await I.clickContinue();
  },

  async addExpert() {
    await I.addAnotherElementToCollection();
    I.fillField(this.fields.expertDetails.element.name, 'John Smith');
    I.fillField(this.fields.expertDetails.element.fieldOfExpertise, 'Science');
    I.fillField(this.fields.expertDetails.element.whyRequired, 'Reason why required');
    I.fillField(this.fields.expertDetails.element.estimatedCost, '100');
  },
};
