const {I} = inject();

module.exports = {

  fields: {
    futureApplications: {
      id: '#respondent1DQFurtherInformation_futureApplications',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    reasonForFutureApplications: '#respondent1DQFurtherInformation_reasonForFutureApplications',
    otherInformationForJudge: '#respondent1DQFurtherInformation_otherInformationForJudge',
  },

  async enterFurtherInformation() {
    await within (this.fields.futureApplications.id, () => {
      I.click(this.fields.futureApplications.options.yes);
    });
    I.fillField(this.fields.reasonForFutureApplications, 'Reason for future applications');
    I.fillField(this.fields.otherInformationForJudge, 'Other information for judge');
    await I.clickContinue();
  },
};
