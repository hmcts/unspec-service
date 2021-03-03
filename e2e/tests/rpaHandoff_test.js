const config = require('../config.js');

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
