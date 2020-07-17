const { I } = inject();

module.exports = {

  fields: {
    extensionProposedDeadline: {
      day: '#extensionProposedDeadline-day',
      month: '#extensionProposedDeadline-month',
      year: '#extensionProposedDeadline-year',
    }
  },

  async enterExtensionProposedDeadline () {
    const proposedDeadline = new Date();
    proposedDeadline.setDate(proposedDeadline.getDate() + 28);
    I.fillField(this.fields.extensionProposedDeadline.day, proposedDeadline.getDate());
    I.fillField(this.fields.extensionProposedDeadline.month, proposedDeadline.getMonth() + 1);
    I.fillField(this.fields.extensionProposedDeadline.year, proposedDeadline.getFullYear());

    await I.clickContinue();
  }
};

