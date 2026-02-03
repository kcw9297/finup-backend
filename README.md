>### FinUp (주식 경제 공부 플랫폼)
#### 천재IT교육센터 Java 풀스택 과정 12기 3차 팀 프로젝트 gif 자료


![홈페이지 전경](https://velog.velcdn.com/images/darklight_0418/post/11d10808-6af7-45f7-9593-3f0ef2d97b47/image.gif)

---

## 회원가입/로그인

이메일 기반 회원가입이 가능합니다.
- 회원가입 시도 시 등록한 메일로 인증코드를  전송할 수 있습니다.
- 가입 시 비밀번호 유효성 검사를 하게끔 설계했습니다.

> - 인증 메일 전송 : 스프링 부트 메일 라이브러리
- 인증 코드 검증 : 코드 발송 
            → Redis 인메모리 데이터베이스에 저장 
            → 저장된 Redis Key 기반 인증 번호 유효 및 만료여부를 판단

> 1. 인증 코드 전송 및 확인 
![인증코드](https://velog.velcdn.com/images/darklight_0418/post/7c96b041-7ece-42e9-ab73-d2a051376aed/image.gif)
***
> 1-1. 발송된 인증코드
<img src="https://velog.velcdn.com/images/darklight_0418/post/a1e9002e-8391-4c7f-8279-704b4bc0b2c4/image.jpg"
     width="50%" height="50%"
     />
***
> 2. 비밀번호 유효성 검사 
![비밀번호 유효성 검사](https://velog.velcdn.com/images/darklight_0418/post/b3874a3d-3a25-40b2-8fa4-6fbfc89391ad/image.gif)
***
> 3. 로그인
![로그인](https://velog.velcdn.com/images/darklight_0418/post/9e11cb20-9f8d-4ecf-9b74-49c4913d93e9/image.gif)

***

## 회원 정보 관련

**개인 정보 수정**
회원은 가입 후 프로필 사진, 닉네임, 비밀번호를 수정할 수 있습니다.
- 비밀번호는 유효성 검사를 통해 올바른 규칙의 비밀번호를 입력하게끔 설정했습니다.
- 이미지 업로드를 통해 프로필 사진을 변경할 수 있고, 닉네임 역시 새로 설정 가능합니다.


>
- 프로필 변경
![프로필 사진 변경](https://velog.velcdn.com/images/darklight_0418/post/10f07f50-b7ca-4c03-aaae-a0e4ed10dd00/image.gif)
***
- 비밀번호 변경 시 유효성 검사
![비밀번호 변경 시 유효성 검사](https://velog.velcdn.com/images/darklight_0418/post/cada0334-b057-4427-8e72-4955a5f101a9/image.gif)


---
## 홈 페이지

### 1. 개념 학습

**수준 테스트**

현재 사용자의 금융 지식 상식을 확인해볼 수 있는 퀴즈를 풀 수 있습니다.
- 후보 단어 중 임의로 선별하여 본인의 금융 지식 수준을 판단할 수 있습니다.
- 테스트를 마친 후 수준에 맞는 콘텐츠를 학습할 수 있습니다.

> - 퀴즈 데이터 : 시사경제용어사전 용어
- 퀴즈 문제 및 보기 : 후보 중에서 AI 기반 선별 

> 1. 수준 테스트
![개념 테스트](https://velog.velcdn.com/images/darklight_0418/post/e3952fb6-4f1b-4f70-9937-33316b46ac71/image.gif)
***
> 2. 개념 학습 (개념 정리) 페이지
![개념 학습 페이지](https://velog.velcdn.com/images/darklight_0418/post/8c3593a2-9c39-4022-a007-fe20f09680e4/image.gif)
***
> 3. 개념 학습 진척도 반영
![개념 학습 진척도 반영](https://velog.velcdn.com/images/darklight_0418/post/2e80a12e-160f-42f1-a72c-3bd8cc20f982/image.gif)


### 뉴스 학습

**1. 뉴스 목록**

투자 학습에 적절한 최신 뉴스 목록을 제공합니다.

- 뉴스는 작성일 기준 최신 순으로 최대 2주까지의 기사가 제공됩니다. (무한 스크롤)
- 뉴스 검색 결과 중, 내용이 유사하거나 학습에 적절하지 않은 기사는 필터하였습니다.

> - 목록 제공 : 네이버 뉴스 API
- 필터 기준 : 기사 제목과 본문을 n-gram 토큰화 
        → jaccard, dice score 계산             
        → 일정 이상의 유사도를 보이면 필터 

***

**2. 뉴스 본문**

목록에서 원하는 뉴스를 클릭하여 뉴스 본문을 볼 수 있습니다.
뉴스를 클릭하면 다음과 같은 정보가 제공됩니다.

- 뉴스 제목, 썸네일 이미지 : 네이버 뉴스 API 제공
- 언론사, 뉴스 본문 : 네이버 뉴스 API 에서는 뉴스 본문과 언론사 정보 미제공
               → API 제공 뉴스링크 기반 원본 기사에서 크롤링 후 제공
- AI 분석 : 현재 뉴스내용에 맞는 초보자에게 유용한 기사 분석 제공
- AI 키워드 : 기사와 관련 있는 추천 경제 용어 제공 (RAG 추천 기반 시사경제용어사전 용어 중 선별하여 제공)

***

>
1. 뉴스 단어 재추천 및 단어 바로가기
![뉴스 단어 재추천 및 단어 바로가기](https://velog.velcdn.com/images/darklight_0418/post/e2a61a84-d6b2-4962-9af6-65a3a2db33df/image.gif)
***
2. 그 외 재추천 항목들
![그 외 재추천 항목들](https://velog.velcdn.com/images/darklight_0418/post/a07a0536-1ea1-410e-bfae-f1e23459662c/image.gif)
***
3. 뉴스 원문 바로가기
![뉴스 원문 바로가기](https://velog.velcdn.com/images/darklight_0418/post/1bdb0316-3c72-44cf-a781-7db40eb52ab9/image.gif)
***
4. 뉴스 탭 진입 후 뉴스 살펴보기
![뉴스 탭 진입 후 뉴스 살펴보기](https://velog.velcdn.com/images/darklight_0418/post/115f929c-2f6a-433b-9d0b-a281d4eaefd5/image.gif)
***
5. 뉴스 무한 스크롤 구현
![뉴스 무한 스크롤 구현](https://velog.velcdn.com/images/darklight_0418/post/3e25ab4b-a297-47a9-964c-c8e0ac3c3c9c/image.gif)

* * *
### 종목 학습

**1. 종목 목록**

실제 거래되는 국내 주식 종목 기반 투자 학습 정보를 제공합니다.

- 정보 제공 : KIS(한국투자증권) API
- 제공 목록 : 시가총액 순, 거래대금 순으로 각각 상위 30개 종목
***

>
- 종목 학습 탭, 시가총액 거래대금 순위 확인
![종목 학습 탭](https://velog.velcdn.com/images/darklight_0418/post/693f5989-83cd-4f82-b2f3-4e211b84fc6f/image.gif)

***

**2-1. 차트**
현재 종목에 대한 100개의 캔들로 이루어진 차트를 제공합니다.
시가, 고가, 저가, 종가, 거래량, 5일/20일 평균선 정보를 제공합니다.

- 제공 차트 : 일봉, 주봉, 월봉
- AI 분석 : 현재 차트 기반 종합적, 추세, 리스크, 거래량 분석 정보

>
![종목별 차트 확인](https://velog.velcdn.com/images/darklight_0418/post/5ec6c2e1-b127-4638-900c-983cab3d6ba3/image.gif)

***

**2-2. 상세**
현재 종목의 상세 지표 및 분석 정보를 확인할 수 있습니다.
- 지표 설명
- 여러 관점에서의 AI 분석
- 추천 영상 : 종목명으로 유튜브 검색 API > 검색된 영상 후보를 AI 선별 후 제공하는 RAG 추천으로 제공

>![종목별 상세 항목 확인](https://velog.velcdn.com/images/darklight_0418/post/7df362d3-0a6f-4e2b-8797-82edf7f85c02/image.gif)


***
**2-3. 뉴스**
- 현재 종목에 특화된 종목과 연계하여 학습하기 적절한 뉴스 목록을 제공합니다.
제공 방식은 상단의 "뉴스 본문" 과 동일합니다.

>![종목별 뉴스](https://velog.velcdn.com/images/darklight_0418/post/cbba6e16-e05a-408b-938c-f69f88744b3c/image.gif)


---
### 단어 학습 (개념 테스트 전용 단어들)

학습자가 궁금한 경제 용어를 검색하고 학습할 수 있습니다.
제공되는 단어 기반은 모두 시사경제용어사전 용어를 이용했습니다.

***
**1. 단어장 홈**
단어 학습 전반에 도움이 될 기능을 제공합니다.
구체적으론 아래 기능들이 제공됩니다.

1. 과거 검색어 목록
	- Redis List 자료형에 저장하여, 최근 20개의 검색어 목록을 빠르게 제공
2. 오늘의 단어 및 퀴즈
3. 단어 검색 바
***

**2. 단어 검색**
검색한 단어 키워드와 연관성이 높은 검색 결과를 제공합니다. (벡터 유사도 기반)
최대 20개의 검색 결과를 제공합니다.

***
**3. 단어 상세**
검색된 단어를 클릭하면, 단어 상세 뜻 확인 및 단어장 저장 기능이 제공됩니다.
"뉴스 상세" 에서 제공되는 뉴스 키워드를 클릭 시에도 확인 가능합니다.

>
- 단어장 퀴즈 기능
![단어장 퀴즈 기능](https://velog.velcdn.com/images/darklight_0418/post/5cca3d02-35d0-41ac-ad9e-02c3fa594cfa/image.gif)
***
>
- 내 단어장 기능
![내 단어장 기능](https://velog.velcdn.com/images/darklight_0418/post/d0d5812b-fcbf-4daa-9514-545278c7ccb7/image.gif)
***
>
- 내 단어장에 단어 저장하기
![내 단어장에 단어 저장](https://velog.velcdn.com/images/darklight_0418/post/c56e0dde-495b-480c-85ab-4f06de0b24a5/image.gif)
***
>
- 단어 검색 기능 (벡터 연산을 통한 상위 20개 단어 출력 및 최근 검색어 저장)
![단어 검색 기능](https://velog.velcdn.com/images/darklight_0418/post/c821984e-bafa-4c59-9fdd-d088ce981b19/image.gif)
***
>
- 기획재정부 시사경제용어사전 링크 (추가 학습에 용이하도록 설계)
![기획재정부 시사경제용어사전 링크](https://velog.velcdn.com/images/darklight_0418/post/66bd3243-f45a-4a66-8479-0115ab4e451c/image.gif)

---
### 관리자 기능

#### 회원 목록

관리자 화면에서 회원 목록을 별도로 조회할 수 있는 화면입니다.
- 개인정보 보호를 위한 마스킹 기능을 적용했습니다.
- pdf, xlsx 확장자 형식의 회원 전체 목록을 다운로드 받을 수 있습니다.

>
- 회원 목록 일부 글자 마스킹 기능
![회원 목록 일부 글자 마스킹 기능](https://velog.velcdn.com/images/darklight_0418/post/0960c147-2bae-488d-8a6b-f6f2c8bba743/image.gif)
***
- 회원 목록 pdf, xlsx 파일 다운로드
![회원 목록 pdf, xlsx 파일 다운로드](https://velog.velcdn.com/images/darklight_0418/post/730a5de2-4aa9-4379-8fd8-b1cbbb350a23/image.gif)

#### 공지사항
홈화면에서 사용자에게 최근 3개의 공지사항을 표시합니다.
- 관리자는 별도로 공지사항을 등록/수정할 수 있습니다.
- 관리자 메뉴에서 공지사항 게시글 검색이 가능하며, 조회가 가능합니다.

>
- 공지사항 검색
![공지사항 검색](https://velog.velcdn.com/images/darklight_0418/post/a2764684-c7f9-482c-9d9d-39fb8d3f64b6/image.gif)
***
- 공지사항 등록
![공지사항 등록](https://velog.velcdn.com/images/darklight_0418/post/f3088c97-e536-4acd-b06c-10f7c2a38bc3/image.gif)


#### 개념 학습 관리

개념 학습에서 회원이 학습할 수 있는 파트를 관리하는 화면입니다.
- 관리자가 별도로 개념 학습 파트 조회 및 수정이 가능합니다.
- 마찬가지로 규칙에 따른 유효성 검사 기능이 있습니다.

>
- 항목 조회 및 편집 기능
![개념 학습 조회 및 편집](https://velog.velcdn.com/images/darklight_0418/post/ae9fdcc3-5edb-417d-81ac-cb72089f7b12/image.gif)


#### 단어 관리 (개념 학습 파트, 관리자 전용)
개념 학습에서 별도로 사용되는 단어를 등록할 수 있습니다.
- 마찬가지로 단어 규칙에 따라 유효성 검사를 통해 수정, 등록이 가능하게끔 했습니다.
- 이미지 썸네일을 등록할 수 있습니다.
- 등록 시간순 등 3가지 조건을 통해 단어 정렬이 가능합니다.

>
- 단어 조회 및 등록 유효성 검사
![단어 조회 및 등록 유효성 검사](https://velog.velcdn.com/images/darklight_0418/post/8bc0de78-5e19-42e6-9424-973f2fd31ad9/image.gif)
***
- 단어 정렬 기능
![단어 정렬 기능](https://velog.velcdn.com/images/darklight_0418/post/36deb800-c0e7-4886-814e-026c6dcb5e71/image.gif)

#### 유튜브 영상 관리(개념 학습 파트)
개념 학습 파트에서 볼 수 있는 추천 영상들을 등록할 수 있습니다.
- 유튜브 영상 목록 정렬 기능이 제공됩니다.
- 관리자 화면 내에서 등록된 유튜브 영상 중 별도 검색이 가능합니다.
- 영상 등록 시 올바른 링크가 등록되도록 유효성 검사 조건을 설계했습니다.

>
- 유튜브 영상 목록 정렬 기능
![유튜브 영상 목록 정렬 기능](https://velog.velcdn.com/images/darklight_0418/post/3aaff355-d534-4e62-a490-1b5da3fb79b0/image.gif)
***
- 유튜브 영상 검색 기능
![유튜브 영상 검색 기능](https://velog.velcdn.com/images/darklight_0418/post/f804d5b1-e198-4ea3-bf06-79d660a3a23a/image.gif)
***
- 유튜브 영상 등록 및 유효성 검사, 삭제
![유튜브 영상 등록 및 유효성 검사, 삭제](https://velog.velcdn.com/images/darklight_0418/post/66d520bd-11b6-4803-95e7-261b929b1525/image.gif)

***
## 그 외
#### AI 항목 재추천 기능
AI 분석, 추천 항목들은 재추천 버튼을 클릭하면 다시 AI 분석을 통한 재추천 조회 결과가 제공됩니다.

> - 뉴스 해설 AI 재추천 등
![뉴스 해설 AI 재추천](https://velog.velcdn.com/images/darklight_0418/post/1dabee73-8c11-4bbb-84d8-6cac7595a289/image.gif)
***
- AI 학습 단어 추천 등
![AI 학습 단어 추천](https://velog.velcdn.com/images/darklight_0418/post/c42e5ddb-599f-47fd-b6b8-e9362945cde1/image.gif)

---
Ver 0.2 - 2026.02.02.
