~~~
KKukkie's bookstore
~~~
![IMG_1041](https://user-images.githubusercontent.com/37236920/187603755-ac393017-c9f8-4d96-9a12-174f09e62c02.JPG)
  
## Purpose
~~~
- 서점 운영 시스템 구축
- 복잡한 개발보다는 [Spring Boot] 와 [Spring Data JPA] 를 사용한 개발 방법 적용
~~~
  
## Function
### 1) 팀 + 계정 관리
~~~
- 사용자 계정은 특정 팀에 아래 종속되어 관리된다.
- 팀은 관리자나 일반 권한을 갖지 않는다. 단순히 영역(지역 또는 범위)을 구분하기 위한 포괄적인 단위 개념이다.
- 사용자는 관리자와 일반 사용자로 구분된다.
- 관리자 계정만 팀에 대한 CRUD 가 가능하다.
- 일반 사용자 계정은 회원가입만 할 수 있다.
~~~
### 2) 책 관리
~~~
- 관리자 계정만 책을 추가, 삭제, 수정 가능하다.
- 일반 사용자 계정을 책을 조회 가능하다.
- 사용자는 자신의 계정에 책 정보를 추가할 수 있다. (장바구니 기능 또는 즐겨찾기 기능, 향후 구현 예정)
~~~
  
## Tech
~~~
1) Spring boot
2) Spring data jpa
3) QueryDSL
4) Thymeleaf
~~~
  
## Flow
![스크린샷 2022-08-31 오후 3 36 13](https://user-images.githubusercontent.com/37236920/187610083-17c11c65-c8cd-4a8e-8087-e789c0cbc3f0.png)
  
