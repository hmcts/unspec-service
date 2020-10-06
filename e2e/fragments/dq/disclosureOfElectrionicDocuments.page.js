const {I} = inject();

module.exports = {

  fields: {
    reachedAgreement: {
      id: '#respondent1DQDisclosureOfElectronicDocuments_reachedAgreement',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    agreementLikely: {
      id: '#respondent1DQDisclosureOfElectronicDocuments_agreementLikely',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    reasonForNoAgreement: '#respondent1DQDisclosureOfElectronicDocuments_reasonForNoAgreement',
  },

  async enterDisclosureOfElectronicDocuments() {
    await within (this.fields.reachedAgreement.id, () => {
      I.click(this.fields.reachedAgreement.options.no);
    });

    await within (this.fields.agreementLikely.id, () => {
      I.click(this.fields.agreementLikely.options.no);
    });

    I.fillField(this.fields.reasonForNoAgreement, 'Reason for no agreement');

    await I.clickContinue();
  }
};
