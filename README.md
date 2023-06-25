# OAuth-practice-v2
> **해커톤을 앞두고..** 언제든 OAuth를 후딱 구축할 수 있도록 브랜치로 단계별로 구현하는 실습 레포..
>
>[이전에 작성한 Spring Security를 사용하지 않고 OAuth를 구현하는 프로젝트](https://github.com/2dongyeop/OAuth-practice)를 다시금 연습하였습니다.

<br/>

[히스토리](https://github.com/2dongyeop/OAuth-practice-v2/commits/main)로 들어가면 이해를 돕기 위해 커밋별로 작성한 메시지들을 확인하실 수 있으니 참고 바랍니다.

아래에서 동작 방식을 보시지 마시고, [http 요청](https://github.com/2dongyeop/OAuth-practice-v2/tree/main/http)을 보고 따라하시는걸 추천드립니다.

추후에는 Redis에 Access token 및 Refresh 토큰을 저장하는 과정을 추가할 예정입니다.

<br/>

### 사전 설명
- secret-key가 담긴 파일은 올리지 않습니다.
  - 하단에 gitignore된 파일 예시를 보여줍니다.
- OAuth에 대한 사전 지식을 필요로 합니다.
  - [참고 블로그](https://velog.io/@max9106/OAuth) 


<br/>

### 동작 방식

1. 애플리케이션 실행 후 localhost:8080으로 접속
2. `Google`, `Github`, `Naver` 중 원하는 방식을 클릭 후 로그인
3. White Label Page를 무시하고, 주소창에 생긴 code 값을 가져온다. 

→ ex) localhost:8080/redirect/oauth?code=***{code}***

4. Postman을 이용해 이 코드를 백엔드 서버로 보내보자. (아래의 provider에는 google, github, naver가 해당)

→ `GET` http://localhost:8080/login/oauth/{provider}?code={code}

5. Response로 받은 정보를 확인
6. h2 database에 접속해 회원 정보 확인

<br/>


### Config file

#### `application-oauth.yml`
```yaml
oauth2:
  user:
    google:
      client-id: { client-id }
      client-secret: { secret-key }
      redirect-uri: http://localhost:8080/redirect/oauth
    github:
      client-id: { client-id }
      client-secret: { secret-key }
      redirect-uri: http://localhost:8080/redirect/oauth
    naver:
      client-id: { client-id }
      client-secret: { secret-key }
      redirect-uri: http://localhost:8080/redirect/oauth
      
  provider:
    github:
      token-uri: https://github.com/login/oauth/access_token
      user-info-uri: https://api.github.com/user
    google:
      token-uri: https://www.googleapis.com/oauth2/v4/token
      user-info-uri: https://www.googleapis.com/oauth2/v3/userinfo
    naver:
      token-uri: https://nid.naver.com/oauth2.0/token
      user-info-uri: https://openapi.naver.com/v1/nid/me
```

<br/>

#### `application-jwt.yml`
```yaml
jwt:
  token:
    secret-key: { secret }

  refresh-token:
    expire-length: { time }

  access-token:
    expire-length: { time }
```
