import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import { parseCSV } from 'https://jslib.k6.io/papaparse/5.1.1/index.js';

// Load CSV data - make sure the path to your CSV file is correct
const customers = new SharedArray('customers', function() {
  // Load the CSV file and parse it using Papa Parse
  return parseCSV(open('.library-mono-repo/ms-example-usage/customers.csv'), { header: true }).data;
});

export const options = {
  vus: 5,      // 5 virtual users (same as Thread Group num_threads)
  iterations: 5, // Each VU will execute the script once (equivalent to one loop iteration)
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95% of requests should be below 1s
  },
};

export default function () {
  // Get the current customer data (using VU number as index)
  const customerIndex = __VU - 1;
  
  if (customerIndex >= customers.length) {
    console.log(`No more customer data available for VU ${__VU}. Skipping.`);
    return;
  }
  
  const customer = customers[customerIndex];
  
  // Step 1: Create Customer
  const createPayload = JSON.stringify({
    id: customer.id,
    firstName: customer.firstName,
    lastName: customer.lastName,
    email: customer.email,
    phoneNumber: customer.phoneNumber,
    createdAt: customer.createdAt,
    updatedAt: customer.updatedAt
  });
  
  const createHeaders = { 'Content-Type': 'application/json' };
  
  const createResponse = http.post('http://localhost:8080/api/v1/customers', createPayload, { 
    headers: createHeaders,
    tags: { name: 'CreateCustomer' } 
  });
  
  check(createResponse, {
    'Create successful': (r) => r.status === 201 || r.status === 200,
    'Response has id': (r) => r.json('data.id') !== undefined,
  });
  
  // Extract ID for future use (similar to JSON Extractor in JMeter)
  let extractedId;
  try {
    extractedId = createResponse.json('data.id');
    console.log(`Created customer with ID: ${extractedId}`);
  } catch (e) {
    // If the JSON parsing fails, use the original ID from the CSV
    extractedId = customer.id;
    console.log(`Failed to extract ID from response, using original ID: ${extractedId}`);
  }
  
  // Step 2: Get All Customers
  const getAllResponse = http.get('http://localhost:8080/api/v1/customers', {
    headers: createHeaders,
    tags: { name: 'GetAllCustomers' }
  });
  
  check(getAllResponse, {
    'Get All successful': (r) => r.status === 200,
    'Response contains data array': (r) => Array.isArray(r.json('data')),
  });
  
  // Step 3: Get Specific Customer
  const getOneResponse = http.get(`http://localhost:8080/api/v1/customers/${extractedId}`, {
    headers: createHeaders,
    tags: { name: 'GetCustomer' }
  });
  
  check(getOneResponse, {
    'Get Customer successful': (r) => r.status === 200,
    'Correct customer returned': (r) => r.json('data.id') === extractedId,
  });
  
  // Step 4: Delete Customer (this was in your JMeter error logs but not in the JMX)
  const deleteResponse = http.del(`http://localhost:8080/api/v1/customers/${extractedId}`, null, {
    headers: createHeaders,
    tags: { name: 'DeleteCustomer' }
  });
  
  check(deleteResponse, {
    'Delete successful': (r) => r.status === 200 || r.status === 204,
  });
  
  // Add a small pause between iterations to simulate real user behavior
  sleep(1);
}

/*
To run this test:
1. Save this script as script.js
2. Make sure your customers.csv file is in the same directory
3. Run with: k6 run script.js

Add more options for larger tests:
- Ramp up: export const options = { 
    stages: [
      { duration: '30s', target: 5 },   // Ramp up to 5 users over 30 seconds
      { duration: '1m', target: 5 },    // Stay at 5 users for 1 minute
      { duration: '30s', target: 0 },   // Ramp down to 0 users
    ]
  };

- Generate HTML report with the browser-based UI extension:
  k6 run --out web-dashboard=export=results.html script.js
*/
