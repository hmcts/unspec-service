const {I} = inject();
const config = require('../config.js');

const totp = require('totp-generator');
const fetch = require('node-fetch');
const HttpProxyAgent = require('http-proxy-agent');
const HttpsProxyAgent = require('https-proxy-agent');

const PROXY_AGENT = isHttp => config.proxyServer ? isHttp ? new HttpProxyAgent(`http://${config.proxyServer}`) : new HttpsProxyAgent(`http://${config.proxyServer}`) : null;

module.exports = {
  getApiData: async () => {
    const authToken = await I.grabCookie('__auth__').then(cookie => cookie.value);

    const userId = await fetch(`${config.idamApiUrl}/o/userinfo`, {
      method: 'POST',
      body: {},
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        Authorization: `Bearer ${authToken}`
      },
      agent: PROXY_AGENT(false)
    }).then(response => response.json()).then(data => data.uid);

    const s2sTokenRequestBody = {
      microservice: config.s2s.microservice,
      oneTimePassword: totp(config.s2s.secret)
    };

    const s2sToken = await fetch(`${config.s2s.authProviderUrl}/lease`, {
      method: 'POST',
      body: JSON.stringify(s2sTokenRequestBody),
      headers: {'Content-Type': 'application/json'},
      agent: PROXY_AGENT(true)
    }).then(response => response.text());

    return {
      authToken: authToken,
      userId: userId,
      s2sToken: s2sToken,
    };
  },

  getCreateClaimToken: async apiData => {
    return fetch(`${config.ccdDataStoreUrl}/caseworkers/${apiData.userId}/jurisdictions/CIVIL/`
    + 'case-types/UNSPECIFIED_CLAIMS/event-triggers/CREATE_CLAIM/token', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + apiData.authToken,
        'ServiceAuthorization': apiData.s2sToken
      },
      agent: PROXY_AGENT(true)
    }).then(response => response.json()).then(data => data.token);
  },

  validate: async (apiData, pageId, caseData) => {
    return fetch(`${config.ccdDataStoreUrl}/caseworkers/${apiData.userId}/jurisdictions/CIVIL/`
    + `case-types/UNSPECIFIED_CLAIMS/validate?pageId=${pageId}`, {
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
      agent: PROXY_AGENT(true)
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
      agent: PROXY_AGENT(true)
    });
  }
};
