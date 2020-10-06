const {I} = inject();

module.exports = {

  fields: {
    explainedToClient: {
      id: '#respondent1DQFileDirectionsQuestionnaire_explainedToClient',
      options: {
        confirm: 'I confirm I\'ve explained to my client that they myst try to settle, the available options, and the possibility of costs sanctions if they refuse.'
      }
    },
    oneMonthStay: {
      id: '#respondent1DQFileDirectionsQuestionnaire_oneMonthStayRequested',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    reactionProtocolCompliedWith: {
      id: '#respondent1DQFileDirectionsQuestionnaire_reactionProtocolCompliedWith',
      options: {
        yes: 'Yes',
        no: 'No'
      }
    },
    reactionProtocolNotCompliedWithReason: '#respondent1DQFileDirectionsQuestionnaire_reactionProtocolNotCompliedWithReason',
  },

  async fileDirectionsQuestionnaire() {
    I.waitForElement(this.fields.explainedToClient.id);
    await within (this.fields.explainedToClient.id, () => {
      I.click(this.fields.explainedToClient.options.confirm);
    });
    await within (this.fields.oneMonthStay.id, () => {
      I.click(this.fields.oneMonthStay.options.no);
    });

    await within (this.fields.reactionProtocolCompliedWith.id, () => {
      I.click(this.fields.reactionProtocolCompliedWith.options.no);
    });

    I.fillField(this.fields.reactionProtocolNotCompliedWithReason, 'Reason for not complying');

    await I.clickContinue();
  }
};
