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
});

Scenario('Solicitor acknowledges service', async (I) => {
  await I.acknowledgeService();
  await I.see(caseEventMessage('Acknowledge service'));
});

Scenario('Case proceeds in Caseman', async (I) => {
  await I.caseProceedsInCaseman();
});
