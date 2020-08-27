const {I} = inject();

module.exports = {

  fields: {
    courtChoice: {
      id: '#applicantSolicitor1PreferredCourtLocation_option',
      options: {
        liverpool: 'Liverpool',
        birkenhead: 'Birkenhead',
        other: 'Other'
      }
    },
    courtLocation: '#applicantSolicitor1PreferredCourtLocation_courtName'
  },

  async enterCourt() {
    I.waitForElement(this.fields.courtChoice.id);
    await within(this.fields.courtChoice.id, () => {
      I.click(this.fields.courtChoice.options.other);
    });
    I.fillField(this.fields.courtLocation, 'London High Court');
    await I.clickContinue();
  }
};

