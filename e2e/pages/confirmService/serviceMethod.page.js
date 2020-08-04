const {I} = inject();

module.exports = {

  fields: {
    serviceMethod: {
      type: {
        id: '#serviceMethod_type',
        options: {
          post: 'First class post',
          dx: 'Document exchange',
          fax: 'Fax',
          email: 'Email',
          other: 'Other'
        }
      }
    }
  },

  async selectPostMethod() {
    I.waitForElement(this.fields.serviceMethod.type.id);
    await within(this.fields.serviceMethod.type.id, () => {
      I.click(this.fields.serviceMethod.type.options.post);
    });

    await I.clickContinue();
  }
};

