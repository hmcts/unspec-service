const { I } = inject();

const address = require('./fixtures/address.js');
const postcodeLookup = require('./addressPostcodeLookup');

module.exports = {

  fields: {
    organisationName: '#respondentSolicitor1OrganisationDetails_organisationName',
    phoneNumber: '#respondentSolicitor1OrganisationDetails_phoneNumber',
    emailAddress: '#respondentSolicitor1OrganisationDetails_email',
    dx: '#respondentSolicitor1OrganisationDetails_dx',
    fax: '#respondentSolicitor1OrganisationDetails_fax',
    address: '#respondentSolicitor1OrganisationDetails_address'
  },

  async enterOrganisationDetails () {
    I.waitForElement(this.fields.organisationName);
    I.fillField(this.fields.organisationName, 'Civil Org limited');
    I.fillField(this.fields.phoneNumber, '02425698765');
    I.fillField(this.fields.emailAddress, 'civilunspecified@gmail.com');
    I.fillField(this.fields.dx, 'DX1234');
    I.fillField(this.fields.fax, '02425698765');
    await within(this.fields.address, () => {
      postcodeLookup.enterAddressManually(address);
    });
    await I.clickContinue();
  }
};

