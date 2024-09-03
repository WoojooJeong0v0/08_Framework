package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


// Controller : 어떤 요청을 받고, 어떻게 응답할지 제어하는 역할
//  - 어떤 요청을 처리할지 주소 매핑
//  - 요청 처리된 결과에 따라 응답하는 방법 (forward, redirect, 값 반환 등) 제어

// * instance : 개발자가 new 연산자 이용하여 만든 객체
// * bean			: Spring(Java)이 만든 객체 

// @Controller 어노테이션
// 1) Controller 임을 명시함
// 2) 클래스에 작성된 내용 대로 구현 -> 스프링이 객체 생성 == bean 등록(생성)
// (사람이 만든 객체는 인스턴스) 

// Spring 은 어노테이션을 많이 사용한다
@Controller
public class ForwardTestController {

	// 기존 Servlet
	// - @WebServlet("요청주소") -> 클래스명 위에 작성
	//  == 해당 클래스는 "요청주소" 매핑해서 처리하는 클래스다 라는 뜻
	//  -> 클래스 별로 요청 주소 1개만 처리 가능
	
	/**
	 * Spring Controller
	 * 
	 * - @RequestMapping("요청주소") -> 클래스명, 메서드명 위에 작성 가능
	 * - @GetMapping("요청주소") -> 메서드명 위에 작성
	 * - @PostMapping("요청주소") -> 메서드명 위에 작성
	 * 
	 *  - @RequestMapping("요청주소") 뜻
	 *   요청 주소를 처리할 클래스/메서드 매핑하는 어노테이션
	 *   
	 *   1) 클래스 위에 작성하는 경우
	 *   	- 공통 주소를 해당 클래스에 매핑
	 *   		예 ) /todo/select , /todo/insert , /todo/update...
	 *        - > @RequestMapping("todo") // /todo 로 시작하는 요청
	 *   
	 *   2) 메서드 위에 작성하는 경우
	 *   	- "요청주소" 로 요청을 받은 경우 해당 메서드에서 처리함
	 */
		
	
		// Controller 메서드 작성 방법
	 	// 1) 접근 제한자는 무조건 public
		// 2) 반환형은 대부분 String (ModelAndView 또는 Ajax 사용 시 달라질 수 있음)
		// 		왜 스트링?? 
		// 			-> Controller 메서드에서 반환되는 문자열이
		//			 forward할 html 파일의 경로가 되기 때문임!!
		// 3) 메서드명은 자유롭게 작성하되 의미를 담아서 작성
		// 4) ((중요!)) 매개변수는 필요한 만큼 마음 대로 
		// 		-> Arguments Resolver 참조
		// 5) 매핑할 요청 주소를 @RequsetMapping 등 이용해 작성
	
	/* Spring boot Controller에서 
	 * 특수한 경우 제외하고
	 * 매핑 주소 제일 앞에 "/" (슬래시) 는 적지 않는다 */
	@RequestMapping("forward")
		public String forwardTest() {
		
//		System.out.println("/forward 매핑 됐는지 확인");
		
		/* Thymeleaf 이용 : 템플릿 엔진 (JSP 대신 사용함)
		 * Thymeleaf 사용 시 접두사, 접미사가 제공됨
		 * 
		 * - 접두사 (prefix) : classpath:/templates/
		 * - 접미사 (suffix) : .html
		 * 
		 * * Controller 메서드에서 반환되는 문자열의
		 *  앞, 뒤에 접두사/접미사가 붙어서
		 *  forward할 html 파일 경로 형태가 됨
		 */
		
	// classpath:/templates/forward.html
			return "forward";
		}
	
}
