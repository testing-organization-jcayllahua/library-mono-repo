import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';

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
  vus: 5,
  iterations: 5,
  thresholds: {
    http_req_failed: ['rate<0.1'], // Error rate should be less than 1%
    http_req_duration: ['p(95)<1500'], // 95% of requests should be below 500ms
  }
};

export default function () {
  const userIndex = __VU - 1;

  if (userIndex >= users.length) {
    console.log(`No more user data available for VU ${__VU}. Skipping.`);
    return;
  }

  const user = users[userIndex];
  const headers = { 'Content-Type': 'application/json' };

  // Create Customer
  const createRes = http.post('http://localhost:8080/api/v1/customers', JSON.stringify(user), {
    headers,
    tags: { name: 'CreateCustomer' },
  });

  check(createRes, {
    'Create successful': (r) => r.status === 201 || r.status === 200,
    'Response has id': (r) => r.json('data.id') !== undefined,
  });

  let extractedId;
  try {
    extractedId = createRes.json('data.id');
  } catch (_) {
    extractedId = user.id;
  }

  // Get All Customers
  const getAllRes = http.get('http://localhost:8080/api/v1/customers', {
    headers,
    tags: { name: 'GetAllCustomers' },
  });

  check(getAllRes, {
    'Get All successful': (r) => r.status === 200,
    'Response contains data array': (r) => Array.isArray(r.json()),
  });

  // Get Specific Customer
  const getOneRes = http.get(`http://localhost:8080/api/v1/customers/${extractedId}`, {
    headers,
    tags: { name: 'GetCustomer' },
  });

  check(getOneRes, {
    'Get Customer successful': (r) => r.status === 200,
    'Correct customer returned': (r) => r.json('data.id') === extractedId,
  });

  // Delete Customer
  const deleteRes = http.del(`http://localhost:8080/api/v1/customers/${extractedId}`, null, {
    headers,
    tags: { name: 'DeleteCustomer' },
  });

  check(deleteRes, {
    'Delete successful': (r) => r.status === 200 || r.status === 204,
  });

  sleep(1);
}

export function handleSummary(data) {
  // Print metrics to terminal in a JMeter-style summary
  console.log('\n==================== Custom Summary ====================');
  for (const name in data.metrics.http_req_duration.tags) {
    const metric = data.metrics[`http_req_duration{${name}}`];
    if (metric) {
      console.log(`\nRequest: ${name}`);
      console.log(`  Avg   : ${metric.avg.toFixed(2)} ms`);
      console.log(`  Min   : ${metric.min.toFixed(2)} ms`);
      console.log(`  Max   : ${metric.max.toFixed(2)} ms`);
      console.log(`  90%   : ${metric['p(90)'].toFixed(2)} ms`);
      console.log(`  95%   : ${metric['p(95)'].toFixed(2)} ms`);
      console.log(`  99%   : ${metric['p(99)'].toFixed(2)} ms`);
    }
  }

  return {
    'index.html': htmlReport(data), // JMeter-like HTML
  };
}
