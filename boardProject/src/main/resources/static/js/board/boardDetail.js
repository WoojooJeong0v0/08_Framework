// 현재 상세 조회한 게시글 번호
const boardNo = location.pathname.split("/")[3]; 

/* 좋아요 하트 클릭 시 */
const boardLike = document.querySelector("#boardLike");
boardLike.addEventListener("click", e => {
  // 1. 로그인 여부 검사
  if(loginCheck === false){
    alert("로그인 후 이용해 주세요");
    return;
  }

  // 2. 비동기로 좋아요 요청
  // Rest API... GET/POST/PUT/DELETE 지키면 좋지만
  // 왔다갔다 하니까 POST 로 진행

  fetch("/board/like", {
    method : "POST",
    headers : {"Content-Type" : "application/json"},
    body : boardNo
  })
  .then(response => {
    if(response.ok) return response.json();
    throw new Error("좋아요 실패");
  })
  .then(result=> {
    // console.log("result :", result);

    // 좋아요 결과가 담긴 result 객체의 check 값에 따라
    // 하트 아이콘 비우기/채우기 지정
    
    if(result.check === 'insert'){
      boardLike.classList.add("fa-solid");
      boardLike.classList.remove("fa-regular");
    } else { // 하트 비우기
      boardLike.classList.add("fa-regular");
      boardLike.classList.remove("fa-solid");
    }

    // 좋아요 하트 다음 형제 요소의 내용을 
    // result.count 로 변경하기
    boardLike.nextElementSibling.innerText = result.count;
  })
  .catch(err=>console.error(err));

});

// ----------------------------------------------

/* 삭제 버튼 클릭 시
- 삭제 버튼 클릭 시 "정말 삭제 하시겠습니까?" confirm()

- /editBoard/delete, POST 방식, 동기식 요청
  -> form 태그 생성 + 게시글 번호가 세팅된 input 
  -> body 태그 제일 아래 추가해서 submit()

- 서버(Java 백엔드)에서 로그인한 회원의 회원 번호 얻어와서
  로그인한 회원이 작성한 글이 맞는지 SQL에서 검사
*/

// 삭제 버튼 요소 얻어 오기
const deleteBtn = document.querySelector("#deleteBtn");

// 삭제 버튼 존재할 때 이벤트 리스너 추가
deleteBtn?.addEventListener("click", () => {

  if(confirm("정말 삭제 하시겠습니까?") == false) return;

  const url = "/editBoard/delete"; // 요청 주소
  // 게시글 번호 == 전역 변수 boardNo 사용

  // form 태그 생성
  const form = document.createElement("form");
  form.action = url;
  form.method = "POST";

  // input 태그 생성
  const input = document.createElement("input");
  input.type = "hidden";
  input.name = "boardNo"; // 파라미터 꺼내오는 key값
  input.value = boardNo; // 전역 변수 값 사용

  form.append(input); // form 자식으로 input 추가

  // body 자식으로 form 추가
  document.querySelector("body").append(form);
  
  form.submit(); // 제출

});

// ---------------------------------------------

/* 게시글 수정 버튼 클릭 시
 - /editBoard/{boardCode}/{boardNo}/updateView, POST, 동기식
  -> form 태그 생성
  -> body 태그 제일 아래 추가해서 submit()

  - 서버(Java)에서 로그인한 회원 번호를 얻어와서
   현재 로그인한 회원이 작성한 글이 맞을 경우
   해당 글을 상세조회 한 후
   수정 화면으로 전환
*/

const updateBtn = document.querySelector("#updateBtn");
updateBtn?.addEventListener("click", ()=>{
  const form = document.createElement("form");

  ///editBoard/{boardCode}/{boardNo}/updateView
  form.action = location.pathname.replace("board","editBoard") + "/updateView";
  form.method = "POST";

  document.querySelector("body").append(form);
  form.submit();
  // 파라미터 없어도 PathVariable 상태 2개로 제출
});