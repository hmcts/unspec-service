const { Client, logger } = require("camunda-external-task-client-js");

// configuration for the Client:
//  - 'baseUrl': url to the Workflow Engine
//  - 'logger': utility to automatically log important events
const config = { baseUrl: "http://localhost:8080/engine-rest", use: logger , workerId: "chargeCreditCardWorker",  lockDuration: 20000};

// create a Client instance with custom configuration
const client = new Client(config);

// susbscribe to the topic: 'creditScoreChecker'
client.subscribe("chargeCreditCard", async function({ task, taskService }) {
  // Put your business logic

  const shouldFailCard = task.variables.get("shouldFailCard");
  console.log("shouldFailCard: "+ shouldFailCard);
  if(shouldFailCard){
    await taskService.handleBpmnError(task, "BPMNError_Code", "Error message", variables);

  } else{ 

console.log("Charging credit card now ..  ");
const amount = task.variables.get("remainingAmountAfterCredit");
console.log("Charging credit card now for  " + amount);

console.log("Credit card is charged now");

  // complete the task
await taskService.complete(task);
  }
});
