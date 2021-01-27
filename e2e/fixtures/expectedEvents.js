module.exports = {
  CREATED: [
    {
      id: 'REQUEST_EXTENSION',
      name: 'Request extension',
      description: 'Defendant solicitor is submitting requesting for an extension of time',
      order: 2
    },
    {
      id: 'RESPOND_EXTENSION',
      name: 'Respond to extension request',
      description: 'Respondent solicitor is responding to a request for an extension of time',
      order: 3
    },
    {
      id: 'ACKNOWLEDGE_SERVICE',
      name: 'Acknowledge service',
      description: 'Defendant solicitor is acknowledging service',
      order: 5
    },
    {
      id: 'ADD_DEFENDANT_LITIGATION_FRIEND',
      name: 'Add litigation friend',
      description: 'Add litigation friend',
      order: 6
    },
    {
      id: 'DEFENDANT_RESPONSE',
      name: 'Respond to claim',
      description: 'Defendant response to claim',
      order: 7
    },
    {
      id: 'WITHDRAW_CLAIM',
      name: 'Withdraw claim',
      description: 'Withdraw a claim',
      order: 10
    },
    {
      id: 'DISCONTINUE_CLAIM',
      name: 'Discontinue claim',
      description: 'Discontinue a claim',
      order: 11
    }
  ]
};
