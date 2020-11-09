const {I} = inject();

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

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
      console.log((new Date()).toISOString());
      await sleep(5000);
      await I.goToCase(caseId);
    }, locate('option').withText(event), 20);

    I.selectOption(this.fields.eventDropdown, event);
    I.click(this.goButton);
  }
};
