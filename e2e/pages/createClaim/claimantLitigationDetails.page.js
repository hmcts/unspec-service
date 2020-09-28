const {I} = inject();
const postcodeLookup = require('../../fragments/addressPostcodeLookup');

module.exports = {

  fields: {
    childClaimant: {
      id: '#claimantLitigationFriend_required',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    litigationFriendName: '#claimantLitigationFriend_fullName',
    litigantInFriendDifferentAddress: {
      id: '#claimantLitigationFriend_differentAddress',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    litigantInFriendAddress: '#claimantLitigationFriend_address_address'
  },

  async enterLitigantFriendWithDifferentAddressToClaimant(address) {
    I.waitForElement(this.fields.childClaimant.id);
    await within(this.fields.childClaimant.id, () => {
      I.click(this.fields.childClaimant.options.yes);
    });

    I.fillField(this.fields.litigationFriendName, 'John Smith');

    await within(this.fields.litigantInFriendDifferentAddress.id, () => {
      I.click(this.fields.litigantInFriendDifferentAddress.options.no);
    });

    await within(this.fields.litigantInFriendAddress, () => {
      postcodeLookup.enterAddressManually(address);
    });

    await I.clickContinue();
  }
};

