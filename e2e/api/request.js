const config = require('../config.js');

const restHelper = require('../api/restHelper.js');
const totp = require('totp-generator');

module.exports = {
  getTokens: async (user) => {
    const authToken = await restHelper.request(
      `${config.idamApiUrl}/loginUser?username=${user.email}&password=${user.password}`,
      {'Content-Type': 'application/x-www-form-urlencoded'})
      .then(response => response.json()).then(data => data.access_token);

    const userId = await restHelper.request(
      `${config.idamApiUrl}/o/userinfo`,
      {
        'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: `Bearer ${authToken}`
      })
      .then(response => response.json()).then(data => data.uid);

    const s2sToken = await restHelper.request(
      `${config.s2s.authProviderUrl}/lease`,
      {'Content-Type': 'application/json'},
      {
        microservice: config.s2s.microservice,
        oneTimePassword: totp(config.s2s.secret)
      })
      .then(response => response.text());

    return {
      userAuth: authToken,
      userId: userId,
      s2sAuth: s2sToken,
    };
  },

  getCreateClaimToken: async tokens => {
    return restHelper.request(`${config.ccdDataStoreUrl}/caseworkers/${tokens.userId}/jurisdictions/CIVIL/`
      + 'case-types/UNSPECIFIED_CLAIMS/event-triggers/CREATE_CLAIM/token',
      {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${tokens.userAuth}`,
        'ServiceAuthorization': tokens.s2sAuth
      }, null, 'GET')
      .then(response => response.json()).then(data => data.token);
  },

  validate: async (tokens, pageId, caseData) => {
    return restHelper.request(
      `${config.ccdDataStoreUrl}/caseworkers/${tokens.userId}/jurisdictions/CIVIL/case-types/UNSPECIFIED_CLAIMS/validate?pageId=${pageId}`,
      {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${tokens.userAuth}`,
        'ServiceAuthorization': tokens.s2sAuth
      },
      {
        data: caseData,
        event: {
          id: 'CREATE_CLAIM',
          summary: '',
          description: ''
        },
        event_token: tokens.ccdEvent,
        ignore_warning: false
      }
    );
  },

  createClaim: async (tokens, caseData) => {
    return restHelper.request(
      `${config.ccdDataStoreUrl}/caseworkers/${tokens.userId}/jurisdictions/CIVIL/case-types/UNSPECIFIED_CLAIMS/cases`,
      {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${tokens.userAuth}`,
        'ServiceAuthorization': tokens.s2sAuth
      },
      {
        data: caseData,
        event: {
          id: 'CREATE_CLAIM',
          summary: '',
          description: ''
        },
        event_token: tokens.ccdEvent,
        ignore_warning: false,
        draft_id: null
      });
  }
};
