const {I} = inject();

module.exports = {

  fields: {
    solicitorReferences: {
      id: '#solicitorReferences_respondentSolicitor1Reference'
    }
  },

  async confirmReference() {
    I.waitForElement(this.fields.solicitorReferences.id);
    I.fillField(this.fields.solicitorReferences.id, 'Updated Defendant Reference');

    await I.clickContinue();
  }
};
