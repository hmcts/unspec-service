const {I} = inject();
const config = require('../config.js');

const totp = require('totp-generator');
const fetch = require('node-fetch');
const HttpProxyAgent = require('http-proxy-agent');

const PROXY_AGENT = config.proxyServer ? new HttpProxyAgent(`http://${config.proxyServer}`) : null;

module.exports = {
  getApiData: async () => {
    const authCookie = await I.grabCookie('__auth__');
    const userIdCookie = await I.grabCookie('__userid__');

    const s2sTokenRequestBody = {
      microservice: 'unspec_service',
      oneTimePassword: totp(config.s2sSecret)
    };

    const s2sToken = await fetch(`${config.serviceAuthProviderUrl}/lease`, {
      method: 'POST',
      body: JSON.stringify(s2sTokenRequestBody),
      headers: {'Content-Type': 'application/json'},
      agent: PROXY_AGENT
    }).then(response => response.text());

    return {
      authToken: authCookie.value,
      userId: userIdCookie.value,
      s2sToken: s2sToken,
    };
  },

  getCreateClaimToken: async apiData => {
    return fetch(`${config.ccdDataStoreUrl}/caseworkers/${apiData.userId}/jurisdictions/CIVIL/case-types/UNSPECIFIED_CLAIMS/event-triggers/CREATE_CLAIM/token`, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + apiData.authToken,
        'ServiceAuthorization': apiData.s2sToken
      },
      agent: PROXY_AGENT
    }).then(response => response.json()).then(data => data.token);
  },

  validate: async (apiData, pageId, caseData) => {
    return fetch(`${config.ccdDataStoreUrl}/caseworkers/${apiData.userId}/jurisdictions/CIVIL/case-types/UNSPECIFIED_CLAIMS/validate?pageId=${pageId}`, {
      method: 'POST',
      body: JSON.stringify({
        data: caseData,
        event: {
          id: 'CREATE_CLAIM',
          summary: '',
          description: ''
        },
        event_token: apiData.ccdEventToken,
        ignore_warning: false
      }),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiData.authToken}`,
        'ServiceAuthorization': apiData.s2sToken
      },
      agent: PROXY_AGENT
    });
  },

  createClaim: async (apiData, caseData) => {
    return fetch(`${config.ccdDataStoreUrl}/caseworkers/${apiData.userId}/jurisdictions/CIVIL/case-types/UNSPECIFIED_CLAIMS/cases`, {
      method: 'POST',
      body: JSON.stringify({
        data: caseData,
        event: {
          id: 'CREATE_CLAIM',
          summary: '',
          description: ''
        },
        event_token: apiData.ccdEventToken,
        ignore_warning: false,
        draft_id: null
      }),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiData.authToken}`,
        'ServiceAuthorization': apiData.s2sToken
      },
      agent: PROXY_AGENT
    });
  }
}
