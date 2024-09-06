package edu.kh.todolist.service;

import java.util.Map;

public interface TodoListService {

	/**
	 * 할일 목록 조회 + 완료된 할일 개수
	 * @return map
	 */
	Map<String, Object> selectTodoList();

}
