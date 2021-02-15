const config = require('../config.js');
//const {waitForFinishedBusinessProcess} = require('../api/testingSupport');

const caseEventMessage = eventName => `Case ${caseNumber} has been updated with event: ${eventName}`;
const caseId = () => `${caseNumber.split('-').join('').replace(/#/, '')}`;

//const CASE_HEADER = 'ccd-case-header > h1';
//const CASE_LIST = 'exui-case-list';

let caseNumber;

Feature('Claim creation @rpa-handoff-tests');

Scenario('Take claim offline @rpa-handoff', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();

  caseNumber = await I.grabCaseNumber();
  await I.see(`Case #${caseId()} has been created.`);

  await I.notifyClaim();

  await I.acknowledgeService();
  await I.see(caseEventMessage('Acknowledge service'));
  await I.caseProceedsInCaseman();
});

Scenario('Defendant - Litigant In Person @rpa-handoff', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase(true);

  caseNumber = await I.grabCaseNumber();
  await I.see(`Case #${caseId()} has been created.`);
});

Scenario('Defendant - Defend part of Claim @rpa-handoff', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();

  caseNumber = await I.grabCaseNumber();
  await I.see(`Case #${caseId()} has been created.`);

  await I.notifyClaim();

  await I.acknowledgeService('PART');
  await I.see(caseEventMessage('Acknowledge service'));

  await I.respondToClaim('PART');
});

Scenario('Defendant - Defends, Claimant decides not to proceed @rpa-handoff', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();

  caseNumber = await I.grabCaseNumber();
  await I.see(`Case #${caseId()} has been created.`);

  await I.notifyClaim();

  await I.see(caseEventMessage('Notify claim'));

  await I.acknowledgeService();
  await I.see(caseEventMessage('Acknowledge service'));

  await I.respondToClaim();

  await I.respondToDefenceDropClaim();

});
