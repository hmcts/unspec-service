const {I} = inject();

module.exports = {

  fields: {
    disclosureFormFiledAndServed: {
      id: '#respondent1DQDisclosureReport_disclosureFormFiledAndServed',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    disclosureProposalAgreed: {
      id: '#respondent1DQDisclosureReport_disclosureProposalAgreed',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    draftOrderNumber: '#respondent1DQDisclosureReport_draftOrderNumber',
  },

  async enterDisclosureReport() {
    await within (this.fields.disclosureFormFiledAndServed.id, () => {
      I.click(this.fields.disclosureFormFiledAndServed.options.yes);
    });

    await within (this.fields.disclosureProposalAgreed.id, () => {
      I.click(this.fields.disclosureProposalAgreed.options.yes);
    });

    I.fillField(this.fields.draftOrderNumber, '123456');

    await I.clickContinue();
  }
};
