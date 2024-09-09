package edu.kh.todolist.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todolist.dto.Todo;
import edu.kh.todolist.service.TodoListService;

// Controller : 요청/응답 제어 처리
@Controller // 컨트롤러임을 명시하며 Bean 등록
public class MainController {

	@Autowired // 의존성 주입 (DI) 자세한 설명 서비스임플에있음
	private TodoListService service;
	
	
	/**
	 * 메인 페이지
	 * @param model 데이터 전달용 객체 (request scope가 기본)
	 * @return
	 */
	@RequestMapping("/") // 최상위 주소 매핑 GET POST 가리지 않음
	public String mainPage(Model model) {
		
		Map<String, Object> map = service.selectTodoList();
		
		// map 에 담긴 값 꺼내놓기
		List<Todo> todoList = (List<Todo>)map.get("todoList");
		int completeCount = (int)map.get("completeCount");
		
		// 조회 결과 request scope에 추가 (model)
		model.addAttribute("todoList", todoList);
		model.addAttribute("completeCount", completeCount);
		
		// classpath:/templates/common/main.html forward
		return "common/main";
	}
	
	
	@GetMapping("todo/add")
	public String add() {
		return "/todo/add";
	}
	
	
	
	
	
	
} // end
