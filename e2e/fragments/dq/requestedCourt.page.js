const {I} = inject();

module.exports = {

  fields: {
    requestHearingAtSpecificCourt: {
      id: '#respondent1DQRequestedCourt_requestHearingAtSpecificCourt',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    name: '#respondent1DQRequestedCourt_name',
    reasonForHearingAtSpecificCourt: '#respondent1DQRequestedCourt_reasonForHearingAtSpecificCourt',
  },

  async selectSpecificCourtForHearing() {
    await within(this.fields.requestHearingAtSpecificCourt.id, () => {
      I.click(this.fields.requestHearingAtSpecificCourt.yes);
    });

    I.fillField(this.fields.name, 'A court name');
    I.fillField(this.fields.reasonForHearingAtSpecificCourt, 'A reason for the court');
    await I.clickContinue();
  },
};
