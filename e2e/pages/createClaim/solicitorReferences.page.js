const {I} = inject();

module.exports = {

  fields: {
    applicantReference: '#solicitorReferences_applicantSolicitor1Reference',
    defendantReference: '#solicitorReferences_respondentSolicitor1Reference',
  },

  async enterReferences() {
    I.waitForElement(this.fields.applicantReference);
    I.fillField(this.fields.applicantReference, 'Applicant Reference');
    I.fillField(this.fields.defendantReference, 'Defendant Reference');
    await I.clickContinue();
  }
};

