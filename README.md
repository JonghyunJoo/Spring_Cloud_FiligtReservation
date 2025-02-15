# 🎫 대규모 트래픽에도 안정적인 콘서트 티켓팅 플랫폼

## 목차
- [프로젝트 개요](#1-프로젝트-개요)
- [마일스톤](#2-마일스톤)
- [이벤트 시퀀스 다이어그랩](#3-이벤트-시퀀스-다이어그램)
- [ERD](#4-ERD)
- [API 명세 문서](#5-API-명세-문서)
- [주요 기술](#6-주요-기술)
- [동시성 문제와 극복](#7-동시성-문제와-극복)
- [대기열 시스템 설계 및 Redis 이관](#8-대기열-시스템-설계-및-Redis-이관)
- [캐시 도입을 통한 성능 개선](#9-캐시-도입을-통한-성능-개선)
- [쿼리 분석 및 인덱스 필요성 평가](#10-쿼리-분석-및-인덱스-필요성-평가)
- [API 부하 테스트 분석과 장애 대응 방안](#11-API-부하-테스트-분석과-장애-대응-방안)

## 1. 프로젝트 개요
- 특정한 시각에 사람들이 한꺼번에 몰려 트래픽이 집중되는 것을 가정한 티케팅 사이트
- 이러한 상황에서도 서버가 터지지 않게 하기 위한 안정적인 아키텍처를 고민해보고 프로젝트에 적용

### 목표 & KPI(핵심성과지표)

#### 1. **안정적인 예약 및 결제 시스템 구축**

✅ **대규모 트래픽 상황에서도 서버 안정성 유지**

- **예약 시스템 가용성 99.9% 이상 유지**
- **TPS(초당 트랜잭션) 500 이상 처리 가능** (부하 테스트 기준)
- 예약 및 결제 프로세스의 평균 응답 시간 **1초 이내** 유지

✅ **대기열을 통한 서버 부하 방지 및 서비스 안정성 확보**

- 동시 접속자 **1만명 이상** 처리 목표
- 대기열 진입 후 평균 대기 시간 3분 **이내**

#### 2. **공정한 예약 시스템 제공**

✅ **중복 예약 방지**

- 동시성 제어를 통한 **중복 예약 발생율 0% 목표**

✅ **예매 실패자를 위한 취소표 알림 시스템 운영**

- 대기열 혹은 동시성 제어로 인해 예매하지 못한 유저 대상으로 **취소표 알림 기능 제공**

#### 3. **모니터링 및 장애 대응 체계 구축**

✅ **애플리케이션 및 DB 성능 모니터링**

- 시스템 리소스(CPU, 메모리, DB 연결 수) **실시간 모니터링 대시보드 제공**
- 특정 임계치 초과 시 **자동 알람 및 확장(스케일링) 기능 활성화**

### 가정
- 짧은 시간에 많은 트래픽이 발생할 것으로 가정
- 특정 요청에 많은 트래픽이 동시에 발생할 것으로 가정

## 2. 🔗[마일스톤](https://github.com/JonghyunJoo/Spring_Cloud_ConcertReservation/blob/master/docs/01_Milestone.md)
## 3. 🔗[이벤트 시퀀스 다이어그램](https://github.com/JonghyunJoo/Spring_Cloud_ConcertReservation/blob/master/docs/02_EventSequence.md)
## 4. 🔗[ERD](https://github.com/JonghyunJoo/Spring_Cloud_ConcertReservation/blob/master/docs/03_ERD.md)
## 5. 🔗[API 명세 문서](https://flossy-name-c7c.notion.site/Spring-Cloud-ConcertReservation-1908f15d8fbc80ddb4ddcd3284892151)
## 6. 주요 기술
## MSA
MSA란 MicroService Architecture의 약자로, 기존의 Monolithic Architecture의 한계를 벗어나 애플리케이션을 느슨하게 결합된 서비스의 모임으로 구조화하는 서비스 지향 아키텍처(SOA) 스타일의 일종인 소프트웨어 개발 기법이다.

기존에 진행하고 있던 콘서트 예약 프로젝트를 크게 6개의 서비스를 분리하고 서버와 DB를 각 서비스에 맞게 분류하여 구현하였다.

### MSA 설계도
![Image](https://github.com/user-attachments/assets/b65db890-1750-4ab3-bc1b-927a5e24e999)

### MSA를 도입하며 얻은 이점
#### 확장성
- **서비스 단위로 확장 가능** : 각 서비스를 독립적으로 확장할 수 있어 필요에 따라 **수평 확장**(scale-out)이 가능하다.
- **클라우드 기반 서비스와의 최적화** : **동적 리소스 할당과 자동 스케일링 기능**을 통해 확장성이 극대화된다
#### 유연성
- **독립적인 배포와 운영** : 서비스 단위의 독립적인 수정과 배포가 가능해 개발 주기가 단축되고 빠르게 대응함으로써 **운영의 유연성**이 확보된다,
- **기술 스택의 자유로운 선택** : MSA 환경에서는 각 서비스가 독립적으로 운영되기 때문에, 서비스별로 적합한 **기술 스택을 자유롭게 선택** 가능하다.
#### 장애 대응성
- **장애 격리** : 하나의 서비스에 장애가 발생해도 **전체 시스템에 영향을 미치지 않도록 격리**하여 **서비스 가용성**이 확보된다.
- **부분 장애 대응 및 복구** : 서비스 간에 명확한 경계를 설정함으로써, **부분 장애 발생 시 빠른 대응**이 가능하고 **장애 발생 후 회복력**이 높아진다.

### Spring Cloud Gateway 및 Spring Eureka
기능별로 서버를 여러 개로 분리하였기 때문에, client에서 server로 요청을 보낼 때 어떤 서버로 요청을 전달해야 할 지 혼란스러울 수 있기 때문에 클라이언트로부터 들어온 요청을 구축해놓은 Gateway에서 라우팅하여 전달하였다.

#### Spring Cloud Gateway :
- 마이크로서비스 아키텍처에서 API 게이트웨이를 구축하기 위한 라이브러리, API 게이트웨이는 클라이언트와 마이크로서비스 간의 통신을 중개하고 라우팅하는 역할을 수행한다.
- Eureka와 같은 서비스 디스커버리 클라이언트와 통합하여 동적으로 서비스를 발견(discovery)한다.

#### Spring Cloud Eureka :
- 마이크로서비스 아키텍처에서는 많은 수의 서비스 인스턴스가 동적으로 생성되고 제거되기 때문에, 이러한 서비스들을 효과적으로 관리하고 찾을 수 있는 메커니즘이 필요하다.
- 서비스 인스턴스들은 Eureka 서버에 자신의 정보를 등록하고 등록한 클라이언트들은 Eureka 서버에 등록된 다른 서비스를 동적으로 검색해 요청을 전달한다.

### Feign Client 및 Kafka
- 서버들이 기능별로 분리됨에 따라 각 서버는 **자신이 관할하는 DB에만 직접 접근**이 가능하기 때문에 자신이 관할하지 않는 DB의 데이터가 필요할 경우, 해당 DB를 담당하고 있는 서버에게 데이터를 요청해야한다.
- MSA에서의 서버 간 통신을 하기 위한 방법으로 **Feign Client**를 통해 필요한 데이터를 요청하고 **Kafka**를 이용해 **이벤트 기반**의 **서버 간 통신 및 트랜잭션 처리**를 구현하였다.

### 더 자세한 MSA 전환 과정이 궁금하시다면

#### 🔗 [Docs 5. kafka_transactional](https://github.com/JonghyunJoo/Spring_Cloud_ConcertReservation/blob/master/docs/05_kafka_transactional.md)

#### 🔗 [MSA 전환기 포스트](https://velog.io/@j3261221/MSA-MSA-전환-프로젝트-MSA란)

## 쿠버네티스
### 도커 컨테이너 오케스트레이션

### 아키텍처

## 7. 동시성 문제와 극복
## 8. 대기열 시스템 설계 및 Redis 이관
## 9. 캐시 도입을 통한 성능 개선
## 10. 쿼리 분석 및 인덱스 필요성 평가
## 11. API 부하 테스트 분석과 장애 대응 방안
