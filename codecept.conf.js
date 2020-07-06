/* global process */

exports.config = {
  tests: './e2e/tests/*_test.js',
  output: './output',
  helpers: {
    Puppeteer: {
      show: true || false,
      windowSize: '1200x900',
      waitForTimeout: 20000,
      waitForAction: 1000,
      waitForNavigation: [ "domcontentloaded", "networkidle0" ],
      chrome: {
        ignoreHTTPSErrors: true,
        args: [
          '--no-sandbox',
          '--proxy-server=proxyout.reform.hmcts.net:8080',
          '--proxy-bypass-list=*beta*LB.reform.hmcts.net',
          '--window-size=1440,1400'
        ]
      },
    },
    PuppeteerHelpers: {
      require: './e2e/helpers/puppeteer_helper.js',
    },
  },
  include: {
    I: './e2e/steps_file.js'
  },
  mocha: {
    reporterOptions: {
      'codeceptjs-cli-reporter': {
        stdout: '-',
        options: {
          steps: false,
        },
      },
      'mocha-junit-reporter': {
        stdout: '-',
        options: {
          mochaFile: 'test-results/result.xml',
        },
      },
      'mochawesome': {
        stdout: '-',
        options: {
          reportDir: './output',
          inlineAssets: true,
          json: false,
        },
      },
    }
  }
}
