

## hhplus-ecommerce
e-commerce 에서 자주 사용되는 기능들을 구현한 백엔드 프로젝트입니다.  
사용자의 잔액 관리, 상품 조회, 쿠폰 발급, 주문/결제, 인기 상품 통계 등 e-commerce의 주요 기능을 제공합니다.  
클린 아키텍처와 레이어드 아키텍처를 기반으로 DDD(Domain-Driven Design)를 적용하여, 도메인 중심의 비즈니스 로직을 명확히 분리하고 유연한 패키지 구조를 설계하고자 했습니다.

## 초기 설계 패키지 구조
```plaintext
com
└── example
    └── ecommerce
        ├── order
        │   ├── interfaces
        │   │   ├── OrderController.java
        │   │   ├── OrderRequest.java
        │   │   ├── OrderResponse.java
        │   │   └── ...
        │   ├── application
        │   │   ├── OrderFacade.java
        │   │   ├── OrderCommand.java
        │   │   └── OrderInfo.java
        │   ├── domain
        │   │   ├── Order.java
        │   │   ├── OrderService.java
        │   │   └── OrderRepository.java
        │   └── infra
        │       ├── OrderJpaEntity.java
        │       ├── OrderRepositoryImpl.java
        │       └── JpaOrderRepository.java
        ├── product
        │   ├── interfaces
        │   │   ├── ProductController.java
        │   │   ├── ProductRequest.java
        │   │   ├── ProductResponse.java
        │   │   └── ...
        │   ├── application
        │   │   ├── ProductService.java
        │   │   ├── ProductCommand.java
        │   │   └── ProductInfo.java
        │   ├── domain
        │   │   ├── Product.java
        │   │   ├── ProductService.java
        │   │   └── ProductRepository.java
        │   └── infra
        │       ├── ProductJpaEntity.java
        │       ├── ProductRepositoryImpl.java
        │       └── JpaProductRepository.java
        ├── user
        │   ├── interfaces
        │   │   ├── UserController.java
        │   │   ├── UserRequest.java
        │   │   ├── UserResponse.java
        │   │   └── ...
        │   ├── application
        │   │   ├── UserService.java
        │   │   ├── UserCommand.java
        │   │   └── UserInfo.java
        │   ├── domain
        │   │   ├── User.java
        │   │   ├── UserService.java
        │   │   └── UserRepository.java
        │   └── infra
        │       ├── UserJpaEntity.java
        │       ├── UserRepositoryImpl.java
        │       └── JpaUserRepository.java
        └── coupon
            ├── interfaces
            │   ├── CouponController.java
            │   ├── CouponRequest.java
            │   └── CouponResponse.java
            ├── application
            │   ├── CouponService.java
            │   ├── CouponCommand.java
            │   └── CouponInfo.java
            ├── domain
            │   ├── Coupon.java
            │   ├── CouponService.java
            │   └── CouponRepository.java
            └── infra
                ├── CouponJpaEntity.java
                ├── CouponRepositoryImpl.java
                └── JpaCouponRepository.java
```

## Configuration
| **Role**           | **Component**                |
|---------------------|------------------------------|
| ☕ Programming      | **Java 17**                  |
| 🌱 Framework        | **Spring Boot 3.4.1**        |
| ✅ Testing          | **JUnit 5**                    |
| 🐬 Database         | **MySQL 8.0**                |
| 🐳 Containerization | **Docker**                   |
| 🛠 Cache            | **Redis**                    |
| ✉️ Messaging        | **Kafka**                    |


#### [🔗 마일스톤](https://github.com/users/dhgudtmxhs/projects/3)
  
### 시퀀스 다이어그램
```mermaid
sequenceDiagram
    %% 레이어(5개): 사용자, 표시(프레젠테이션), 비즈니스, 인프라, DB
    actor U as 사용자
    participant P as presentation layer
    participant B as business layer
    participant I as infra layer
    participant DB as db

    Note over U,P: (1) 잔액 충전 / 조회
    U->>P: 잔액 충전 요청 (사용자ID, 충전금액)
    P->>B: 잔액 충전 비즈니스 로직
    B->>I: 사용자 잔액 업데이트
    I->>DB: DB에 사용자 잔액 갱신
    DB-->>I: 업데이트 결과
    I-->>B: 갱신된 잔액 정보
    B-->>P: 잔액 정보 응답
    P-->>U: 충전 결과 응답

    U->>P: 잔액 조회 요청 (사용자ID)
    P->>B: 잔액 조회 비즈니스 로직
    B->>I: 사용자 잔액 조회
    I->>DB: DB에서 잔액 SELECT
    DB-->>I: 잔액 데이터
    I-->>B: 조회된 잔액 정보
    B-->>P: 잔액 정보
    P-->>U: 잔액 조회 결과

    Note over U,P: (2) 상품 조회
    U->>P: 상품 목록 조회 요청
    P->>B: 상품 조회 비즈니스 로직
    B->>I: 상품 목록 조회
    I->>DB: DB에서 상품 목록 SELECT
    DB-->>I: 상품 목록
    I-->>B: 상품 데이터
    B-->>P: 상품 목록 결과
    P-->>U: 상품 조회 응답

    Note over U,P: (3) 선착순 쿠폰 발급 / 조회
    U->>P: 쿠폰 발급 요청 (사용자ID)
    P->>B: 쿠폰 발급 로직
    B->>I: 쿠폰 재고 확인 및 발급
    I->>DB: 쿠폰 재고 SELECT
    DB-->>I: 남은 쿠폰 재고
    alt 쿠폰 남아 있음
        I->>DB: 쿠폰 재고 -1 업데이트
        DB-->>I: 업데이트 성공
        I-->>B: 쿠폰 발급 성공
        B-->>P: 쿠폰 정보 응답
        P-->>U: 쿠폰 발급 완료
    else 쿠폰 소진
        I-->>B: 쿠폰 발급 불가
        B-->>P: 쿠폰 발급 실패
        P-->>U: 쿠폰 소진 안내
    end

    U->>P: 보유 쿠폰 조회 요청 (사용자ID)
    P->>B: 쿠폰 조회 로직
    B->>I: 사용자 쿠폰 조회
    I->>DB: DB에서 사용자 쿠폰 SELECT
    DB-->>I: 쿠폰 목록
    I-->>B: 쿠폰 데이터
    B-->>P: 쿠폰 조회 결과
    P-->>U: 보유 쿠폰 목록 응답

    Note over P,B: (4) 주문 / 결제
    U->>P: 주문 요청 (사용자ID, 상품/수량, 쿠폰ID)
    P->>B: 주문/결제 로직
    alt 쿠폰이 있음
        B->>I: 쿠폰 유효성 확인
        I->>DB: 쿠폰 상태 SELECT
        DB-->>I: 유효/무효, 할인 정보 등
        I-->>B: 쿠폰 데이터
    end
    B->>I: 잔액 및 재고 확인
    I->>DB: 잔액, 상품 재고 SELECT
    DB-->>I: 조회 결과 (잔액, 재고)
    alt 재고 또는 잔액 부족
        B-->>P: 주문 실패 (잔액 부족/재고 부족)
        P-->>U: 주문 실패 안내
    else 결제 가능
        B->>I: 잔액 차감 및 재고 차감
        I->>DB: UPDATE (user_balance, product_stock)
        DB-->>I: 업데이트 완료
        B-->>P: 주문 성공
        P-->>U: 결제 완료 응답
    end

    Note over U,P: (5) 인기 상품 조회
    U->>P: 인기 상품 조회 요청 (최근 3일 기준)
    P->>B: 인기 상품 조회 로직
    B->>I: 최근 3일간 판매량 상위 5개 조회
    I->>DB: SELECT Top5 (최근 3일 판매량)
    DB-->>I: 베스트셀러 목록
    I-->>B: 베스트셀러 데이터
    B-->>P: 베스트셀러 결과
    P-->>U: 인기 상품 조회 응답
```

### 플로우 차트

```mermaid
flowchart TD
    A[Start] --> B{사용자 요청: 잔액 충전, 상품조회 등}
    B --> C[잔액 충전 API 호출: /balance/charge?userId&amount]
    C --> D{DB에 잔액 업데이트}
    D --> E[결과 반환: 잔액]
    E --> F[사용자 만족도 확인]
    F --> |예| G[쿠폰 발급: /coupons/issue?userId]
    F --> |아니오| H[다른 API 호출]

    G --> I{쿠폰 발급 가능 여부}
    I --> |성공| J[쿠폰 정보 저장 & 반환]
    I --> |실패| K[실패 메시지 반환]
    J --> L[쿠폰 확인 후 주문 단계 이동]
    K --> L

    L --> M[상품 조회: /products]
    M --> N{DB에서 상품 목록 및 재고 조회}
    N --> O[조회 결과 반환]

    O --> P[주문 및 결제: /orders]
    P --> Q{잔액 또는 재고 부족?}
    Q --> |예| R[결제 실패 메시지]
    Q --> |아니오| S[재고 & 잔액 차감]
    S --> T[데이터 플랫폼에 주문정보 전송]
    T --> U[결제 성공 후 종료]
```
### ERD

### API

### MOCK API

### package, configuration




