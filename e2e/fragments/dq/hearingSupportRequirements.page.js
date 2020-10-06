const {I} = inject();

module.exports = {

  fields: {
    requirements: {
      id: '#respondent1DQHearingSupport_requirements',
      options: {
        disabledAccess: 'Disabled access',
        hearingLoop: 'Hearing loop',
        signLanguage: 'Sign language interpreter',
        languageInterpreter: 'Language interpreter',
        other: 'Other support'
      }
    },
    signLanguageRequired: '#respondent1DQHearingSupport_signLanguageRequired',
    languageToBeInterpreted: '#respondent1DQHearingSupport_languageToBeInterpreted',
    otherSupport: '#respondent1DQHearingSupport_otherSupport',
  },

  async selectRequirements() {
    await within(this.fields.requirements.id, () => {
      I.click(this.fields.requirements.options.signLanguage);
      I.click(this.fields.requirements.options.languageInterpreter);
      I.click(this.fields.requirements.options.other);
    });

    I.fillField(this.fields.signLanguageRequired, 'A language');
    I.fillField(this.fields.languageToBeInterpreted, 'A language');
    I.fillField(this.fields.otherSupport, 'Some support');
    await I.clickContinue();
  },
};
