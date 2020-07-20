const { I } = inject();

module.exports = {

  fields: {
    extensionProposedDeadline: {
      day: '#defendantSolicitor1claimResponseExtensionProposedDeadline-day',
      month: '#defendantSolicitor1claimResponseExtensionProposedDeadline-month',
      year: '#defendantSolicitor1claimResponseExtensionProposedDeadline-year'
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

