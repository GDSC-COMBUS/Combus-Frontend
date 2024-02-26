# 🚌 Combus Frontend

## 🗂️ 앱 종류
- 사용자용 앱 - MyApplication
- 버스 운전자용 앱 - ComBus_DriverApp
### 📱 사용자용 앱
시각장애인과 휠체어 이용객을 위한 예약 서비스

**기능**
- 위치를 통한 탑승 정류장 및 하차 정류장 선택
- 버스 탑승 예약, 음성 예약 및 음성 서비스
- 카메라를 통한 버스 번호 확인 서비스
 <Br/>
  
### 📱 버스 운전자용 앱
버스 예약자 확인 서비스

**기능**
- 버스 실시간 위치 확인 및 자동 스크롤
- 다음 정류장 예약 내역 팝업
 <Br/>

## 🎬 사용방법
### 📱 사용자용 앱
**일반 예약 방법**
1. 사용자 코드를 입력하여 `START`를 눌러 로그인한다. (사용자 코드 : 1111 / 2222 / ... / 9999 / 1010)
2. `CREATE`를 눌러 현재 위치를 기준으로 주변에 있는 정류장 중에 탑승 정류장을 선택한다.
3. 해당 정류장을 지나가는 버스 리스트 중 탑승 버스를 선택한다.
4. 해당 버스의 정류장들 중 하차 정류장을 선택한다.
5. 예약 완료

**음성 예약 방법**
1. `VOICE`를 눌러 사용자 코드를 음성으로 말하고 `START`버튼을 눌러 로그인한다.
2. `CREATE WITH VOICE`를 눌러 음성 예약를 시작한다.
3. 음성 설명을 듣고 `START`를 눌러 음성 인식을 한다.
4. 탑승 정류장과 버스, 하차 정류장을 지정하고 예약을 마친다.

**예약 후 버스 번호 확인 방법**
1. `WEBCAM FOR BUS VERIFICATION`를 눌러 카메라 화면을 확인한다.
2. 버스가 도착하면 `START CAPTURE`을 눌러 영상 녹화를 시작하고 `STOP CAPTURE`을 눌러 영상 녹화를 멈춘다.
3. `START CAPTURE` 버튼 아래에 뜬 정보를 확인한다.

**예약 후 사용 방법**
1. 탑승하면 `BOARDING CONFIRM`을 눌러 Status를 변경시킨다.
2. 하차할때 `DROPPING OFF CONFIRM`을 눌러 Status를 변경시키면 예약 목록이 사라진다.

### 📱 버스 운전자용 앱
**위치 및 예약 정보 확인 방법**
1. 운전자 코드를 입력하여 `LOGIN`을 눌러 로그인한다. (운전자 코드 : 5555)
2. 자동으로 스크롤 되며 현재 버스 위치를 보여주는 노란 버스 모양을 확인한다. (30초마다 자동 새로고침)
3. 정류장별로 있는 예약 정보를 확인한다. <br/>
   (Reservation : 탑승예약 / Drop off : 하차 예약 / 휠체어 예약자 존재시 오른쪽에 휠체어 모양)
4. 정류장을 눌러 각 정류장에 예약되어있는 탑승 정보와 하차 정보를 확인한다.
5. 홈 화면에서 현재 버스 위치 기준 다음 정류장 예약자가 존재시 팝업이 뜨고 10초 후 자동 닫힘 또는 X를 눌러 닫는다.

## 👥 멤버 구성
#### 👩‍💻 [강신영](https://github.com/sinyoung6491)
#### 👩‍💻 [정유나](https://github.com/13b13)
 <Br/>

## 🎨 디자인
#### 🖼️ [피그마](https://www.figma.com/file/pKEx9GyBsCvqL84lFxsSZI/2024-Google-Solution-Challenge---%EC%9E%A5%EC%95%A0%EC%9D%B8-%EB%B2%84%EC%8A%A4-%EB%8F%84%EC%9A%B0%EB%AF%B8?type=design&node-id=3%3A163&mode=design&t=KuWlg1gsUkA1h4xp-1)
 <Br/>
 
## 🖥️ 사용 프로그램
- 안드로이드 스튜디오

💿[설치 방법](https://developer.android.com/codelabs/basic-android-kotlin-compose-install-android-studio?hl=ko#0)

