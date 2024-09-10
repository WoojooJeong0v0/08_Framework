/* 할일 추가 관련 요소 얻어와 변수에 저장 */
const todoSub = document.querySelector("#todoSub");
const todoContent = document.querySelector("#todoContent");
const addBtn = document.querySelector("#addBtn");


// 추가 버튼 #addBtn 버튼 클릭 되었을 때
addBtn?.addEventListener("click", () => {
  // JS의 경우 위에서 오류가 나면 아래가 해석 안 됨
  // addBtn 뒤에 ? 를 넣어 있을 경우에만 실행하고 아니면 넘기는 것으로 수정함
  // Spring EL 에서 사용한 것과 비슷한 용도로 사용 가능!

  updateLayer.classList.remove("popup-hidden");

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
      // 비동기로 전체 할일 개수 조회해 화면 반영하는 함수, 화면에 반영하기
      getTotalCount();

      // 전체 할 일 목록을 조회해서 화면에 반영(출력)
      selectTodoList();

    } else{ // 삽입 실패한 경우
      alert("할일 추가 실패...!");
    }
  }); // 첫 번째 then에서 파싱한 데이터를 이용한 동작 작성

});


// ---------------------------------------------
/*  전체 Todo 개수 비동기ajax 조회  */ 
function getTotalCount(){
  // 함수 이용하기


  // fetch() API 작성 패치 : 가져오다 (비동기로 요청해서 응답 가져오겠다!)
  // GET방식
 fetch("/todo/totalCount") // 요청하는 주소 (쿼리스트링으로 값 전달 가능하지만 지금은 전달할 게 없음)
 // 위의 주소만 실행하면 비동기 요청 수행 -> Promise 객체 반환됨 (응답값 또는 에러 내용, 또는 성공 메시지 등 비동기 값)
 .then(response => {
  console.log(response); // Promise가 두 번째 리스폰즈에 들어온다고 생각하면 됨 (비동기 요청에 대한 응답 담긴 객체)

  // 비동기 요청에 대한 응답 문제가 없을 경우
  // == 비동기 요청 성공 시
  // if(response.status === 200) // 아래 if문과 같은 의미
  if(response.ok){

    //response.text(); 응답 결과 데이터를 
    // text(String)형태로 변환(parsing)
    return response.text();
  }

  // 예외 강제 발생
  throw new Error("비동기 통신 실패!");
 })
 
  // 첫 번째 then에서 반환된 값을 매개변수 저장한 후 수행되는 구문
 .then(totalCount => {
  console.log("totalCount : ", totalCount);

  // #totalCount 요소 내용으로 비동기 통신 결과를 대입 
  document.querySelector("#totalCount").innerText = totalCount;
 })
 
 // 첫 번째 then에서 던져진 error 잡아서 처리하는 구문
 .catch( e => console.error(e));
}

// --------------------------------------------------

/* 완료된 할일 개수 비동기로 조회 */
function getCompleteCount(){

  /*
  첫 번째 then
  - 비동기 요청 결과(응답, response)에 따라
    어떤 코드를 수행할지 제어하는 부분
  - 매개변수 response : 응답 결과, HTTP 상태코드, 요청 주소 등이 담긴 객체
  */

  /*
  두 번째 then
  - 비동기 요청으로 얻어온 값을 이용해서 수행될 JS 코드 작성하는 구문(영역)
  */

  fetch("/todo/completeCount") // 비동기 요청해서 결과 데이터 응답 받기
  .then(response => {
    if(response.ok){ // 비동기 통신 성공 시, 상태코드 200번일 때
      return response.text(); // response에서 응답 결과를 text로 변환해서 반환 -> 두 번째 then으로 이동
    }

    // 요청 실패 시 예외 (에러) 던지기
    throw new Error("완료된 할 일 개수 조회 실패");
  })

  .then(count => {
    console.log("완료된 할 일 개수 :" , count);
    document.querySelector("#completeCount").innerText = count;
  })

  .catch(e => {console.error(e)});

}

// ----------------------------------------------

// 비동기로 할 일 목록 전체 조회
function selectTodoList(){
  fetch("/todo/todoList")
  .then(response => {
    if(response.ok) return response.text();
    throw new Error("목록 조회 실패 :" + response.status);
  })
  .then(result => {
    // JSON(List 형태) -> JS Array 객체 배열
    const todoList = JSON.parse(result);
    console.log(todoList);

    /* #tbody 내용 모두 지운 후 조회된 내용 요소 추가하기 */
    const tbody = document.querySelector("#tbody");
    tbody.innerHTML ="";

    // JS 향상된 for문 : for(요소 of 배열){}
    for(let todo of todoList){
      // 1) todoNo가 들어갈 th 요소 생성
      const todoNo = document.createElement("th");
      todoNo.innerText = todo.todoNo;

      // 2) todoSub 들어갈 td, a 요소 생성
      const todoSub = document.createElement("td");

      const a = document.createElement("a");
      a.innerText = todo.todoSub;
      a.href = `/todo/todoContent/${todo.todoNo}`;

      // a 요소롤 todoSub td 요소 자식으로 추가
      todoSub.append(a);

      // 만들어진 a요소가 클릭 되었을 때
      a.addEventListener("click", e => {
        // e : 이벤트 객체 (이벤트 관련 모든 정보가 담김)
        // e.preventDefault() : prevent가 막는다 는 뜻 -> 요소 기본 이벤트 막기
        // -> a태그의 클릭 이벤트를 막아버림
        e.preventDefault();

        // 할일 상세 조회 비동기 요청
        // e.target.href : 클릭된 a 태그 href 속성 값 얻어오기
        selectTodo(e.target.href);
      })

      // 3) todoCompl 완료 여부 들어갈 th 요소 생성
      const todoCompl = document.createElement("th");
      todoCompl.innerText = (todo.todoCompl == 'O') ? 'O' : 'X';

      // 4) todoDate 들어갈 td 요소 생성
      const todoDate = document.createElement("td");
      todoDate.innerText = todo.todoDate;

      // 5) tr 요소 만들어서 1) ~ 4) 에서 만든 요소 자식으로 추가
      const tr = document.createElement("tr");
      tr.append(todoNo, todoSub, todoCompl, todoDate);

      // 6) tbody 에 tr 요소 자식 추가
      tbody.append(tr);
    }
  })
  .catch(e=>console.error(e));
}

/* 페이지 로딩 완료된 후 todo제목인 a 태그 클릭 막기 */
document.addEventListener("DOMContentLoaded", ()=>{ // 띄어쓰기 : 후손 모두
  //DOMContentLoaded : DOM 이라고 하는 화면 모든 콘텐츠를 재적재
  // 화면이 모두 로딩된 후 라는 뜻

  document.querySelectorAll("#tbody a").forEach( (a) => {
    // 매개변수 a : 반복마다 하나씩 요소가 꺼내져서 저장되는 변수

    // a 기본 이벤트 막고 selectTodo() 호출하게 하기
    a.addEventListener("click", e => {
      e.preventDefault();
      selectTodo(e.target.href);
    })
  })  
});

// ----------------------

/** 비동기로 할 일 상세조회 하여 팝업 레이어에 출력하기
 * @param url : /todo/detail/10 등의 형태
 */
function selectTodo(url){
  fetch(url)
  .then(response => {
    if(response.ok) { // 요청 응답 성공 시
      // response.json() 
      //  -> response.text() + JSON.parse() 합친 메서드
      //    -> (문자 형태 변환) + (JSON->JS Object 변환) 동시에
      return response.json();
    }
    throw new Error("상세 조회 실패 : " + response.status);
  })
  .then(todo => {
    console.log(todo);

    /* 팝업 레이어에 조회된 todo 내용 추가하기 */
    const popupTodoNo = document.querySelector("#popupTodoNo");
    const popupTodoTitle = document.querySelector("#popupTodoTitle");
    const popupComplete = document.querySelector("#popupComplete");
    const popupRegDate = document.querySelector("#popupRegDate");
    const popupTodoContent = document.querySelector("#popupTodoContent");

    // 완료 여부 
    popupComplete.innerText = todo.todoCompl == 'O' ? 'O' : 'X';
    popupTodoNo.innerText = todo.todoNo;
    popupTodoTitle.innerText = todo.todoSub;
    popupRegDate.innerText = todo.todoDate;
    popupTodoContent.innerText = todo.todoContent;

    // 팝업 레이어 보이게 하기
    // -> 클래스 중 popup-hidden 제거
    document.querySelector("#popupLayer").classList.remove("popup-hidden");
  })
  .catch(err => console.error(err));
}

// 팝업 레이어 X 버튼 클릭 시 닫기
document.querySelector("#popupClose").addEventListener("click", () => {
  document.querySelector("#popupLayer").classList.add("popup-hidden");
});

// -------------------------------------------------

/* 완료 여부 변경 버튼 클릭 시 */ 
const changeComplete = document.querySelector("#changeComplete")
changeComplete.addEventListener("click", () => {
  // 팝업 레이어에 작성된 todoNo, todoCompl 에 적힌 내용 얻어 오기
  const todoNo = document.querySelector("#popupTodoNo").innerText;

  // 비동기로 완료 여부 변경
  /* Ajax (비동기)요청 시 사용 가능한 데이터 전달 방식
  REST API와 관련됨
  GET    : 조회 (SELECT)
  POST   : 삽입 (INSERT)
  PUT    : 수정 (UPDATE)
  DELETE : 삭제 (DELETE)
  */

  fetch("/todo/todoComplete", {
    method : "PUT", // PUT 방식 요청 콤마 필수
    headers : {"Content-Type" : "application/json"},
    // 제출되는 데이터는 json 형태라고 정의
    body : todoNo // todoNo 번호
  })
  .then(response => {
    if(response.ok) return response.text();

    throw new Error("완료 여부 변경 실패!" + response.status)
  })
  .then(result => {
    console.log(result);
    // 팝업 레이어 완료 여부 부분 O <-> X 겨경
    const todoCompl = document.querySelector("#popupComplete");
    todoCompl.innerText = (todoCompl.innerText == 'O') ? 'X' : 'O';

    // 완료된 할일 개수를 비동기로 조회하는 함수 호출
    getCompleteCount();

    // 할일 목록을 비동기로 조회하는 함수 호출
    selectTodoList(); // 호출하면 알아서 변경됨 (이미 만들어놔서..)
  })
  .catch(err => console.error(err));
 
});

// ---------------------------------------

// 할일 삭제
const deleteBtn = document.querySelector("#deleteBtn");

deleteBtn.addEventListener("click", () => {

  if(!confirm("삭제할까요?")) return;

  // 삭제할 할일 번호 얻어오기
  const todoNo = document.querySelector("#popupTodoNo").innerText;

  // 비동기로 삭제 요청 하기 (DELETE 방식 요청)
  fetch("/todo/todoDelete", {
    method : "DELETE",
    headers : {"Content-Type" : "application/json"},
    body : todoNo
  })
  .then(response => {
    if(response.ok) return response.text();
    throw new Error("삭제 실패 : " + response.status);
  })
  .then(result => {
    // 팝업 닫기 
    document.querySelector("#popupLayer").classList.add("popup-hidden");

    // 전체 목록 수, 할일 완료 개수, 할일 목록 다시 조회
    getTotalCount();
    getCompleteCount();
    selectTodoList();
  })
  .catch( err => console.error(err));

});

// --------------------------

// 수정관련 요소 얻어오기
const popupLayer = document.querySelector("#popupLayer");
const updateLayer = document.querySelector("#updateLayer");

// 수정 레이어 열기
const updateView = document.querySelector("#updateView");

// 수정 비동기 요청 버튼
const updateBtn = document.querySelector("#updateBtn");
const updateCancel = document.querySelector("#updateCancel");

// 수정 레이어 열기
updateView.addEventListener("click", () => {
  popupLayer.classList.add("popup-hidden"); // 팝업 숨기기
  updateLayer.classList.remove("popup-hidden");  // 수정 레이어 보이기

  // 상세 조회 제목/내용
  const todoSub = document.querySelector("#popupTodoTitle").innerText;
  const todoContent = document.querySelector("#popupTodoContent").innerHTML;

  // 수정 레이어 제목/내용 대입
  document.querySelector("#updateTitle").value = todoSub;
  document.querySelector("#updateContent").value = todoContent.replaceAll("<br>", "\n");
  // 줄바꿈 문자로 변경!! (텍스트 에리에에선 br 태그가 안 먹히니까..)

  // 수정 버튼 (#updateBtn)에 todoNo(PK값) 숨겨놓기
  // - dataset 속성 : 요소에 js 사용할 값(data)를 추가하는 속성
  // 요소.dataset.속성명 = "값"; -> 대입
  // 요소.dataset.속성명         -> 값 얻어오기
  updateBtn.dataset.todoNo = document.querySelector("#popupTodoNo").innerText;
})

// 수정 취소 시
updateCancel.addEventListener("click", () => {
  popupLayer.classList.remove("popup-hidden"); // 팝업 보이기
  updateLayer.classList.add("popup-hidden");  // 수정 레이어 숨기기
});


// 수정 버튼 (#updateBtn) 클릭 시
updateBtn.addEventListener("click", () => {

  // 서버로 제출되어야 하는 값을 JS 객체 형태로 묶기
  const obj = {}; // 빈 객체 생성
  obj.todoNo = updateBtn.dataset.todoNo; // 버튼에 dataset 값 얻어오기

  obj.todoSub = document.querySelector("#updateTitle").value;
  obj.todoContent = document.querySelector("#updateContent").value;

  console.log(obj);

  // 비동기로 할일 수정 요청
  fetch("/todo/todoEdit", {
    method : "PUT",
    headers : {"Content-Type" : "application/json"},
    body : JSON.stringify(obj)
    // obj 객체를 JSON 문자열 형태로 변환해서 제출함
  })
  .then(response => {
    if(response.ok) return response.text();
    throw new Error("수정 실패 : " + response.status);
  })
  .then(result => {
    console.log(result); // 1 or 0

    if(result > 0){
      // 수정 성공
      alert("수정 성공!!");
      // 상세 조회하는 팝업 레이어 보이게 해야 함
      // 수정 레이어 숨기기
      updateLayer.classList.add("popup-hidden");

      // 상세 조회 (팝업 레이어 오픈) 함수 호출
      selectTodo("/todo/todoContent/" + updateBtn.dataset.todoNo);

      // 목록 조회 다시 (제목 바뀐 경우)
      selectTodoList();

    } else {
      // 수정 실패
      alert("수정 실패 ㅠㅠ");
    }
  })
  .catch( err => console.error(err) );
});

