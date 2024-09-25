/* 다음 주소 API로 주소 검색하기 */

function findAddress() {
  new daum.Postcode({
    oncomplete: function (data) {
      // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

      // 각 주소의 노출 규칙에 따라 주소를 조합한다.
      // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
      var addr = ''; // 주소 변수
      

      //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
      if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
        addr = data.roadAddress;
      } else { // 사용자가 지번 주소를 선택했을 경우(J)
        addr = data.jibunAddress;
      }

      // 우편번호와 주소 정보를 해당 필드에 넣는다.
      document.getElementById('postcode').value = data.zonecode;
      document.getElementById("address").value = addr;
      // 커서를 상세주소 필드로 이동한다.
      document.getElementById("detailAddress").focus();
    }
  }).open();
}

/* 주소 검색 버튼 클릭 시*/
if(document.querySelector("#findAddressBtn") !== null ){
  document.querySelector("#findAddressBtn")
    .addEventListener("click", findAddress);
}

// 함수명만 작성하는 경우 : 함수 코드 반환 된다!! (함수를 매개변수로 전달하는 부분 다시 확인)
// 함수명() 작성하는 경우 : 함수에 정의된 내용을 실행한다는 뜻

// -------------------------------------------

/* =====유효성 검사 Validation===== */

// 입력 값이 유효한 형태인지 표시하는 객체(체크리스트)
const checkObj = {
  "memberNickname" : true, // 이 페이지 들어온 건 유효했다
  "memberTel" : true // 위에 꼭 콤마 찍어야 함!!
};

/* 닉네임 검사 */
// 조건
// - 3글자 이상
// - 중복 안 됨
const memberNickname = document.querySelector("#memberNickname");


// 객체?.속성 -> ? : 안전탐색연산자
//  - 객체가 null 또는 undefined가 아니면 수행해라, 라는 뜻!

// 기존 닉네임 (함수 바깥에 있어서 바로 해석됨)
const originalNickname = memberNickname?.value;

memberNickname?.addEventListener("input", () => {
  // input 이벤트 : 입력과 관련된 모든 동작
  // 키보드 입력, 마우스 클릭, 붙여넣기 같은 모든 것 (JS 입력(값세팅)은 인식 안 됨)

  // 입력된 값 얻어오기 (양쪽 공백 제거)
  const inputValue = memberNickname.value.trim();

  if(inputValue.length < 3){ // 3글자 미만인 경우
    // 클래스 제거
    memberNickname.classList.remove("green");
    memberNickname.classList.remove("red");

    // 닉네임 유효하지 않다고 기록해둠
    checkObj.memberNickname = false;

    return;
  }

  // 입력된 닉네임이 기존 닉네임과 같을 경우
  if (originalNickname === inputValue){
      // 클래스 제거
      memberNickname.classList.remove("green");
      memberNickname.classList.remove("red");
  
      // 닉네임 유효하다고 기록함!! (기존 것은 문제가 없으니까)
      checkObj.memberNickname = true;
        return;
  }

  /* 닉네임 형식 유효성 검사 */
  // 영어 또는 숫자 또는 한글만 작성 가능
  // 3~10글자 사이

  const lengthCheck = inputValue.length >= 3 && inputValue.length <= 10;
  const validCharactersCheck = /^[a-zA-Z0-9가-힣]+$/.test(inputValue); // 영어, 숫자, 한글만 허용
  // ^ : 시작
  // $ : 끝
  // [] : 한 칸(한 문자)에 들어갈 수 있는 문자 패턴 기록
  // + : 1 개 이상

  // 조건이 하나라도 false인 경우
  if (!(lengthCheck && validCharactersCheck)){ // === false 등
    // ==  if( (lengthCheck && validCharactersCheck) === false )
    memberNickname.classList.remove("green");
    memberNickname.classList.add("red");

    // 닉네임이 유효하지 않다고 기록
    checkObj.memberNickname = false;
    return;
  }

  // 3글자는 넘었다는 뜻!
  // 이제 중복검사 해야 함
  // 비동기 입력된 닉네임이
  // DB에 존재하는지 확인하는 Ajax(fetch 사용) 코드
  // 존재하는지 확인 후 결과를 얻어오겠다는 조회니까 GET으로 하는 것임!

  // GET 방식 요청 (쿼리스트링으로 파라미터 전달)
  // 얘는 상대경로가 안 적히나? 질문
  fetch("/myPage/checkNickname?input=" + inputValue)
  .then(response => {
    if (response.ok) { // 응답 상태코드 200(성공)인 경우
      return response.text(); // 응답 결과 text로 변환
      // 이 구문은 아래 then으로 이동
    }

    throw new Error("중복 검사 실패 : " + response.status); // catch로 이동
  }) // fatch 실행면면 얻는 결과값
  .then(result => {
    // result == 첫 번째 then에서 return 된 값
    // back 단 작성하고 돌아옴!
  if (result > 0){ // 중복인 경우
    memberNickname.classList.add("red");
    memberNickname.classList.remove("green");

    checkObj.memberNickname = false; // 체크리스트에 false 기록함
    return;
  }
  // 중복 아닌 경우
  memberNickname.classList.add("green");
  memberNickname.classList.remove("red");
  checkObj.memberNickname = true;
  })
  .catch(err => console.error(err));


});

// ------------------------------------------------------------
/*전화번호 유효성 검사 */

const memberTel = document.querySelector("#memberTel");
memberTel?.addEventListener("input", ()=>{

  // 입력된 전화번호 값 (중복 검사까지는 안 함)
  const inputTel = memberTel.value.trim();

  // 전화번호 정규식 검사
  // 010으로 시작하고 11글자
  const validFormat = /^010\d{8}$/; 

  // 입력 받은 전화 번호가 유효한 형식이 아닌 경우
  if(!validFormat.test(inputTel)){
    memberTel.classList.add("red");
    memberTel.classList.remove("green");
    checkObj.memberTel = false;
    return;
  }

  // 유효한 경우
  memberTel.classList.add("green");
  memberTel.classList.remove("red");
  checkObj.memberTel = true;

});

// ------------------------------------------------------------

/* 내 정보 수정 form 제출 시 */
const updateInfo = document.querySelector("#updateInfo");
updateInfo?.addEventListener("submit", e => {
  
  // 닉네임과 번호는 이미 check 리스트에서 진행함
  // checkObj에 작성된 값 검사하기

  // 향상된 for문과 비슷함
  // for ~ in 구문 : JS 객체 key 값을 하나씩 접근하는 반복문
  for(let key in checkObj){
    // 닉네임, 전화번호 중 유효하지 않은 값이 있을 경우
    if(checkObj[key] === false){

      let str = " 유효하지 않습니다";
      switch(key){
        case "memberNickname" : str = "닉네임이" + str; break;
        case "memberTel"      : str = "전화번호가" + str;  break;
      }

      alert (str); // 000이 유요하지 않습니다 출력
      e.preventDefault(); // form 제출 막기
      document.getElementById(key).focus(); // focus 맞추기
      return;
    } 
    
  } // for in end

  /* 주소 유효성 검사 */
  // - 모두 작성 또는 모두 미작성
/*   const postcode = document.querySelector("#postcode").value.trim();
  const address = document.querySelector("#address").value.trim();
  const detailAddress = document.querySelector("#detailAddress").value.trim(); */
  const addr = document.querySelectorAll("[name = memberAddress]");

  let empty = 0; // 비어있는 input의 개수
  let notEmpty = 0; // 비어있지 않은 input의 개수

  // for ~ of 향상된 for문
  for(let item of addr){
    let len = item.value.trim().length; // 작성된 값의 길이를 얻어옴
    if (len > 0) notEmpty++; // 비어있지 않을 경우
    else         empty++; // 비어있을 경우
  }

  // empty, notempty 중 3이 하나도 없을 경우
   if(empty < 3 && notEmpty < 3){ 
    // if( empty < 3 && notEmpty < 3 ){
    alert("주소가 유효하지 않습니다");
    e.preventDefault();
    return;
   }

});

// ------------------------------------------------------------

/* 비밀번호 변경 */
const changePw  = document.querySelector("#changePw");

// 비밀번호 변경하기 버튼 클릭 시 조건 따지기
// 또는 form 태부 내부에서 엔터 입력 시
// == 둘다 submit (제출) 하는 경우
changePw?.addEventListener("submit", e => {

 /*  e.preventDefault();  */// prevent는 막다는 뜻, 기본 이벤트를 막는다
  // form의 기본 이벤트인 제출을 막는다! (제출버튼 누르면 일단 제출을 막음)
  // -> 유효성 검사 조건이 만족되지 않을 때 수행함

  // 입력 요소 모두 얻어오기
  const currentPw = document.querySelector("#currentPw");
  const newPw = document.querySelector("#newPw");
  const newPwConfirm = document.querySelector("#newPwConfirm");

  // 1. 현재 비밀번호 , 새 비밀번호 , 새 비밀번호 확인
  // 입력 여부 체크

  let str; // undefined 상태
  if(newPwConfirm.value.trim().length == 0)
    str = "새 비밀번호 확인을 위해 다시 입력해주세요";
  if(newPw.value.trim().length == 0)
    str = "새 비밀번호를 입력해 주세요";
  if(currentPw.value.trim().length == 0)
    str = "현재 비밀번호를 입력해 주세요";

  if(str !== undefined){ // undefined가 아니었다! 입력되지 않은 값이 존재한다는 뜻
    alert(str);
    e.preventDefault(); // form 제출 막기
    return; // 이미 틀려서 다른 검사 안 하기 (submit 이벤트핸들러 종료)
  } // 첫 번째 조건 완료

  // 2. 새 비밀번호가 알맞은 형태로 작성 되었는가 확인
  // - 영어(대소문자 가리지 X) 1글자 이상
  // - 숫자 1글자 이상
  // - 특수문자 (! @ # _ -) 1글자 이상
  // - 최소 6글자 최대 20글자

  /* 정규 표현식 regEx */
  //  https://developer.mozilla.org/ko/docs/Web/JavaScript/Guide/Regular_expressions
  // 이거 매우 잘 쓰면!! 좋다!! (익히기는 어려워)

  // ex ) 숫자가 3개 이상 작성된 문자열 조합 찾기
   // "12abc" -> X
   // "444" -> O
   // "1q2w3e" -> O

     /* [JS 정규 표현식 객체 생성 방법]
  1. /정규표현식/
  2. new RegExp("정규표현식")
  https://regexr.com/
  */

  /**    // 비밀번호 길이 체크
    if (password.length < 6 || password.length > 20) {
        return "비밀번호는 6자 이상 20자 이하이어야 합니다.";
    }

    // 각 조건을 확인하기 위한 정규 표현식
    const hasUppercase = /[A-Z]/.test(password);
    const hasLowercase = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecialChar = /[!@#_-]/.test(password);

    // 모든 조건이 충족되었는지 확인
    if (hasUppercase || hasLowercase) {
        if (hasNumber && hasSpecialChar) {
            return "비밀번호가 유효합니다.";
        } else {
            return "비밀번호는 숫자와 특수문자를 포함해야 합니다.";
        }
    }

    return "비밀번호는 영어 대소문자를 포함해야 합니다."; */

    const lengthCheck = newPw.value.length >= 6 && newPw.value.length <= 20;
    const letterCheck = /[a-zA-Z]/.test(newPw.value); // 영어 알파벳 포함
    const numberCheck = /\d/.test(newPw.value); // 숫자 포함
    const specialCharCheck = /[\!\@\#\_\-]/.test(newPw.value); // 특수문자 포함
  
    // 조건이 하나라도 만족하지 못하면
    if ( !(lengthCheck && letterCheck && numberCheck && specialCharCheck) ) {
      alert("영어, 숫자, 특수문자 1글자 이상, 6~20자 사이로 입력해주세요")
      e.preventDefault();
      return;
    }

  // 3. 새 비밀번호, 새 비밀번호 확인이 같은지 체크
  if(newPw.value !== newPwConfirm.value){ // 입력된 값이 다르면?
    alert("새로 입력한 비밀번호가 일치하지 않습니다")
    e.preventDefault();
    return;
  }

});


// ------------------------------------

/* 회원 탈퇴 유효성 검사 */
const secession = document.querySelector("#secession");
secession?.addEventListener("submit", e => {

    // 1) 비밀번호 입력 확인
    const memberPw = document.querySelector("#memberPw");
    if(memberPw.value.trim().length === 0){ // 미입력
      alert("비밀번호 입력해 주세요")
      e.preventDefault();
      return;
    }

    // 2) 체크 되었는지 검사 
    const agree = document.querySelector("#agree");

    if(agree.checked === false){ // 체크가 되어있지 않은 경우
      alert("회원 탈퇴를 진행하려면 약관 동의 체크해 주세요")
      e.preventDefault();
      return;
    }

    // 3) confirm 이용해서 탈퇴확인
    if(confirm("정말 탈퇴하시겠습니까?") === false) {
      // 취소 클릭 시
      alert("탈퇴 취소")
      e.preventDefault();
      return;
    }
  
});