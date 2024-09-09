/* 할일 추가 관련 요소 얻어와 변수에 저장 */
const todoSub = document.querySelector("#todoSub");
const todoContent = document.querySelector("#todoContent");
const addBtn = document.querySelector("#addBtn");


// 추가 버튼 #addBtn 버튼 클릭 되었을 때
addBtn?.addEventListener("click", () => {
  // JS의 경우 위에서 오류가 나면 아래가 해석 안 됨
  // addBtn 뒤에 ? 를 넣어 있을 경우에만 실행하고 아니면 넘기는 것으로 수정함
  // Spring EL 에서 사용한 것과 비슷한 용도로 사용 가능!

  // 클릭된 순간 화면에 작성되어있는 제목, 내용 얻어오기
  const title = todoSub.value; // 본인이 작성한 것으로 가져와야 함
  const detail = todoContent.value; // 본인이 작성한 것으로 내용 작성해 꼭!!
 
  // Ajax(비동기) POST 방식으로 요청하기 위한 fetch() API 코드 작성

  // HTTP 통신 시 
  // - headers : 요청 관련 정보를 담음 (어떤 데이터, 어디서 요청 등)
  // - body : POST/PUT/DELETE 요청 시 전달할 데이터를 담는다
  
  fetch("/todo/add", {   // 지정된 주소로 비동기 요청(ajax)
      method : "POST",  // 데이터 전달 방식을 POST로 지정
      headers: {"Content-Type": "application/json"}, // 요청 데이터의 형식을 JSON으로 지정
      body : JSON.stringify( {"todoSub" : title, "todoContent":detail} ) // JS객체를 JSON 형태로 변환하여 Body에 추가
  }) /* stringify : String 화 */
  .then(response => response.text() ) // 요청에 대한 응답 객체(response)를 필요한 형태로 파싱
  // response.text() : 컨트롤러 반환 값을 text형태로 변환해서
  //                   아래 두 번째 then 매개변수로 전달한다는 뜻
 
  .then(result => {
    console.log("result : ", result);

    // 비동기 통신 응답 결과가 1인 경우 (삽입 성공한 경우)
    if (result > 0){
      alert("할일 추가 성공!");

      // 추가하려고 작성한 값 화면에서 지우기 code
      todoSub.value = "";
      todoContent.value = "";
    } else{ // 삽입 실패한 경우
      alert("할일 추가 실패...!");
    }

  }); // 첫 번째 then에서 파싱한 데이터를 이용한 동작 작성

});

// ---------------------------------
/* ajax 기초 연습 - todoNo 일치하는 할 일의 제목 얻어오기 */
const inputTodoNo = document.querySelector("#inputTodoNo");
const searchBtn = document.querySelector("#searchBtn");

// # searchBtn 클릭 시
searchBtn.addEventListener("click", () => {

  // #inputTodoNo 에 작성된 값 얻어오기
  const todoNo = inputTodoNo.value;

  // 비동기로 todoNo를 서버에 전달하고 
  // 해당되는 할일 제목(==값) 가져오기 == fetch - GET방식
  // Get 방식은 주소에 제출할 값이 쿼리스트링 형태로 담긴다!!

  fetch("/todo/searchTitle?todoNo=" + todoNo)
  .then(response => response.text())
  .then(todoTitle => {
    // 매개변수 todoTitle
    // == 서버에서 반환받은 "할일 제목"이 담긴 변수
    alert(todoTitle);
  });

});