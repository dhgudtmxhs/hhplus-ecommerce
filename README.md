

## hhplus-ecommerce
e-commerce 에서 자주 사용되는 기능들을 구현한 백엔드 프로젝트입니다.  
사용자의 잔액 관리, 상품 조회, 쿠폰 발급, 주문/결제, 인기 상품 통계 등 e-commerce의 주요 기능을 제공합니다.  
클린 아키텍처 + 레이어드 아키텍처를 기반으로 DDD(Domain-Driven Design)를 적용하였습니다.


## [🔗 마일스톤](https://github.com/users/dhgudtmxhs/projects/3)
  
## [🔗 시퀀스 다이어그램, 플로우차트](https://github.com/dhgudtmxhs/hhplus-ecommerce/pull/11)

## [🔗 ERD, API명세](https://github.com/dhgudtmxhs/hhplus-ecommerce/pull/12)

## [🔗 MSA 형태로 도메인 배포단위 분리 및 트랜잭션 처리 설계](https://github.com/dhgudtmxhs/hhplus-ecommerce/pull/26)

## Configuration
| **Role**           | **Component**                |
|---------------------|------------------------------|
| ☕ Programming      | **Java 17**                  |
| 🌱 Framework        | **Spring Boot 3.4.1**        |
| ✅ Testing          | **JUnit 5**                  |
| 🐬 Database         | **MySQL 8.0**                |
| 🐳 Containerization | **Docker**                   |
| 🛠 Cache            | **Redis**                    |
| ✉️ Messaging        | **Kafka**                    |

<!--
## package
```plaintext
kr
└── hhplus
    └── be
        └── server
            ├── point
            │   ├── interfaces
            │   │   ├── PointController.java       ← REST API
            │   │   ├── PointRequest.java          ← DTO 요청
            │   │   └── PointResponse.java         ← DTO 응답
            │   ├── application
            │   │   ├── PointFacade.java           ← 유스케이스 조율
            │   │   ├── PointCommand.java          ← 유스케이스 요청 DTO
            │   │   └── PointInfo.java             ← 유스케이스 응답 DTO
            │   ├── domain
            │   │   ├── Point.java                 ← 도메인 객체
            │   │   ├── PointDomainService.java    ← 도메인 비즈니스 로직
            │   │   └── PointRepository.java       ← 도메인 레포지토리 인터페이스
            │   └── infra
            │       ├── PointJpaEntity.java        ← DB 매핑 엔티티
            │       ├── JpaPointRepository.java    ← Spring Data JPA 인터페이스
            │       └── PointRepositoryImpl.java   ← 도메인 인터페이스의 구현체
            ├── order
            │   ├── interfaces
            │   │   ├── OrderController.java
            │   │   ├── OrderRequest.java
            │   │   └── OrderResponse.java
            │   ├── application
            │   │   ├── OrderFacade.java
            │   │   ├── OrderCommand.java
            │   │   └── OrderInfo.java
            │   ├── domain
            │   │   ├── Order.java
            │   │   ├── OrderDomainService.java
            │   │   └── OrderRepository.java
            │   └── infra
            │       ├── OrderJpaEntity.java
            │       ├── JpaOrderRepository.java
            │       └── OrderRepositoryImpl.java
            ├── payment
            │   ├── interfaces
            │   │   ├── PaymentController.java
            │   │   ├── PaymentRequest.java
            │   │   └── PaymentResponse.java
            │   ├── application
            │   │   ├── PaymentFacade.java
            │   │   ├── PaymentCommand.java
            │   │   └── PaymentInfo.java
            │   ├── domain
            │   │   ├── Payment.java
            │   │   ├── PaymentDomainService.java
            │   │   └── PaymentRepository.java
            │   └── infra
            │       ├── PaymentJpaEntity.java
            │       ├── JpaPaymentRepository.java
            │       └── PaymentRepositoryImpl.java
            ├── product
            │   ├── interfaces
            │   │   ├── ProductController.java
            │   │   ├── ProductRequest.java
            │   │   └── ProductResponse.java
            │   ├── application
            │   │   ├── ProductFacade.java
            │   │   ├── ProductCommand.java
            │   │   └── ProductInfo.java
            │   ├── domain
            │   │   ├── Product.java
            │   │   ├── ProductDomainService.java
            │   │   └── ProductRepository.java
            │   └── infra
            │       ├── ProductJpaEntity.java
            │       ├── JpaProductRepository.java
            │       └── ProductRepositoryImpl.java
            ├── user
            │   ├── interfaces
            │   │   ├── UserController.java
            │   │   ├── UserRequest.java
            │   │   └── UserResponse.java
            │   ├── application
            │   │   ├── UserFacade.java
            │   │   ├── UserCommand.java
            │   │   └── UserInfo.java
            │   ├── domain
            │   │   ├── User.java
            │   │   ├── UserDomainService.java
            │   │   └── UserRepository.java
            │   └── infra
            │       ├── UserJpaEntity.java
            │       ├── JpaUserRepository.java
            │       └── UserRepositoryImpl.java
            ├── coupon
            │   ├── interfaces
            │   │   ├── CouponController.java
            │   │   ├── CouponRequest.java
            │   │   └── CouponResponse.java
            │   ├── application
            │   │   ├── CouponFacade.java
            │   │   ├── CouponCommand.java
            │   │   └── CouponInfo.java
            │   ├── domain
            │   │   ├── Coupon.java
            │   │   ├── CouponDomainService.java
            │   │   └── CouponRepository.java
            │   └── infra
            │       ├── CouponJpaEntity.java
            │       ├── JpaCouponRepository.java
            │       └── CouponRepositoryImpl.java
            └── common
                ├── exception
                │   ├── GlobalExceptionHandler.java ← 공통 예외 클래스
                │   └── ErrorCode.java              ← 공통 에러 코드
                └── util, ... ← 필요한 경우 추가
```




**도메인 중심 설계**(DDD)와 **클린 + 레이어드 아키텍처**를 기반으로 패키지를 구성했습니다.  

도메인별(`point`, `order`, `product`, `user`, `coupon`)로 패키지를 나누었으며,  
결제(payment)는 주문(Order) 흐름의 일부로 판단하여, order 도메인 내에 포함했습니다.  
각 도메인은 **interfaces → application → domain ← infra**의 계층 구조를 따릅니다.  
모든 계층은 도메인 중심으로 설계되었으며, 높은 응집도와 낮은 결합도를 목표로 합니다.  

공통 기능은 `common` 패키지로 관리합니다.
20250213 각 개발 내용 링크 필요. 패키지 구조는 변경해야 함 -->

