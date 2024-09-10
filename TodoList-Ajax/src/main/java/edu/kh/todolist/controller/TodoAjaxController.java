package edu.kh.todolist.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.todolist.dto.Todo;
import edu.kh.todolist.service.TodoListService;
import lombok.extern.slf4j.Slf4j;

/* @RequestBody
 * - 비동기 요청(ajax) 시 전달되는 데이터 중
 *   body 부분에 포함된 요청 데이터를
 *   알맞은 Java 객체 타입으로 바인딩하는 어노테이션
 * 
 * (쉬운 설명)
 * - 비동기 요청 시 body 에 담긴 값을
 *   알맞은 타입으로 변환해서 매개 변수에 저장
 * */

/* @ResponseBody
 * - 컨트롤러 메서드의 반환 값을
 *   HTTP 응답 본문에 직접 바인딩하는 역할임을 명시
 *  
 * (쉬운 해석)  
 * -> 컨트롤러 메서드의 반환 값을
 *  비동기 (ajax)요청했던 
 *  HTML/JS 파일 부분에 값을 돌려 보낼 것이다를 명시
 *  
 *  - forward/redirect 로 인식 X
 * */

/* [HttpMessageConverter]
 *  Spring 에서 비동기 통신 시
 * - 전달되는 데이터의 자료형
 * - 응답하는 데이터의 자료형
 * 위 두가지 알맞은 형태로 가공(변환)해주는 객체
 * 
 * - 문자열, 숫자 <-> TEXT
 * - Map <-> JSON
 * - DTO <-> JSON
 * 
 * (참고)
 * HttpMessageConverter가 동작하기 위해서는
 * Jackson-data-bind 라이브러리가 필요한데
 * Spring Boot 모듈에 내장되어 있음
 * ( Jackson : 자바에서 JSON 다루는 방법 제공하는 라이브러리)
 */


@Slf4j
@Controller
@RequestMapping("todo")
public class TodoAjaxController {

	@Autowired // 의존성 주입
	private TodoListService service;
	
	
	/**
	 * 비동기로 할 일 추가
	 * @param todo : @RequestBody 이용
	 * 							 전달 받은 JSON 형태 (String) 데이터를 
	 * 							 Todo 객체로 변환
	 * @return
	 */
	@ResponseBody
	@PostMapping("add")
	public int todoAdd( // 반환형을 알맞은 형태로 변경해야 함 (처음엔 String 으로 작성함)
			@RequestBody Todo todo // 전달된 데이터를 알맞은 타입으로 받아줘서 커맨드객체처럼 변함
			) {
		
		log.debug("todo : {}", todo);
		
		// service 호출 후 결과 반환 받기
		int result = service.todoAdd(todo);
		
		/* 비동기 통신의 목적 : 값 또는 화면 일부만 갱신 없이
		 * 서버로부터 응답 받고 싶을 때 사용함!!! (대부분 값 받기 위해서) */
		return result; // service 수행 결과 그대로 반환
	}
	
	
	/**
	 * 
	 * @param todoNo : GET 방식 요청은 body가 아닌 주소에 담겨 전달된 "파라미터"
	 * 								 그래서 Request Param으로 얻어온다!
	 * @return 검색된 제목
	 */
	//ajax 기초 연습 - todoNo 일치하는 할 일의 제목 얻어오기
	@ResponseBody // 비동기 요청한 JS 본문으로 값 반환
	@GetMapping("searchTitle")
	public String searchTitle(
			@RequestParam("todoNo") int todoNo
			) {
		
		String todoTitle = service.searchTitle(todoNo);
		
		// 서비스 결과를 "값" 형태 그대로 JS본문으로 반환할 것
		return todoTitle;
	}
	
	
	/**
	 * 
	 * @return 전체 할 일 개수 반환
	 */
	@ResponseBody // 반환 값을 요청한 JS 코드에 값만!!! 돌려보내라
	@GetMapping("totalCount")
	public int getTotalCount() {
		return service.getTotalCount();
	}
	
	
	@ResponseBody // 호출한 ajax 코드로 값 자체를 반환 (forward X)
	@GetMapping("completeCount")
	public int getCompleteCount() {
		return service.getCompleteCount();
	}
	
	
	/**
	 * 할일 상세 조회
	 * @param todoNo
	 * @return
	 */
	@ResponseBody
	@GetMapping("todoContent")
	public /*String*/ Todo todoContent(@RequestParam("todoNo") int todoNo)  // 쿼리스트링에 넣어 파라미터로 전달 받은 것이니 리퀘 파람으로
	{
		
		/*반환형이 스트링인 경우*/
		// Todo 객체를 JS 로 넘기면 다룰 수 없기 때문에 JSON 을 이용해야 함!!
		// ((강사님 필기)) 반환형 String 인 경우 
		// - Java 객체는 JS 호환이 안 됨
		// -> Java에서 JS에 호환되도록 JSON 형태 데이터 반환
		
//		return "{\"todoNo\":17, \"todoSub\":\"제목 테스트\"}";
		
		/*반환형 Todo(String이 아닌 Object) */
		// Java 객체가 반환되면 JS에서 쓸 수 없으니
		// Spring이 인지한 문제를 해결하기 위해서
		// HttpMessagerConverter 객체가 자동으로 변환 해준다!
		return service.todoContent(todoNo);
	}
	
	
	/**
	 * 할 일 전체 목록 비동기 요청 처리
	 * @return
	 */
	@ResponseBody // 응답 데이터를 그대로 호출한 ajax 코드로 반환
	@GetMapping("todoList")
	public List<Todo> getTodoList() {
		return service.getTodoList();	
		
		// 비동기 요청에 대한 응답으로 객체 반환 시
		// "HttpMessageConverter"가
		// JSON(단일 객체) 또는 JSONArray(다수의 객체, 배열 컬렉션 등) 형태로 변환
		
		// "[{"KEY":V}, {"K":V}, {"K":V}]" == JSONArray
		}
	
	
	/**
	 * 할일 상세 조회
	 * @param todoNo
	 * @return
	 */
	@ResponseBody
	@GetMapping("todoContent/{todoNo}")
	public Todo selectTodo(
			@PathVariable("todoNo") int todoNo) {
		return service.todoContent(todoNo);
	}
	
	
	/** 할 일 완료 여부 수정
	 * @param todoNo
	 * @return result
	 */
	@ResponseBody
	@PutMapping("todoComplete")
	public int todoComplete(@RequestBody int todoNo) {
		// RequestBody 설명 다시 보기
		return service.todoComplete(todoNo);
	}
	
	
	/**
	 * 할일 삭제
	 * @param todoNo
	 * @return
	 */
	@ResponseBody
	@DeleteMapping("todoDelete")
	public int todoDelete(@RequestBody int todoNo) {
		return service.todoDelete(todoNo);
	}
	
	
	/**
	 * 할일 수정
	 * @param todo : JSON 변환되어 필드 값에 대입된 객체
	 * @return
	 */
	@ResponseBody
	@PutMapping("todoEdit")
	public int todoEdit(@RequestBody Todo todo) {
		 return service.todoEdit(todo);
	}
	
	
} //end
