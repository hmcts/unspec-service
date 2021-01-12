const config = require('../config.js');
const idamHelper = require('./idamHelper');
const restHelper = require('./restHelper');

const {retry} = require('./retryHelper');
let incidentMessage;

const MAX_RETRIES = 300;
const RETRY_TIMEOUT_MS = 1000;

module.exports =  {
  waitForFinishedBusinessProcess: async caseId => {
    const authToken = await idamHelper.accessToken(config.solicitorUser);

    await retry(() => {
      return restHelper.request(
        `${config.url.unspecService}/testing-support/case/${caseId}/business-process`,
        {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authToken}`,
        }, null, 'GET')
        .then(async response => await response.json()).then(response => {
          let businessProcess = response.businessProcess;
          if (response.incidentMessage) {
            incidentMessage = response.incidentMessage;
          } else if (businessProcess.status !== 'FINISHED') {
            throw new Error(`Ongoing business process: ${businessProcess.camundaEvent}, case id: ${caseId}, status: ${businessProcess.status},`
              + ` process instance: ${businessProcess.processInstanceId}, last finished activity: ${businessProcess.activityId}`);
          }
      });
    }, MAX_RETRIES, RETRY_TIMEOUT_MS);
    if (incidentMessage)
      throw new Error(`Business process failed for case: ${caseId}, incident message: ${incidentMessage}`);
  },
  assignCaseToDefendant: async caseId => {
    const authToken = await idamHelper.accessToken(config.defendantSolicitorUser);
    const userId = await idamHelper.userId(authToken);

    await retry(() => {
      return restHelper.request(
        `${config.url.unspecService}/testing-support/assignCase`,
        { 'Content-Type': 'application/json' },
        {
          "caseId":`${caseId}`,
          "userId": `${userId}`,
          "caseRoles": ["[\"[RESPONDENTSOLICITORONE]\"]"]
        },
        'POST')
        .then( response =>  response._status)
        .then(status => {
          if (status === 200) {
            console.log( "Role created successfully");
          } else if (status === 409) {
            console.log("Role already exists!");
          } else  {
            throw new Error(`Error occurred with status": ${status}`);
          }
        });
    }, MAX_RETRIES, RETRY_TIMEOUT_MS);
  }
};
