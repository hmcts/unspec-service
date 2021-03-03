const config = require('../config.js');
const {waitForFinishedBusinessProcess, updateCaseData} = require('../api/testingSupport');
const {dateTime} = require('../api/dataHelper');

const getCaseId = caseNumber => `${caseNumber.split('-').join('').replace(/#/, '')}`;

Feature('RPA Handoff points tests @rpa-handoff-tests');

Scenario('Take claim offline', async (I) => {
  await I.login(config.solicitorUser);
  await I.assertNoEventsAvailable();

});

Scenario('Defendant - Litigant In Person', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase(true);
  await I.assertNoEventsAvailable();
});

Scenario('Defendant - Defend part of Claim', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();
  await I.notifyClaim();
  await I.notifyClaimDetails();
  await I.acknowledgeService('PART');
  await I.respondToClaim('PART');
});

Scenario('Defendant - Defends, Claimant decides not to proceed', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();
  await I.notifyClaim();
  await I.notifyClaimDetails();
  await I.acknowledgeService();
  await I.respondToClaim();
  await I.respondToDefenceDropClaim();
  await I.assertNoEventsAvailable();
});

Scenario('Claimant does not respond to defence with defined timescale', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();
  let caseId = getCaseId(await I.grabCaseNumber());
  await I.notifyClaim();
  await I.notifyClaimDetails();
  await I.acknowledgeService('PART');
  await I.respondToClaim('FULL');

  await waitForFinishedBusinessProcess(caseId)
  await updateCaseData(caseId, {applicantSolicitorSecondResponseDeadlineToRespondentSolicitor1: dateTime(-1)})

  console.log('Start waiting for Case strikeout scheduler ' + dateTime())
  // 5 min sleep waiting for Case strikeout scheduler
  await sleep(300);
  console.log('Waiting finished ' + dateTime())
  await I.assertNoEventsAvailable();
});

function sleep(seconds) {
  return new Promise(resolve => setTimeout(resolve, seconds * 1000));
}
