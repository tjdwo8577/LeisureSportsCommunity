# MySQL 데이터 베이스와 Spring boot를 이용한 블로그를 만드어 봤습니다.

- Spring Boot
- Spring MVC
- Spring JDBC
- MYSQL - SQL
- thymeleaf 템플릿 엔진

## MySQL 데이터 베이스에 board, role, role2, user, user_role의 테이블을 만들어 로그인 기능을통해 게시글 작성자만 글 삭제와 수정이 가능하게 하고
## 게시글을 board 테이블에 저장하게 만들어 봤습니다.
### 테이블의 속성은 아래와 같습니다.
![mysql1](https://github.com/tjdwo8577/baord_group3/assets/88715270/1ed08859-a08d-4721-a83c-aefdfb166799)

![mysql2](https://github.com/tjdwo8577/baord_group3/assets/88715270/07df94e0-df1b-498d-9aef-c273e3da9d41)

```
                     Spring Core
                     Spring MVC                   Spring JDBC    MySQL
브라우저 ---- 요청 ---> Controller ----> Service ----> DAO ----> DB
        <--- 응답 --- 템플릿 <---           <----         <----
                      <------------ layer간에 데이터 전송은 DTO -->
```
### css 부분은 아직 미완성입니다.

# 수정 및 추가 사항
### 메세지 팝업 해주기
####    - 로그인 했으면 로그인 했다고 팝업 메세지
####    - 글 작성,수정,삭제 했으면 작성 했다고 팝업 메세지 
####    - 로그인 하지 않고 글 작성 혹은 삭제 했을시 오류 메세지("로그인" 해달라는 팝업 메세지, 삭제시 "글 작성자가 아닙니다" 팝업 메세지)
####    - 제목 과 글 내용을 무조건으로 하기

#### search 할수 있는거 만들기 
#### pageable 할수 있게 하기
#### 댓글 + 대댓글 

#### 사이드 탭을 만들어서 기상예측 + 편의점 위치 (기상청 api + 카카오지도 api)
