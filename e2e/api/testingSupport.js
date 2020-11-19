const config = require('../config.js');
const restHelper = require('./restHelper');

const fetch = require('node-fetch');
const {retry} = require('./retryHelper');

module.exports =  {
  waitForFinishedBusinessProcess: async caseId => {
    const authToken = await restHelper.request(
      `${config.url.idamApi}/loginUser?username=${config.solicitorUser.email}&password=${config.solicitorUser.password}`,
      {'Content-Type': 'application/x-www-form-urlencoded'})
      .then(response => response.json()).then(data => data.access_token);

    await retry(() => {
      return fetch(`${config.url.unspecService}/testing-support/case/${caseId}/business-process/status`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authToken}`,
        },
      }).then(async response => await response.text()).then(status => {
        if (status !== 'FINISHED') {
          throw new Error('Ongoing business process, status: ' + status);
        }
      });
    });
  }
};
