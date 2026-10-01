// k6 run loadtest/orders.js
import http from 'k6/http';
import { check } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
  scenarios: { ramp: { executor: 'ramping-arrival-rate', startRate: 50, timeUnit: '1s',
    preAllocatedVUs: 200, stages: [{ target: 500, duration: '1m' }, { target: 500, duration: '2m' }] } },
  thresholds: { http_req_duration: ['p(99)<200'], http_req_failed: ['rate<0.01'] },
};

export default function () {
  const res = http.post('http://localhost:8080/api/v1/orders',
    JSON.stringify({ clientId: 'c-' + (__VU % 50), symbol: 'AAPL', side: 'BUY', quantity: 100, price: 150.25 }),
    { headers: { 'Content-Type': 'application/json', 'Idempotency-Key': uuidv4() } });
  check(res, { accepted: r => r.status === 202 });
}
