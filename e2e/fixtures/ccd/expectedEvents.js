const events =  require('./events.js');

module.exports = {
  AWAITING_CASE_NOTIFICATION: [
    events.NOTIFY_DEFENDANT_OF_CLAIM,
    events.ADD_DEFENDANT_LITIGATION_FRIEND
  ],
  CREATED: [
    events.REQUEST_EXTENSION,
    events.RESPOND_EXTENSION,
    events.ACKNOWLEDGE_SERVICE,
    events.ADD_DEFENDANT_LITIGATION_FRIEND,
    events.DEFENDANT_RESPONSE,
    events.CASE_PROCEEDS_IN_CASEMAN
  ],
  PROCEEDS_WITH_OFFLINE_JOURNEY: [],
  AWAITING_CLAIMANT_INTENTION: [
    events.ADD_DEFENDANT_LITIGATION_FRIEND,
    events.CLAIMANT_RESPONSE,
    events.CASE_PROCEEDS_IN_CASEMAN
  ]
};
