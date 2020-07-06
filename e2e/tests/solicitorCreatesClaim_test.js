const config = require('../config.js');

Feature('Claim creation @create-claim');

Scenario('Solicitor creates claim', async (I) => {
  await I.login(config.solicitorUser);
  await I.createCase();
});
