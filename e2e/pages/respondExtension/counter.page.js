const {I} = inject();

const date = require('../../fragments/date');

module.exports = {

  fields: {
    extensionCounter: {
      id: '#extensionCounter',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    extensionCounterDate: {
      id: 'extensionCounterDate',
    }
  },

  async enterCounterDate() {
    await within(this.fields.extensionCounter.id, () => {
      I.click(this.fields.extensionCounter.options.yes);
    });

    await date.enterDate(this.fields.extensionCounterDate.id);
    await I.clickContinue();
  }
};

