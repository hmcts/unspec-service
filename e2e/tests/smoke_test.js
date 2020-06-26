const user = {
  password: 'Password12',
  email: 'solicitor@example.com'
}

const baseUrl = process.env.URL || 'http://localhost:3333';

Feature('Smoke tests @smoke-tests');

Scenario('Sign in as solicitor user', (I, loginPage) => {
  I.amOnPage(baseUrl);
  loginPage.signIn(user);
  I.see('Case List')
});
