/* global process */

exports.config = {
  tests: './tests/*_test.js',
  output: './output',
  helpers: {
    Puppeteer: {
      restart: false,
      keepCookies: true,
      show: process.env.SHOW_BROWSER_WINDOW || false,
      windowSize: '1200x900',
      waitForTimeout: 40000,
      waitForNavigation: [ "domcontentloaded", "networkidle0" ],
      chrome: {
        ignoreHTTPSErrors: true,
        args: process.env.PROXY_SERVER ? [`--proxy-server=${process.env.PROXY_SERVER} --no-sandbox`,] : ['--no-sandbox'],
      },
    },
    PuppeteerHelpers: {
      require: './helpers/puppeteer_helper.js',
    },
  },
  include: {
    I: './steps_file.js',
    api: './api/steps.js'
  },
  plugins: {
    autoDelay: {
      enabled: true,
      methods: [
        'click',
        'fillField',
        'checkOption',
        'selectOption',
        'attachFile',
      ],
    },
    retryFailedStep: {
      enabled: true,
    },
    screenshotOnFail: {
      enabled: true,
      fullPageScreenshots: true,
    },
  },
  mocha: {
    bail: true,
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
};
