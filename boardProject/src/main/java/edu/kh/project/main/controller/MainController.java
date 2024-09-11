package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // 요청 응답 제어 역할 명시 + Bean 등록 
public class MainController {

	@RequestMapping("/") // "/" 요청 매핑 (method 가리지 않음!) get, post, put...
	public String mainPage() {
		// forward (동기 처리) 
		// 접두사: classpath:/templates /
		// 접미사: .html 
		// -> forward 하려는 파일 Thymeleaf 접두사, 접미사 제외 경로 작성
		return "common/main";
	}
}
