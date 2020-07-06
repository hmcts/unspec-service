const config = require('../config.js');

Feature('Smoke tests @smoke-tests');

Scenario('Sign in as solicitor user', async (I, loginPage) => {
  I.amOnPage(baseUrl);
  loginPage.signIn(config.solicitorUser);
  await I.see('Case List');
});
