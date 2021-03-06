# Moon-API-gateway

[![Build Status](https://travis-ci.org/longcoding/undefined-api-gateway.svg?branch=master&maxAge=2592000)](https://travis-ci.org/longcoding/undefined-api-gateway.svg?branch=master)
[![codecov](https://codecov.io/gh/longcoding/undefined-api-gateway/branch/master/graph/badge.svg?maxAge=2592000)](https://codecov.io/gh/longcoding/undefined-api-gateway/branch/master/graph/badge.svg)
[![Release](https://img.shields.io/github/release/longcoding/undefined-api-gateway.svg?maxAge=2592000)](https://img.shields.io/github/release/longcoding/undefined-api-gateway.svg)
[![HitCount](http://hits.dwyl.io/longcoding@gmail.com/longcoding/undefined-api-gateway.svg)](http://hits.dwyl.io/longcoding@gmail.com/longcoding/undefined-api-gateway.svg)
[![LastCommit](https://img.shields.io/github/last-commit/longcoding/undefined-api-gateway.svg)](https://img.shields.io/github/last-commit/longcoding/undefined-api-gateway.svg)
[![TotalCommit](https://img.shields.io/github/commit-activity/y/longcoding/undefined-api-gateway.svg)](https://img.shields.io/github/commit-activity/y/longcoding/undefined-api-gateway.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?maxAge=2592000)]()

![feature](https://user-images.githubusercontent.com/3271895/51427833-6d132780-1c3f-11e9-8f73-7112a7f0da0c.png)


## Introduction
비동기 API Gateway(이하 게이트웨이) with Spring boot 2.1, Servlet 4, jetty 9 client <br />
게이트웨이는 로드밸런싱, 클러스터링 및 여러 유효성 검증을 지원하는 실시간 높은 수준의 웹 기반 프로토콜 단일 엑세스 접근을 보장하는 네트워크 게이트웨이입니다. 게이트웨이는 open API를 제공하기 위해서 최고의 성능으로 동작하도록 설계되었습니다.

* 경량 API Gateway
* 고성능
* 확장성

## New Feature(January-2019)
* API 관리 기능
* 서버 클러스터 지원

## Features
Moon-API-Gateway는 강력하지만, 가볍고 빠른 기능을 제공합니다.

* **Request Validation** - Request 요청에 대한 여러가지 유효성 검증 기능을 사용할 수 있습니다. 또한 새로운 기능에 쉽게 적용하고 제거할 수 있습니다.
    - Header, Query, Path Param
* **Rate Limiting** - API 사용자들에 대한 강력한 사용빈도 제한이 가능합니다. Redis-based 클러스터 서버들은 키 기반으로 사용빈도 제한 정보를 공유할 수 있습니다.
    - App 일단위 사용빈도 제한
    - App 분단위 사용빈도 제한
* **Service Capacity** - 서비스의 안정적인 동작을 위해 API 게이트웨이에 연결된 서비스 가용량(Capacity)을 관리합니다.
    - Service 일단위 가용량
    - Service 분단위 가용량
* **Service Contract(agreement)** - (Optional) API, App 사용자들은 계약 관계나 계약 기간에 부합하는 API만 호출할 수 있습니다.
* **Request Transform** - (Optional) Header, Query, Path Param, URI 변경을 지원합니다. 사용자 request를 Moon-API-Gateway와 연결된 서비스의 request로 적절하게 변경합니다.
* **IP Whitelisting** - 보다 안전한 상호작용을 위해 키 단위로 신뢰할 수 없는 IP 주소의 접근을 차단합니다.
* **Management API** - API 게이트웨이 관리를 위한 강력한 Rest API를 제공합니다.
    - API Add/Delete/Change
    - APP Add/Delete/Change
    - IP Whitelist Add/Delete
    - Key Expiry/Regenerate
* **Supported Server Cluster** - API 게이트웨이 클러스터를 관리할 수 있습니다. 관리(Management) API를 이용해서 변경사항을 모든 서버에 적용할 수 있습니다. 즉, 사용빈도, 서비스 가용량 정보를 모든 서버가 공유할 수 있습니다.

## Dependency
* Spring Boot 2.1
* Servlet 4
* Ehcache 3
* Jetty 9 client
* Jedis 3.0

## Configuration
Moon-API-Gateway 실행을 위한 필요한 설정있습니다.
관리(management) API를 이용하면 초기화를 진행할 필요가 없습니다.

### Step 1
```
- Please set the global application first in application.yaml

moon:
  service:       
    ip-acl-enable: false
    cluster:
      enable: false
      sync-interval: 300000       
    proxy-timeout: 20000

jedis-client:      
  host: '127.0.0.1'
  port: 6379
  timeout: 1000
  database: 0
```
- ip-acl-enable: IP 화이트리스트 기능을 설정합니다. 이는 APP 기반으로 동작합니다.
- cluster/enable: 서버 클러스터 설정을 사용한다면 데몬 스레드가 Service, App, API 정보들을 가져옵니다.
- cluster/sync-interval: 클러스터의 동기화 시간을 설정할 수 있습니다.
- proxy-timeout: request proxy 타임아웃 시간을 설정할 수 있습니다.  
- **jedis-client**: Moon-API-Gateway에서 Redis 설정은 반드시 필요합니다.
- jedis-client/host: Redis 호스트 정보를 설정합니다.
- jedis-client/port: Redis 포트 정보를 설정합니다.

### B. Step 2
```
- Please set the initial application registration in application-apps.yaml
- (These settings are optional)

init-apps:
  init-enable: true
  apps:
    -
      app-id: 0
      app-name: TestApp
      api-key: 1000-1000-1000-1000
      app-minutely-ratelimit: 2000
      app-daily-ratelimit: 10000
      app-service-contract: [1, 2, 3]
      app-ip-acl: ['192.168.0.1', '127.0.0.1']
    -
      app-id: 1
      app-name: BestApp
      api-key: e3938427-1e27-3a37-a854-0ac5a40d84a8
      app-minutely-ratelimit: 1000
      app-daily-ratelimit: 50000
      app-service-contract: [1, 2]
      app-ip-acl: ['127.0.0.1']
```

- init-enable: init-apps 설정 사용 여부를 설정합니다.
- app-service-contract: 사용권한이 있는 app API 서비스 목록을 설정합니다.
- app-ip-acl: API 키를 사용할 수 있는 IP 화이트리스트 목록을 설정합니다.
- app minutely/daily ratelimit: app에서 호출 가능한 API 호출빈도를 설정합니다.

### Step 3
```
- Set up service and API specification configurations in application-apis.yml
- The API Gateway obtains Service and API information through the APIExposeSpecLoader.

api-spec:
  init-enable: true
  services:
    -
      service-id: 1
      service-name: stackoverflow
      service-minutely-capacity: 10000
      service-daily-capacity: 240000
      service-path: /stackoverflow
      outbound-service-host: api.stackexchange.com
      apis:
        -
          api-id: 101
          api-name: getInfo
          protocol: http, https
          method: get
          inbound-url: /2.2/question/:first
          outbound-url: /2.2/questions
          header: page, votes
          header-required: ""
          query-param: version, site
          query-param-required: site
        -
          api-id: 202
          api-name: getQuestions
          protocol: https
          method: put
          inbound-url: /2.2/question/:first
          outbound-url: /2.2/questions
          header: page, votes
          header-required: ""
          query-param: version, site
          query-param-required: site
    -
      service-id: 2
      service-name: stackoverflow2
      service-minutely-capacity: 5000
      service-daily-capacity: 100000
      service-path: /another
      outbound-service-host: api.stackexchange.com
      apis:
        -
          api-id: 201
          api-name: transformTest
          protocol: http, https
          method: get
          inbound-url: /2.2/haha/question/:site
          outbound-url: /:page/:site
          header: page, votes
          header-required: ""
          query-param: version, site
          query-param-required: site
          transform:
            page: [header, param_path]
            site: [param_path, header]
    -
      service-id: '03'
      service-name: service3
      service-minutely-capacity: 5000
      service-daily-capacity: 100000
      service-path: /service3
      outbound-service-host: api.stackexchange.com
      only-pass-request-without-transform: true
```
- init-enable: api-spec 사용 여부를 설정합니다.
- service-path: URL 경로의 첫번째 파라미터를 설정합니다. API는 해당 경로로 등록된 서비스로 라우팅됩니다.
- service minutely/daily capacity: 서비스의 분/일 단위 가용량을 설정합니다.
- outbound-service-host: 서비스 API의 응답이 라우팅되는 Outbound 도메인을 설정합니다.
- apis/inbound-url: 외부로 노출할 API URL 경로를 명세합니다. `:?`는 변수이며 이곳에 설정합니다.
- apis/outbound-url: API 게이트웨이에 접속할 실제 URL을 설정합니다.
- apis/header: API를 호출할 때 받을 수 있는 헤더를 설정합니다.
- apis/header-required: API 요청의 필수 헤더를 설정합니다.
- apis/query-param: URL 쿼리 파라미터를 설정합니다.
- apis/query-param-required: 필수 URL 쿼리 파라미터를 설정합니다.
- transform: 수신한 요청의 파라미터를 라우팅과 동시에 다른 파라미터로 변환되도록 설정합니다.
    - 사용 가능한 옵션: **header**, **param_path**, **param_query**, **body_json**
    - 사용 방법: [source, destination] 형태로 설정합니다. 예: [header, param_path]
    - body_json 타입을 사용하는 경우, `content-type`은 `application/json`을 사용해야합니다.
- only-pass-request-without-transform: 게이트웨이에 접속하는 모든 API가 어떤 분석이나 변환이 없이 서비스로 라우팅되도록 설정합니다.


Moon-api-gateway 는 아래의 프로토콜과 메소드를 지원합니다.

* 프로토콜
    - HTTP, HTTPS
* 메소드
    - GET, POST, PUT, DELETE

## API Gateway Cluster
Moon API Gateway는 클러스터를 지원합니다. 각 노드는 실시간으로 API, APP, IP 허용 목록 및 앱 키 (= API 키) 정보를 동기화합니다. 또한 클러스터 노드들이 함께 올바른 API 사용빈도 제한, 서비스 가용용량을 계산합니다.

- **Service, API, APP, IP Whitelist Interval Sync**

![feature](https://user-images.githubusercontent.com/3271895/51427837-7b614380-1c3f-11e9-82f3-5a668ba63f00.png)


## Management REST API
관리 API는 단일 게이트웨이 혹은 클러스터 그룹 관리에 유용합니다.

**APP Management**
  - **APP 등록** - [POST] /internal/apps
  - **APP 삭제** - [DELETE] /internal/apps/{appId}
  - **APP 업데이트** - [Future Feature]
  - **API Key 만료처리** - [DELETE] /internal/apps/{appId}/apikey
  - **API Key 갱신처리** - [PUT] /internal/apps/{appId}
  - **IP 허용목록 추가** - [POST] /internal/apps/{appId}/whitelist
  - **IP 허용목록 삭제** - [DELETE] /internal/apps/{appId}/whitelist

**API Management**
  - **API 추가** - [POST] /internal/apis
  - **API 삭제** - [DELETE] /internal/apis/{apiId}
  - **API 수정** - [PUT] /internal/apis/{apiId}

**Service Group API는 곧 업데이트 예정입니다.**



## Usage/Test

##### API - Stack Overflow API.

스택오버플로우(Stack Overflow) 명세를 활용한 예시입니다.
```
service-id: '01'
      service-name: stackoverflow
      service-minutely-capacity: 10000
      service-daily-capacity: 240000
      service-path: /stackoverflow
      outbound-service-host: api.stackexchange.com
      apis:
        -
          api-id: '0101'
          api-name: getInfo
          protocol: http, https
          method: get
          inbound-url: /2.2/question/:first
          outbound-url: /2.2/questions
          header: page, votes
          header-required: ""
          query-param: version, site
          query-param-required: site
```
- 1) 호출하려는 API 서비스 경로는 `/stackoverflow`입니다.
- 2) 게이트웨이 인입 URL은 `/2.2/question/:first` 경로로 설정됩니다.
- 3) ':first' 부분에 경로 파라미터를 추가로 설정합니다.
- 4) 호출 프로토콜은 http와 https를 지원하고, http로 호출합니다.
- 5) 헤더와 URL 쿼리 파라미터를 설정합니다.
- 6) 호출된 API는 `outbound-service-host`로 설정된 `api.stackexchange.com` 도메인으로 라우트됩니다.
- 7) 호출된 API는 `api.stackexchange.com` 도메인의 목적지 URL인 `outbound-url` 설정값인 `/2.2/questions` 경로로 호출됩니다.

##### 1) 테스트 사용법 - Run moon-api-gateway by gradle

    ./gradlew test

##### 2) Postman 같은 클라이언트 사용
메소드와 스킴을 설정합니다.

    GET, http

URL을 입력하세요.

    http://localhost:8080/stackoverflow/2.2/question/test

URL 파라미터를 입력합니다.(site는 필수 파라미터입니다.)

    site = stackoverflow

또는 아래처럼 URL을 입력할 수 있습니다.

    http://localhost:8080/stackoverflow/2.2/question/test?site=stackoverflow

헤더 필드를 입력합니다.(apikey는 필수 헤더값 혹은 쿼리 파라미터입니다)

    apikey, 1000-1000-1000-1000
    page, 5
    votes, 1

요청을 실행하고, 응답 코드와 내용을 확인합니다.

##### 3) Curl 사용하기.

    curl -X GET -H "Content-type: application/json" -H "apikey: 1000-1000-1000-1000" -H "page: 5" -H "votes: 1" http://localhost:8080/stackoverflow/2.2/question/test?site=stackoverflow

## Future update
* 비공개 API를 위한 인증
* Docker-Compose
    - 쉽게 시작하기

## Contact
문의사항은 `longcoding@gmail.com`으로 연락주세요.

## License
`Moon-API-Gateway`는 MIT 라이센스 권리를 갖습니다. LICENSE 세부사항을 확인하세요.
