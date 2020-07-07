const {I} = inject();

module.exports = {

  fields: {
    solicitorReference: {css: 'input[id=solicitorReferences_solicitorReference]'},
    defendantReference: '#solicitorReferences_defendantReference',
  },

  async enterReferences() {
    I.waitForElement({css: 'input[id=solicitorReferences_solicitorReference]'});
    I.fillField(this.fields.solicitorReference, 'Solicitor Reference');
    I.fillField(this.fields.defendantReference, 'Defendant Reference');
    await I.clickContinue();
  }
};

