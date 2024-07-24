## Project System Design
![project design-002(upload)](https://github.com/user-attachments/assets/4e325496-c330-4164-a7ff-cd68d1797212)

- 쿠폰 발급 서버([링크](https://github.com/commerce-prac/coupon-issue))
- 사용자 대기열 관리 서버([링크](https://github.com/commerce-prac/queue-for-reserve))
- 게이트웨이 & 유레카 서버([링크](https://github.com/commerce-prac/gateway-service_discovery))
- 인증/인가 서버([링크](https://github.com/commerce-prac/auth))

## 사용 기술
<div style="display: flex; flex-wrap: wrap; gap: 10px;">
    <img src="https://img.shields.io/badge/java-007396?style=flat-square&logo=java&logoColor=white">
    <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring&logoColor=white">
    <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white">
    <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white">
    <img src="https://img.shields.io/badge/Redis-red?style=flat-square">
    <img src="https://img.shields.io/badge/Grafana-red?style=flat-square">
    <img src="https://img.shields.io/badge/Prometheus-orange?style=flat-square">
    <img src="https://img.shields.io/badge/Locust-yellow?style=flat-square">
    <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat-square">
    <img src="https://img.shields.io/badge/Spring WebFlux-6DB33F?style=flat-square">
    <img src="https://img.shields.io/badge/Spring Cloud Gateway-6DB33F?style=flat-square">
    <img src="https://img.shields.io/badge/Spring Cloud Netflix-6DB33F?style=flat-square">
    <img src="https://img.shields.io/badge/Spring Authorization Server-6DB33F?style=flat-square">
    <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat-square">
</div>

## 트러블 슈팅 및 성능 개선
### 쿠폰 발급 서버
- Grafana, Prometheus로 애플리케이션 서버 용량 모니터링 시스템 구축([링크](https://velog.io/@shinnybest/%EB%B6%80%ED%95%98-%ED%85%8C%EC%8A%A4%ED%8A%B8%EB%A1%9C-%EC%95%A0%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98-%EC%84%9C%EB%B2%84%EC%9D%98-%EC%9E%84%EA%B3%84%EC%B9%98%EB%A5%BC-%ED%99%95%EC%9D%B8%ED%95%98%EA%B3%A0-%EC%8B%B6%EC%96%B4%EC%9A%94))
- MySQL 비관적락으로 동시성 이슈 해결([링크](https://velog.io/@shinnybest/MySQL-InnoDB-Repeatable-Read-Row-Lock-PESSIMISTICWRITE))
- Redis 분산락으로 동시성 이슈 해결([링크](https://velog.io/@shinnybest/Redis-%EB%B6%84%EC%82%B0%EB%9D%BD%EC%9C%BC%EB%A1%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88%EB%A5%BC-%ED%95%B4%EA%B2%B0%ED%96%88%EC%A7%80%EB%A7%8C-%EC%97%AC%EC%A0%84%ED%9E%88-%EB%AC%B8%EC%A0%9C%EB%8A%94-%EB%82%A8%EC%95%84%EC%9E%88%EB%8B%A4))
- 쿠폰 발행 비동기 처리로 구조 변경([링크](https://velog.io/@shinnybest/%EC%BF%A0%ED%8F%B0-%EB%B0%9C%ED%96%89%EC%9D%80-%EB%B9%84%EB%8F%99%EA%B8%B0-%EC%B2%98%EB%A6%AC%EB%90%98%EB%8F%84%EB%A1%9D-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%84%A4%EA%B3%84%EB%A5%BC-%EC%83%88%EB%A1%9C-%ED%95%B4%EB%B3%B4%EA%B8%B0))
- Redis Lua Script 작성 및 Local Cache 적용으로 성능 개선([링크](https://velog.io/@shinnybest/Lua-Script-in-Redis-Local-Cache-%EA%B4%80%EB%A6%AC%EB%A5%BC-%ED%86%B5%ED%95%9C-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0))
- Redis 서버 장애 발생 시, 즉각 대응 가능한 시스템 구축 w. Sentinel([링크](https://velog.io/@shinnybest/Redis-Primary-Replica%EC%99%80-Sentinel%EB%A1%9C-%EC%9E%A5%EC%95%A0%EC%97%90%EB%8F%84-%EB%8C%80%EC%9D%91%EA%B0%80%EB%8A%A5%ED%95%9C-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0))
- [트러블슈팅] synchronized로 동시성 이슈 해결([링크](https://velog.io/@shinnybest/Troubleshooting-%EC%BF%A0%ED%8F%B0-%EB%B0%9C%EA%B8%89-%EB%82%B4%EC%97%AD%EC%9D%B4-%EC%98%88%EC%83%81%EB%B3%B4%EB%8B%A4-%EB%8D%94-%EB%A7%8E%EC%9D%B4-%EC%8C%93%EC%97%AC%EC%9E%88%EC%8A%B5%EB%8B%88%EB%8B%A4))
- [트러블슈팅] RedissonClient.getLock이 선착순 티켓팅 서비스에 사용될 수 없는 이유([링크](https://velog.io/@shinnybest/non-fair-locking-%EB%B9%84%EA%B3%B5%EC%A0%95-%EB%9D%BD))

### 사용자 대기열 관리 서버
- WebFlux + ReactiveRedisTemplate으로 애플리케이션 서버 성능 개선([링크](https://velog.io/@shinnybest/Spring-WebFlux%EB%A1%9C-%EC%95%A0%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98-%EC%84%9C%EB%B2%84%EC%9D%98-%EC%B2%98%EB%A6%AC-%EC%84%B1%EB%8A%A5%EC%9D%84-%EB%86%92%EC%9D%B4%EA%B8%B0))
- [트러블슈팅] 대기열 순서 보장이 안 되는 문제 해결([링크](https://velog.io/@shinnybest/Redis-sorted-set-timestamp-score))

### 게이트웨이 & 유레카 서버
- 로드밸런서 기능 추가([링크](https://velog.io/@shinnybest/Gateway-%EC%84%9C%EB%B2%84%EC%99%80-%EC%84%9C%EB%B9%84%EC%8A%A4-%EC%9D%B8%EC%8A%A4%ED%84%B4%EC%8A%A4-scale-out-%EC%8B%9C%EB%8F%84))
- 토큰 검증 및 로깅 필터 추가([링크](https://velog.io/@shinnybest/MSA-LB-w.-Eureka-Client-tmt0xb2x))

### 인증 / 인가 서버
- [트러블슈팅] 서버 재시작 시 토큰 검증 에러 문제 해결([링크](https://velog.io/@shinnybest/Auth-%EC%84%9C%EB%B2%84-%EC%9E%AC%EC%8B%9C%EC%9E%91-%EC%8B%9C-%ED%86%A0%ED%81%B0-%EA%B2%80%EC%A6%9D-%EC%8B%A4%ED%8C%A8-%ED%95%B4%EA%B2%B0-w.-Keytool))
