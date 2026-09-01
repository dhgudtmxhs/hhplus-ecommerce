## Overview

항해 플러스 백엔드 과정에서 개인으로 진행한 이커머스 백엔드 프로젝트입니다.

포인트, 상품, 쿠폰, 주문, 결제 도메인을 중심으로 이커머스의 기본 흐름을 구현했습니다.

동시 요청에서 발생하는 데이터 정합성 문제와 인기 상품 조회 성능을 개선하는 데 중점을 두었습니다.

## Features

| Domain | Features |
| --- | --- |
| 포인트 | 사용자 포인트 조회 및 충전 |
| 상품 | 상품 목록 조회, 최근 3일 판매량 기준 인기 상품 조회 |
| 쿠폰 | 보유 쿠폰 조회, 수량이 제한된 쿠폰의 선착순 발급 |
| 주문 | 다중 상품 주문, 상품 재고 차감 및 주문 금액 계산 |
| 결제 | 쿠폰 할인, 포인트 차감, 결제 처리 및 주문 상태 변경 |

## Tech Stack

| Area | Technologies |
| --- | --- |
| **Core** | Java 17, Spring Boot 3.4.1, Gradle |
| **Persistence** | Spring Data JPA, QueryDSL, MySQL 8 |
| **Cache & Lock** | Redis 7, Spring Cache, Redisson, Redis Lua Script |
| **Event** | Spring Application Event, Spring Retry |
| **Testing** | JUnit 5, Testcontainers, k6 |
| **Documentation & Infrastructure** | Springdoc OpenAPI, Docker Compose |

## Architecture

사용자, 포인트, 상품, 쿠폰, 주문, 결제를 도메인 단위로 분리했습니다.
각 도메인은 `interfaces → application → domain ← infra` 구조를 따르며, Facade에서 여러 도메인 서비스를 조합해 하나의 유스케이스를 처리합니다.

도메인 간에는 JPA 연관관계 대신 식별자를 사용하여 결합도를 낮추고, Repository 인터페이스는 domain에 두고 실제 구현은 infra에서 담당하도록 구성했습니다.

| Layer | Role |
| --- | --- |
| `interfaces` | HTTP 요청과 응답 처리 |
| `application` | 유스케이스 조율 및 도메인 서비스 조합 |
| `domain` | 엔티티와 핵심 비즈니스 규칙 |
| `infra` | JPA, QueryDSL, Redis 등 데이터 접근 구현 |

## Key Implementations

### 비즈니스 특성에 따른 동시성 제어

포인트 충전에는 낙관적 락과 재시도를 적용해본 뒤, 동시 요청 테스트를 거쳐 비관적 락으로 변경했습니다.

포인트, 쿠폰, 상품 재고처럼 여러 요청이 동일한 데이터를 변경하는 구간에는 MySQL의 비관적 락을 적용하고, 일부 조회에는 lock timeout을 설정해 장시간 대기를 제한했습니다.

동일 사용자의 주문과 동일 주문에 대한 결제 요청은 Redisson 분산 락으로 동시에 처리되지 않도록 구성했습니다.

- 포인트 충전, 초기 쿠폰 발급, 상품 재고 차감에 `PESSIMISTIC_WRITE` 적용
- `lock:user:{userId}`를 기준으로 동일 사용자의 주문 동시 처리 제한
- `lock:payment:order:{orderId}`를 기준으로 동일 주문의 동시 결제 제한
- 포인트, 쿠폰, 주문, 결제에 대한 동시 요청 테스트 작성

[동시성 테스트](https://github.com/dhgudtmxhs/ecommerce-backend/pull/20) ·
[락 전략 적용 및 개선](https://github.com/dhgudtmxhs/ecommerce-backend/pull/22)

### Redis Lua Script를 활용한 선착순 쿠폰 발급

초기에는 쿠폰 발급 수량과 중복 발급을 제어하기 위해 DB 비관적 락을 사용했습니다.
이후 별도 개선 브랜치에서 DB 락에 대한 의존도를 줄이기 위해 Redis와 Lua Script를 활용하는 방식으로 변경했습니다.

Lua Script 내부에서 다음 작업을 하나의 작업으로 처리하여 Redis 내 원자성을 보장했습니다.

- 동일 사용자의 중복 발급 여부 확인
- 쿠폰의 남은 수량 확인
- 쿠폰 수량 차감
- 발급 사용자 기록

DB 저장에 실패하면 Redis의 발급 기록을 제거하고 쿠폰 수량을 복구하는 보상 처리도 함께 구현했습니다.

[Redis와 Lua Script를 활용한 쿠폰 발급 개선](https://github.com/dhgudtmxhs/ecommerce-backend/pull/24)

### 인기 상품 조회 성능 개선

최근 3일 동안 판매된 수량을 집계하여 상위 5개의 인기 상품을 조회합니다.

상품 10만 건과 주문 상품 100만 건의 더미 데이터를 구성해 인덱스 조합을 비교하고, `product_id`, `created_at`, `quantity` 복합 인덱스를 적용했습니다.

반복되는 집계 쿼리를 줄이기 위해 Redis에 조회 결과를 캐싱했습니다.
인기 상품 캐시는 3시간 동안 유지하며, 만료 시간이 한 번에 겹치는 것을 줄이기 위해 TTL에 임의 값을 추가했습니다.

- QueryDSL을 사용한 최근 3일 판매 수량 집계
- `order_item` 테이블 복합 인덱스 적용
- Spring Cache와 Redis를 사용한 조회 결과 캐싱
- k6를 사용한 인기 상품 조회 부하 테스트 시나리오 작성

[Redis 캐시 및 부하 테스트](https://github.com/dhgudtmxhs/ecommerce-backend/pull/23) ·
[인덱스 비교 및 개선](https://github.com/dhgudtmxhs/ecommerce-backend/pull/25)

### 결제 이후 처리 분리

결제가 완료된 이후 외부 플랫폼으로 주문 데이터를 전달하는 상황을 가정해 모의 연동을 구현했습니다.

외부 데이터 전송 실패가 결제 데이터의 롤백에 영향을 주지 않도록 결제 내부에서는 Application Event를 발행하고, 트랜잭션 커밋 이후 이벤트를 처리하도록 구성했습니다.

외부 데이터 전송 실패 시 재시도하며, 최초 시도를 포함해 최대 3회 처리한 후에도 실패한 요청은 로그를 통해 확인할 수 있도록 했습니다.

[이벤트 기반 주문 데이터 처리](https://github.com/dhgudtmxhs/ecommerce-backend/pull/26)

## Testing

JUnit 5를 기반으로 도메인 단위 테스트, 서비스 통합 테스트, Controller API 통합 테스트를 작성했습니다.

- Testcontainers를 사용한 MySQL 테스트 환경 구성
- 포인트, 쿠폰, 주문, 결제의 동시 요청 시나리오 검증
