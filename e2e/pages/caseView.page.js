const {I} = inject();

module.exports = {

  tabs: {
    history: 'History'
  },
  fields: {
    eventDropdown: '#next-step',
  },
  goButton: 'Go',

  async startEvent(event, caseId) {
    await I.retryUntilExists(async () => {
      await I.goToCase(caseId);
    }, locate('option').withText(event), 10);

    I.selectOption(this.fields.eventDropdown, event);
    I.click(this.goButton);
  }
};
