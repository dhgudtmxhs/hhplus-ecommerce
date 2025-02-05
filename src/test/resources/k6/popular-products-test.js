import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '10s', target: 200 },  // 10초 동안 200명 부하 증가
        { duration: '10s', target: 500 },  // 10초 동안 500명까지 증가
        { duration: '10s', target: 800 },  // 10초 동안 800명까지 증가
        { duration: '40s', target: 2000 }, // 40초 동안 2000명 유지
        { duration: '10s', target: 300 },  // 10초 동안 300명으로 감소
        { duration: '5s', target: 0 }      // 5초 동안 부하 종료
    ],
};

export default function () {
    let res = http.get('http://localhost:8080/api/products/popular');

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 2s': (r) => r.timings.duration < 2000, // 2초 넘는 응답 확인
    });

    sleep(0.2);  // 요청 간격
}
