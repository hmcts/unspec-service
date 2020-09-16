// const openCaseData = require('../data/openCase');
// const ordersNeededData = require('../data/ordersNeeded');
/* eslint "require-atomic-updates": "off" */
const qs = require('qs');
const config = require('../config');
const otp = require('otp');
const querystring = require('querystring');
const moment = require('moment');
const axiosfix = require('axios-https-proxy-fix');
var assert = require('assert');

const idamHeaders = {'Accept': 'application/json',
  'Authorization': 'Basic',
  'Content-Type': 'application/x-www-form-urlencoded',
};
const s2sHeaders = {'Content-Type': 'application/json'};

const proxy = {host: 'proxyout.reform.hmcts.net',
  port: '8080',
};

const separator = '/';
const solicitorUid = '5bf74c87-c801-493a-b995-d2b750cd2442';
const adminUid = 'e24fb55f-4911-4705-a07e-4f0ed5f62539';
const gatekeeperUid = 'bcf4c729-c96d-48d3-897a-e31a38a46639';

const ccdBaseUrl = 'http://ccd-data-store-api-aat.service.core-compute-aat.internal/';
const jurisdictions = 'CIVIL';
const caseTypes = 'UNSPECIFIED_CLAIMS';

let ccdCaseId;
let accessToken;
let s2sToken;
let ccdToken;
let uid;

module.exports = class RestHelper extends Helper {

  getCaseName(){
    return 'E2E_' + moment().valueOf().toString();
  }

  getHearingBookingStartEndDate() {
    const now = moment();
    const startDate = moment(now.add(10, 'minutes')).format(moment.HTML5_FMT.DATETIME_LOCAL_MS);
    const endDate = moment(now.add(40, 'minutes')).format(moment.HTML5_FMT.DATETIME_LOCAL_MS);
    return ({startDate: startDate, endDate: endDate});
  }

  getToday() {
    return moment().format(moment.HTML5_FMT.DATE);
  }

  async setupTokensForUser(username = undefined, password = undefined) {
    accessToken = await this.getIdamToken(username, password);
    s2sToken = await this.getS2sToken();

    switch (username) {
      case config.solicitorUser.email:
        uid = solicitorUid;
        break;
      case config.hmctsAdminUser.email:
        uid = adminUid;
        break;
      case config.gateKeeperUser.email:
        uid = gatekeeperUid;
        break;
    }

  }

  async getIdamToken(username, password) {
    const data = qs.stringify({grant_type: config.idam.grant_type,
      redirect_url: config.idam.redirect_url,
      scope: config.idam.scope,
      username: username,
      password: password,
      client_id: config.idam.client_id,
      client_secret: config.idam.client_secret,
    });

    // THIS NO LONGER WORKS :-(
    // const response = await this.helpers['REST']._executeRequest({
    //   method: 'post',
    //   url:'https://idam-api.aat.platform.hmcts.net/o/token',
    //   data: data,
    //   headers: idamHeaders,
    //   proxy: proxy,
    // });


    // THIS WORKS!!!
    const response = await axiosfix.request({
        method: 'post',
        url:'https://idam-api.aat.platform.hmcts.net/o/token',
        data: data,
        headers: idamHeaders,
        proxy: proxy
    });

    return response.data.access_token;
  }

  async getS2sToken(){
    const otpw = otp({secret: 'E6HOVNG5ZOTF6XZP'}).totp();
    const data = {microservice: config.rpe.service_id,
      oneTimePassword: otpw,
    };

    const response = await this.helpers['REST']._executeRequest({
      method: 'post',
      url:'http://rpe-service-auth-provider-aat.service.core-compute-aat.internal/lease',
      data: data,
      headers: s2sHeaders,
      proxy: proxy,
    });

    return response.data;
  }

  async getCcdToken(eventId, ccdCaseId, accessToken, s2sToken, uid){
    let url;
    const urlNoCcdCaseId = {caseworkers: uid,
      jurisdictions: jurisdictions,
      'case-types': caseTypes,
      'event-triggers': eventId,
    };

    const urlCcdCaseId = {caseworkers: uid,
      jurisdictions: jurisdictions,
      'case-types': caseTypes,
      cases: ccdCaseId,
      'event-triggers': eventId,
    };

    url = (eventId === 'CREATE_CLAIM') ? urlNoCcdCaseId : urlCcdCaseId;
    url = querystring.stringify(url, separator, separator) + '/token';

    let ccdHeaders = {'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + accessToken,
      'ServiceAuthorization': s2sToken,
    };

    const response = await this.helpers['REST']._executeRequest({
      method: 'get',
      baseURL: ccdBaseUrl,
      url: url,
      headers: ccdHeaders,
      proxy: proxy,
    });

    return response.data.token;
  }


  async createEvent(eventId) {
    let url;
    // console.log('eventId>>>', eventId);
    // console.log('ccdCaseId>>>', ccdCaseId);
    // console.log('accessToken>>>', accessToken);
    // console.log('s2sToken>>>', s2sToken);
    // console.log('uid>>>', uid);

    ccdToken = await this.getCcdToken(eventId, ccdCaseId, accessToken, s2sToken, uid);
  }

  async validateData(pageId, dataToValidate) {
    let url = {caseworkers: uid,
      jurisdictions: jurisdictions,
      'case-types': caseTypes,
    };

    url = querystring.stringify(url, separator, separator) + '/validate?pageId=' + pageId;

    let ccdHeaders = {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + accessToken,
      'ServiceAuthorization': s2sToken,
    };

    dataToValidate.event_token = ccdToken;
    dataToValidate.case_reference = ccdCaseId;

    const response = await this.helpers['REST']._executeRequest({
      method: 'post',
      baseURL: ccdBaseUrl,
      url: url,
      headers: ccdHeaders,
      proxy: proxy,
      data: dataToValidate,
    });

    assert.deepStrictEqual(response.status, 200);
  }

  async submitEvent(eventData){
    let urlCcdIdNotExists = {caseworkers: uid,
      jurisdictions: jurisdictions,
      'case-types': caseTypes,
    };

    let urlCcdIdExists = {caseworkers: uid,
      jurisdictions: jurisdictions,
      'case-types': caseTypes,
      'cases': ccdCaseId
    };

    let url = ccdCaseId ? querystring.stringify(urlCcdIdExists, separator, separator) + '/events' : querystring.stringify(urlCcdIdNotExists, separator, separator) + '/cases';

    let ccdHeaders = {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + accessToken,
      'ServiceAuthorization': s2sToken,
    };

    eventData.event_token = ccdToken;
    eventData.case_reference = ccdCaseId;

    const response = await this.helpers['REST']._executeRequest({
      method: 'post',
      baseURL: ccdBaseUrl,
      url: url,
      headers: ccdHeaders,
      proxy: proxy,
      data: eventData,
    });

    assert.deepStrictEqual(response.status, 201);

    ccdCaseId = response.data.id;

    console.log('ccdCaseId>>>', ccdCaseId);
  }

  // async getIdamToken() {
  //   // const response = await this.helpers['REST']._executeRequest({url:'http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=9d7b5f07bfaa957d02bbdf1bdb1d49b2'})
  //   // console.log('REST>>>>>',await this.helpers['REST']._executeRequest({url:'http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=9d7b5f07bfaa957d02bbdf1bdb1d49b2'}));
  //   const response = await this.helpers['REST']._executeRequest({
  //     method: 'post',
  //     url:'https://idam-api.aat.platform.hmcts.net/o/token',
  //     data: qs.stringify({ grant_type: 'password',
  //       redirect_url: 'https://fpl-case-service-aat.service.core-compute-aat.internal/oauth2/callback',
  //       scope: 'openid profile roles',
  //       username: 'Kurt@swansea.gov.uk',
  //       password: 'Password12',
  //       client_id: 'fpl_case_service',
  //       client_secret: 'nDDkt~;4P>YR-.k.',
  //     }),
  //     headers: { 'Accept': 'application/json',
  //       'Authorization': 'Basic',
  //       'Content-Type': 'application/x-www-form-urlencoded',
  //     },
  //     proxy: { host: 'proxyout.reform.hmcts.net',
  //       port: '8080',
  //     },
  //   });
  //
  //   console.log('access_token>>>>', response.data.access_token);
  //   console.log('==============================');
  //   console.log('==============================');
  //   console.log('==============================');
  //   console.log('==============================');
  //   console.log('==============================');
  //   console.log('response>>>>', response.status);
  //   //console.log('REST>>>>>',await this.helpers['REST']._executeRequest({url:'http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=9d7b5f07bfaa957d02bbdf1bdb1d49b2'}));
  //
  //   console.log('end');
  //   return null;
  // }

};
