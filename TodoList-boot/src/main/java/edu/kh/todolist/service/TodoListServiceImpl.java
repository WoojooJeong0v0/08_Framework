package edu.kh.todolist.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todolist.dto.Todo;
import edu.kh.todolist.mapper.TodoListMapper;

@Transactional // 내부 메서드 수행 후 트랜잭션 처리수행
							// 예외 발생 시 rollback, 아니면 commit 하도록
							// (단점) 여기에 작성하면 select 에서도 작동해서 조금 느려질 수 있음 -> 대안 있음
@Service // Service 임을 명시하고 Bean 으로 등록
public class TodoListServiceImpl implements TodoListService {

	@Autowired // 등록된 Bean 중에서 같은 타입 얻어와 대입(주입) DI(의존성) 진행
	private TodoListMapper mapper;

	
	@Override
	public Map<String, Object> selectTodoList() {
		// 1) 할 일 목록 조회
		List<Todo> todoList = mapper.selectTodoList();
		
		// 2) 완료된 할 일 개수 조회
		int completeCount = mapper.selectCompleteCount();
		
		// 3) Map 객체 생성 후 조회결과 담기
		Map<String, Object> map = new HashMap<>();
		
		map.put("todoList", todoList);
		map.put("completeCount", completeCount);
		
		//  4) map 반환
		return map;
	}
	
	
}
