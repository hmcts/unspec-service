const retry = (fn, retryTimeout = 3000, remainingRetries = 5, err = null) => {
  if (!remainingRetries) {
    return Promise.reject(err);
  }

  return fn().catch(async err => {
    console.log(`Failed due to an error: ${err}, will try again in ${retryTimeout / 1000} seconds (${remainingRetries} retries left)`);
    await sleep(retryTimeout);
    return retry(fn, 2 * retryTimeout, remainingRetries - 1, err);
  });
};

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

module.exports = {retry};

