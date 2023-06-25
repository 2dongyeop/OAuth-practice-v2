# OAuth-practice-v2
> **해커톤을 앞두고..** 언제든 OAuth를 후딱 구축할 수 있도록 브랜치로 단계별로 구현하는 실습 레포..
>
>[이전에 작성한 Spring Security를 사용하지 않고 OAuth를 구현하는 프로젝트](https://github.com/2dongyeop/OAuth-practice)를 다시금 연습하였습니다.

<br/>

### Description

- [히스토리](https://github.com/2dongyeop/OAuth-practice-v2/commits/main)로 들어가면 이해를 돕기 위해 커밋별로 해당 단계에서 작성한 메시지들을 확인하실 수 있습니다.
  - 이전 프로젝트에서 작성해놓은 동작 방식을 보시지 마시고, [http 요청](https://github.com/2dongyeop/OAuth-practice-v2/tree/main/http)을 보고 따라하시는걸 추천드립니다.
  
  <br/>

- Docker를 설치했다는 가정 하에 작성하였으며, [링크에 첨부한 영상](https://www.youtube.com/watch?v=0r2-kX6gvRo)를 통해 redis-cli를 학습하시는걸 추천드립니다.
  - 로그인 성공시 AccessToken과 RefreshToken을 발급하는 과정에서 RefreshToken만 레디스에 저장하고 있습니다.
  - AccessToken이 만료되었을 경우, RefreshToken을 이용해 재발급받는 로직은 각자 작성해보시길 바랍니다.

  <br/>

- **이해를 위해** 예제로 주어진 시크릿 키를 공개로 올린 상태입니다. 
  - **개인 프로젝트에서는 반드시 키를 공개하시면 안됩니다.**


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