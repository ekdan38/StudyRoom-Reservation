# StudyRoom-Reservation

#### -스터디룸 예약 시스템을 위한 REST API 개발로, 사용자들이 간편하게 스터디룸을 예약하고 업체와 스터디룸을 관리할 수 있는 기능 제공<br>
#### -회원 가입, 업체 및 스터디룸 관리, 예약 시스템을 포함하여 다양한 기능 제공하며, 사용자 역할에 따라 기능을 분리<br>
#### -보안성과 무결성 유지를 위해 Spring Security와 JWT 기반 인증 활용<br>

사용 스킬 : JAVA17, SpringBoot, SpringSecurity, MySQL, JPA, JWT, AmazonS3, Junit, Mockito, Github, Git, Postman

### 💡작동 흐름
  1. 회원 가입 및 로그인 : 사용자는 회원 가입 후 로그인하여 시스템을 사용
  2. 스터디룸 업체 등록 및 관리 : 스터디룸 업체 사장은 자신의 업체를 등록하고 관리
  3. 스터디룸 등록 및 관리 : 등록된 업체는 스터디룸을 생성하고 관리
  4. 스터디룸 예약 : 사용자는 업체와 스터디룸을 조회하고 예약 가능 시간을 확인한 후 예약 및 예약 취소 가능

### 💡사용자 역할별 기능
◆일반 사용자(USER):
  -회원 가입, 로그인, 로그아웃
  -스터디룸 조회 및 예약
  -예약 내역 확인 및 취소

◆업체 사장(STUDYROOM_ADMIN):
  -스터디룸 업체 및 스터디룸 CRUD (등록, 수정, 삭제)
  -업체, 특정 스터디룸에 대한 글 CRUD
  -예약 관리

◆시스템 관리자(SYSTEM_ADMIN):
  -모든 업체 및 스터디룸에 대한 관리 권한

notionLink : https://amethyst-macaroni-f4b.notion.site/12587a78a36880ea95eff0c947956d40?pvs=4 <br>
API명세서 : https://documenter.getpostman.com/view/33322261/2sAY545xq7#9bbf3959-b95e-45b9-8657-cc8b263635d2
