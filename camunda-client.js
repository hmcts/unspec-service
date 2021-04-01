const { Client, logger, Variables } = require("camunda-external-task-client-js");

// configuration for the Client:
//  - 'baseUrl': url to the Workflow Engine
//  - 'logger': utility to automatically log important events
const config = { baseUrl: "http://localhost:8080/engine-rest", use: logger, workerId: "deductAmountWorker" };

// create a Client instance with custom configuration
const client = new Client(config);

// susbscribe to the topic: 'creditScoreChecker'
client.subscribe("deductAmount", async function({ task, taskService }) {
  // Put your business logic

console.log("Deducting amount now ..  ");
const amount = task.variables.get("amount");
const credit = task.variables.get("credit");
const shouldFail = task.variables.get("shouldFail");
console.log("shouldFail: "+ shouldFail);

if(shouldFail){

  await taskService.handleBpmnError(task, "BPMNError_Code", "Error message", variables);
  
} else{

console.log("amount: "+ amount);
console.log("credit" + credit);

const resultVars = new Variables();

if(amount <= credit ){
  console.log(" enough credit ")
  resultVars.set("creditSufficient", true);
} else{
  console.log(" credit is less ");
  resultVars.set("creditSufficient", false);
  resultVars.set("shouldFailCard", true);
  resultVars.set("remainingAmountAfterCredit", amount - credit)

}


  // complete the task
await taskService.complete(task, resultVars);
}
});
