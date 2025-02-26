import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '10s', target: 100 },
        { duration: '10s', target: 300 },
        { duration: '10s', target: 500 },
        { duration: '40s', target: 1000 },
        { duration: '10s', target: 100 },
        { duration: '5s', target: 0 }
    ],
};

const userIds = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

export default function () {
    const userId = userIds[Math.floor(Math.random() * userIds.length)];

    const payload = JSON.stringify({
        userId: userId,
        amount: 1000
    });
    const params = { headers: { 'Content-Type': 'application/json' } };

    let res = http.patch('http://localhost:8080/api/point/charge', payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 2s': (r) => r.timings.duration < 2000,
    });

    sleep(0.1);
}
