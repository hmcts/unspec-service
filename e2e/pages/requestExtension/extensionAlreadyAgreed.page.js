const { I } = inject();

module.exports = {

  fields: {
    extensionAlreadyAgreed: {
      id: '#extensionAlreadyAgreed',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    }
  },

  async selectAlreadyAgreed () {
    await within(this.fields.extensionAlreadyAgreed.id, () => {
      I.click(this.fields.extensionAlreadyAgreed.options.yes);
    });

    await I.clickContinue();
  }
};

