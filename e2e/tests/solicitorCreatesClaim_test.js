const config = require('../config.js');

let caseNumber;
const caseEventMessage = eventName => `Case ${caseNumber} has been updated with event: ${eventName}`;

Feature('Claim creation @claim-tests');

Scenario('Solicitor creates claim @create-claim', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();

  caseNumber = await I.grabCaseNumber();
  await I.see(`Case ${caseNumber.split('-').join('')} has been created.`);
});

xScenario('Solicitor confirms service', async (I) => {
  await I.confirmService();
  await I.see(caseEventMessage('Confirm service'));
});

xScenario('Solicitor acknowledges service', async (I) => {
  await I.acknowledgeService();
  await I.see(caseEventMessage('Acknowledge service'));
});

xScenario('Solicitor requests extension', async (I) => {
  await I.requestExtension();
  await I.see(caseEventMessage('Request extension'));
});

xScenario('Solicitor reponds to extension request', async (I) => {
  await I.respondToExtension();
  await I.see(caseEventMessage('Respond to extension request'));
});

xScenario('Solicitor responds to claim', async (I) => {
  await I.respondToClaim();
  await I.see(caseEventMessage('Respond to claim'));
});

xScenario('Solicitor responds to defence', async (I) => {
  await I.respondToDefence();
  await I.see(caseEventMessage('View and respond to defence'));
});

Scenario('Solicitor adds defendant litigation friend', async (I) => {
  await I.addDefendantLitigationFriend();
  await I.see(caseEventMessage('Add litigation friend'));
});
