const config = require('../config.js');

let caseNumber;
const caseEventMessage = eventName => `Case ${caseNumber} has been updated with event: ${eventName}`;

Feature('Claim creation');

Scenario('Solicitor creates claim @create-claim', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();

  caseNumber = await I.grabCaseNumber();
  await I.see(`Case ${caseNumber.split('-').join('')} has been created.`);
});

Scenario('Solicitor confirms service', async (I) => {
  await I.confirmService();
  await I.see(caseEventMessage('Confirm service'));
});

Scenario('Solicitor requests extension', async (I) => {
  await I.requestExtension();
  await I.see(caseEventMessage('Request extension'));
});

Scenario('Solicitor reponds to extension request', async (I) => {
  await I.respondToExtension();
  await I.see(caseEventMessage('Respond to extension request'));
});
