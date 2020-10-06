const {I} = inject();
const date = require('../../fragments/date');

module.exports = {

  fields: {
    hearingLength: {
      id: '#respondent1DQHearing_hearingLength',
      options: {
        lessThanOneDay: 'Less than a day',
        oneDay: 'One day',
        moreThanOneDay: 'More than a day',
      }
    },
    hearingLengthHours: '#respondent1DQHearing_hearingLengthHours',
    hearingLengthDays: '#respondent1DQHearing_hearingLengthDays',
    unavailableDatesRequired: {
      id: '#respondent1DQHearing_unavailableDatesRequired',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    unavailableDates: {
      id: '#respondent1DQHearing_unavailableDates',
      element: {
        who: '#respondent1DQHearing_unavailableDates_0_who',
        date: '#respondent1DQHearing_unavailableDates_0_date',
      }
    },
  },

  async enterHearingInformation() {
    await within (this.fields.hearingLength.id, () => {
      I.click(this.fields.hearingLength.options.lessThanOneDay);
    });

    I.fillField(this.fields.hearingLengthHours, '5');
    await within (this.fields.unavailableDatesRequired.id, () => {
      I.click(this.fields.unavailableDatesRequired.options.yes);
    });

    await this.addUnavailableDates();
    await I.clickContinue();
  },

  async addUnavailableDates() {
    await I.addAnotherElementToCollection();
    I.fillField(this.fields.unavailableDates.element.who, 'John Smith');
    await date.enterDate(this.fields.unavailableDates.element.date);
  },
};
