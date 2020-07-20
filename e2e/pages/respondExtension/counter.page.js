const {I} = inject();

const date = require('../../fragments/date');

module.exports = {

  fields: {
    counterDate: {
      id: '#extensionCounter',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    newDeadline: {
      id: '#extensionProposedDeadline',
    }
  },

  async enterCounterDate() {
    await within(this.fields.counterDate.id, () => {
      I.click(this.fields.counterDate.options.yes);
    });

    await date.enterDate(this.fields.newDeadline.id);
    await I.clickContinue();
  }
};

