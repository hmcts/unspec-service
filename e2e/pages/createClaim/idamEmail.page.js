const {I} = inject();

module.exports = {

  fields: {
    idamEmailIsCorrect: {
      id: '#applicantSolicitor1IdamEmail_correct',
      options: {
        yes: 'Yes',
        no: 'No'
      },
      newEmail: '#applicantSolicitor1IdamUserDetails_email',
    }
  },

  async enterIdamEmail() {
    I.waitForElement(this.fields.idamEmailIsCorrect.id);
    I.click(this.fields.idamEmailIsCorrect.options.yes);
    await I.clickContinue();
  }
};

