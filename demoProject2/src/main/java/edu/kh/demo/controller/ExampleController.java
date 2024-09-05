package edu.kh.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.demo.dto.Student;
import jakarta.servlet.http.HttpServletRequest;
		// Servlet / JSP 존재하는 내장 객체 4종류의 데이터 유지 범위
		
		// 1) Page : 현재 보이는 페이지, Servlet/JSP
		
		// 2) Request : 요청 받은 데이터를 응답할 때까지
		// 							요청 받은 곳 + 요청 위임 (forward) 받은 곳
		
		// 3) Session : 객체가 생성되고 소멸 되기까지
		//							클라이언트가 서버 최초 접속 시 생성, 브라우저가 종료 되기 전까지
		//							또는 세션 시간 만료까지 유지
		
		// 4) Application : 하나의 Apllication 생성되고 소멸까지
		// 									서버 실행 시 1개만 생성, 서버 종료까지 유지



/**
 * Model
 * 
 * - org.springframework.ui 패키지
 * 
 * - Spring 에서 데이터 전달하는 역할의 객체
 * 
 * - 데이터 유지 범위 (scope) : 기본 request scope 
 * 
 * --> @SessionAttributes 와 함께 사용하면 session scope 로 변경됨
 * 
 * 	[Model 이용해서 값 세팅하는 방법]
 * 	
 * 	Model.addAttribute("key", value); // key는 무조건 String 형태
 * 
 */



// bean 스프링컨테이너가 만들고 관리하는 객체
@Slf4j // log 필드 생성 및 초기화 자동완성 lombok 어노테이션
@RequestMapping("example") // /example 로 시작하는 요청 매핑
@Controller // 컨트롤러임을 명시 + Bean 등록
public class ExampleController {
		
	@GetMapping("ex1")
	public String ex1(HttpServletRequest req, Model model) {
		
		 // request scope에 값 세팅
		 req.setAttribute("test1", "HttpServletRequest로 세팅한 값");
		 
		 // model을 이용해서 request scope 값 세팅
		 model.addAttribute("test2", "Model로 세팅한 값");
		 
		 // 단일 값 세팅(숫자, 문자열)
		 model.addAttribute("productName", "아이스 아메리카노");
		 model.addAttribute("price", 2000);
		 
		 // 복수 값 세팅(배열, List)
		 List<String> fruitList = new ArrayList<>();
		 fruitList.add("복숭아");
		 fruitList.add("딸기");
		 fruitList.add("수박");
		 fruitList.add("바나나");
		 
		 model.addAttribute("fruitList", fruitList);
		 
		 
		 // DTO 객체 만들어서 Model에 세팅 
		 // 단수이면서 복수인 그런 모습
		 Student std = Student.builder()
				 					.studentNo("1111")
				 					.name("짱구")
				 					.age(15)
				 					.build(); // -> 필드 전체가 아닌 일부 초기화 시 활용도 높음
		 
		 log.debug("std : {}", std);
		 
		 model.addAttribute("std", std);
		 // -------------------------------------------
		 
		 // DTO 필드 중 List가 포함된 경우
		 List<String> hobbyList = new ArrayList<>();
		 hobbyList.add("축구");
		 hobbyList.add("독서");
		 hobbyList.add("코딩 공부");
		 
		 Student std2 = Student.builder()
					.studentNo("22222")
					.name("철수")
					.age(20)
					.hobbyList(hobbyList)
					.build();
		 
		 model.addAttribute("std2", std2);
		 
		 
		 // classpath:/templates/ex/result1.html 파일로 
		 // forward(요청 위임)
		 return "ex/result1";
	} // ex1 end
	
	
	
	 /**
	  * 
	  * @param model : Spring에서 데이터 전달하는 용도 객체
	  * 							기본 scope : request
	  * @return
	  */
	@PostMapping("ex2") // /example/ex2 POST 방식
	public String ex2(Model model) { 
		
		
		model.addAttribute("str", "<h1>테스트 중입니다 &times; </h1>");
		
		
		
		 // classpath:/templates/ex/result2.html 파일로 
		 // forward(요청 위임)
		return "ex/result2";
		
	} // ex2 end
	
	
	
	/**
	 * 
	 * @param model : Spring 에서 전달하는 용도의 객체 (request)
	 * @return
	 */
	@GetMapping("ex3")
	public String ex3(Model model) {
		
		model.addAttribute("boardNo", 10);
		model.addAttribute("key", "제목");
		model.addAttribute("query", "검색어");
		
		return "ex/result3";
	} // ex3 end
	
	
	/*
	 * @PathVariable
	 * - 주소 중 일 부분을 변수 값처럼 사용할 수 있다
	 *  -> 해당 어노테이션으로 얻어온 값은
	 *  현재 메서드 + forward한 html 파일에서 사용 가능함
	 */
	
	// 맨 앞에 / 써도 실행되는데 리눅스 환경에서 실행 안 되니 빼는 걸 권장
	// 빈칸을 {} 로 뚫어 놓고 이름을 직접 지정해야 함
	// -> 주소 중 값을 얻어오고 싶은 부분을 {}로 작성하고
	// {} 지칭하는 이름(변수명) 내부에 작성한다!
	
	// 주소의 길이가 짧아져서 활용도가 높고 쿼리스트링과 별개로 용도 지정 가능
	@GetMapping("ex3/{number}")
	public String pathVarialbeTest(
				@PathVariable("number") int num // 주소 중 number를 가져와서 int num에 저장하겠다, 를 매개변수에 넣기
			) {
		
		log.debug("num : {}", num);
		
		
		return "ex/testResult";
	} // ex3
	
	@GetMapping("ex4")
	public String ex4(Model model) {
		
		Student std = new Student("3333", "맹구", 5, null);
		
		model.addAttribute("std", std);
		model.addAttribute("num", 100);
		
		return "ex/result4";
	} // ex4 end
	
	
	@GetMapping("ex5")
	public String ex5(Model model) {
		
		model.addAttribute("message", "서버에서 전달된 메시지");
		model.addAttribute("num", 12345);
		
		Student std = new Student();
		std.setStudentNo("6789");
		model.addAttribute("std", std);
		
		model.addAttribute("start", 0);
		model.addAttribute("end", 7);
		
		
		return "ex/result5";
	}

	
} // class end
