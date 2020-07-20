const { I } = inject();

module.exports = {

  fields: {
    extensionAccepted: {
      id: '#extensionAccepted',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    }
  },

  async selectDoNotAccept () {
    await within(this.fields.extensionAccepted.id, () => {
      I.click(this.fields.extensionAccepted.options.no);
    });

    await I.clickContinue();
  }
};

