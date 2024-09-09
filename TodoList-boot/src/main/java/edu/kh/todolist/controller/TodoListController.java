package edu.kh.todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todolist.dto.Todo;
import edu.kh.todolist.service.TodoListService;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로그 작성에 필요
@Controller // 응답요청 (스프링이) 제어 처리 + BEAN 등록(IOC : 객체생성 및 관리 스프링이 함)
@RequestMapping("todo")
public class TodoListController {

	@Autowired // 등록된 Bean 중에서 같은 타입 객체 의존성 주입 (DI)
	private TodoListService service;
	
	
	/** Todo 추가
	 * @ModelAttribute 제출된 파라미터를 DTO 객체 필드에 대입 
	 *  								-> 생략도 가능함
	 * 
	 * @param todo : 커맨드 객체(제출된 파라미터를 필드에 저장한 DTO 객체)
	 * @param ra   : 재요청 시 request scope로 값 전달
	 * @return
	 */
	@PostMapping("add")
	public String todoAdd(
		@ModelAttribute Todo todo,
		RedirectAttributes ra) {
	
		int result = service.todoAdd(todo);
	
		String message = null;
		
		if(result>0) {
				message = todo.getTodoSub() + " 추가 되었습니다";
			} else {
				message = "추가 실패 ㅠㅠ";
			}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/";
	}
	
	
	/**
	 * 상세 조회
	 * @param todoNo : 조회할 할 일의 PK 번호 (@PathVariable 사용)
	 * @param model  : 데이터 전달용 객체 forward 시 request scope 값 전달
	 * @param ra     : redirecnt 시 scope에 값 세팅해서 불러옴
	 * @return
	 */
	@GetMapping("todoContent/{todoNo}")
	public String todoContent(
			@PathVariable("todoNo") int todoNo, // PathVariable로 todoNo이란 거 꺼낼 건데 int todoNo 변수에 저장해놔
			Model model,
			RedirectAttributes ra
			) {
		
			Todo todo = service.todoContent(todoNo);
			
			if(todo == null) { // 조회한 결과가 없을 경우
				ra.addFlashAttribute("message", "할 일이 존재하지 않습니다");
				return "redirect:/"; // 실패하면 재요청 (리다이렉트)
			}
			
			// 조회 결과가 있을 경우
			model.addAttribute("todo", todo);
			return "todo/content"; // 성공하면 요청 위임해라
	}
	
	
	/**
	 * 완료 여부 변경
	 * @param todoNo : 쿼리스트링으로 전달된 todoNo 값
	 * @param ra
	 * @return
	 */
	@GetMapping("complete")
	public String todoComplete(
			@RequestParam("todoNo") int todoNo,
			RedirectAttributes ra
			) {
		
		// update 진행
		
		int result = service.todoComplete(todoNo);
		
		String message = null;
		String path = null;
		if(result>0) {
			path = "redirect:/todo/todoContent/" + todoNo;
			message = "변경 성공!"; }
		else {
			path = "redirect:/"; // 메인 페이지로 날려
			message = "할 일이 존재하지 않습니다";
		}
		
		ra.addFlashAttribute("message", message);
		return path;
	}
	
	
	/**
	 * 수정 화면 전환
	 * @param toNo
	 * @param model
	 * @param ra
	 * @return
	 */
	@GetMapping("update")
	public String todoUpdate(
			@RequestParam("todoNo") int todoNo,
			Model model,
			RedirectAttributes ra
			) {
			
			// 상세조회 서비스 호출
			Todo todo = service.todoContent(todoNo);
			
			if(todo == null) { // 일치하는 todoNo 가 없을 경우
				ra.addFlashAttribute("message", "할 일이 존재하지 않습니다");
				return "redirect:/";
			}
			
			model.addAttribute("todo", todo);
		
			return "todo/update";
	}
	
	
	/**
	 * 할일 수정
	 * @param todoNo
	 * @param todo : todoSub, todoContent
	 * @param ra
	 * @return
	 */
	@PostMapping("edit")
	public String todoEdit(
			@RequestParam("todoNo") int todoNo,
			@ModelAttribute Todo todo, // 제출되는 파라미터 값 이름이 DTO와 같으면 자동으로 커맨드객체 생성
			RedirectAttributes ra
			) {
		
		int result = service.todoEdit(todo);
		
		String message = null;
		String path = null;
		
		if(result>0) {
			message = todo.getTodoSub() + " 수정 되었습니다";
			path = "redirect:/todo/todoContent/" + todo.getTodoNo();
		} else {
			message = "수정 실패 ㅠㅠ";
			path = "redirect:/todo/update?todoNo=" + todo.getTodoNo();
		}
	
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	
	@GetMapping("delete")
	public String todoDelete(
			@RequestParam("todoNo") int todoNo,
			RedirectAttributes ra
			) {
		
		int result = service.todoDelete(todoNo);
		
		String message = null;
		String path = null;
		
		if(result>0) {
			message = todoNo + "번 할 일 삭제 되었습니다";
			path = "redirect:/";
		} else {
			message = "삭제 실패 ㅠㅠ";
			path = "redirect:/todo/todoContent/" + todoNo;
		}
	
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	
}
