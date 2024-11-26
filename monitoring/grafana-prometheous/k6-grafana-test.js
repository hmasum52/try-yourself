import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Test configuration
export const options = {
  stages: [
    { duration: '1m', target: 10 },   // Ramp up to 10 users
    { duration: '3m', target: 10 },   // Stay at 10 users
    { duration: '1m', target: 20 },   // Ramp up to 20 users
    { duration: '3m', target: 20 },   // Stay at 20 users
    { duration: '1m', target: 0 },    // Ramp down to 0 users
  ],
  thresholds: {
    'http_req_duration': ['p(95)<500'], // 95% of requests should be below 500ms
    'errors': ['rate<0.1'],             // Error rate should be below 10%
  },
};

// Test setup
export function setup() {
  // Login to Grafana and get token
  const loginRes = http.post('http://grafana:3000/login', {
    user: 'admin',
    password: 'admin'
  });
  
  return {
    token: loginRes.headers['X-Grafana-Token'],
  };
}

// Main test function
export default function(data) {
  const headers = {
    'Authorization': `Bearer ${data.token}`,
    'Content-Type': 'application/json',
  };

  // Test different Grafana endpoints
  const tests = {
    // Get dashboards
    dashboards: http.get('http://grafana:3000/api/dashboards', { headers }),
    
    // Get specific dashboard (replace dashboard-uid with your dashboard UID)
    specificDashboard: http.get('http://grafana:3000/api/dashboards/uid/dashboard-uid', { headers }),
    
    // Search dashboards
    searchDashboards: http.get('http://grafana:3000/api/search?query=', { headers }),
    
    // Get datasources
    datasources: http.get('http://grafana:3000/api/datasources', { headers }),
    
    // Get organization users
    orgUsers: http.get('http://grafana:3000/api/org/users', { headers }),
  };

  // Check responses
  Object.entries(tests).forEach(([name, response]) => {
    const checkRes = check(response, {
      [`${name} status is 200`]: (r) => r.status === 200,
      [`${name} response time < 500ms`]: (r) => r.timings.duration < 500,
    });
    
    errorRate.add(!checkRes);
  });

  // Random sleep between requests (1-5 seconds)
  sleep(Math.random() * 4 + 1);
}

// Test teardown
export function teardown(data) {
  // Logout or cleanup
  http.post('http://grafana:3000/api/logout', null, {
    headers: {
      'Authorization': `Bearer ${data.token}`,
    },
  });
}