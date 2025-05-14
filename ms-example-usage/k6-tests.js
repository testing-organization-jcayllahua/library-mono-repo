import http from 'k6/http';
import { check, sleep } from 'k6';

// Hard-coded user data instead of loading from CSV
const users = [
  {
    id: '123e4567-e89b-12d3-a456-426614174000',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    phoneNumber: '+51925685987',
    createdAt: '2025-05-10T12:30:45',
    updatedAt: '2025-05-11T09:15:22',
  },
  {
    id: '223e4567-e89b-12d3-a456-426614174001',
    firstName: 'Jane',
    lastName: 'Smith',
    email: 'jane.smith@example.com',
    phoneNumber: '+51925685988',
    createdAt: '2025-05-10T13:15:30',
    updatedAt: '2025-05-11T10:20:10',
  },
  {
    id: '323e4567-e89b-12d3-a456-426614174002',
    firstName: 'Bob',
    lastName: 'Johnson',
    email: 'bob.johnson@example.com',
    phoneNumber: '+51925685989',
    createdAt: '2025-05-10T14:45:00',
    updatedAt: '2025-05-11T11:05:00',
  },
  {
    id: '423e4567-e89b-12d3-a456-426614174003',
    firstName: 'Alice',
    lastName: 'Williams',
    email: 'alice.williams@example.com',
    phoneNumber: '+51925685990',
    createdAt: '2025-05-10T15:10:05',
    updatedAt: '2025-05-11T12:40:00',
  },
  {
    id: '523e4567-e89b-12d3-a456-426614174004',
    firstName: 'Tom',
    lastName: 'Brown',
    email: 'tom.brown@example.com',
    phoneNumber: '+51925685991',
    createdAt: '2025-05-10T16:00:00',
    updatedAt: '2025-05-11T13:30:45',
  }
];

export const options = {
  vus: 5,      // 5 virtual users (same as Thread Group num_threads)
  iterations: 5
};

export default function () {
  // Get the current user data (using VU number as index)
  const userIndex = __VU - 1;
  
  if (userIndex >= users.length) {
    console.log(`No more user data available for VU ${__VU}. Skipping.`);
    return;
  }
  
  const user = users[userIndex];
  console.log(`VU ${__VU} using user: ${user.firstName} ${user.lastName}`);
  
  // Step 1: Create Customer
  const createPayload = JSON.stringify({
    id: user.id,
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    phoneNumber: user.phoneNumber,
    createdAt: user.createdAt,
    updatedAt: user.updatedAt
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
  
  // Extract ID for future use
  let extractedId;
  try {
    extractedId = createResponse.json('data.id');
    console.log(`Created customer with ID: ${extractedId}`);
  } catch (e) {
    // If the JSON parsing fails, use the original ID
    extractedId = user.id;
    console.log(`Failed to extract ID from response, using original ID: ${extractedId}`);
  }
  
  // Step 2: Get All Customers
  const getAllResponse = http.get('http://localhost:8080/api/v1/customers', {
    headers: createHeaders,
    tags: { name: 'GetAllCustomers' }
  });
  
  check(getAllResponse, {
    'Get All successful': (r) => r.status === 200,
    'Response contains data array': (r) => Array.isArray(r.json()),
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
  
  // Step 4: Delete Customer
  const deleteResponse = http.del(`http://localhost:8080/api/v1/customers/${extractedId}`, null, {
    headers: createHeaders,
    tags: { name: 'DeleteCustomer' }
  });
  
  check(deleteResponse, {
    'Delete successful': (r) => r.status === 200 || r.status === 204,
  });
  
  // Print summary for this VU
  console.log(`VU ${__VU} completed all operations for user: ${user.firstName} ${user.lastName}`);
  
  // Add a small pause between iterations
  sleep(1);
}

import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
// Then add this at the end of your test:
export function handleSummary(data) {
  return {
    "index.html": htmlReport(data),
  };
}
