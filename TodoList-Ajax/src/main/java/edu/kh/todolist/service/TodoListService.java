package edu.kh.todolist.service;

import java.util.List;
import java.util.Map;

import edu.kh.todolist.dto.Todo;

public interface TodoListService {

	/**
	 * 할일 목록 조회 + 완료된 할일 개수
	 * @return map
	 */
	Map<String, Object> selectTodoList();

	/**
	 * 
	 * @param todo
	 * @return
	 */
	int todoAdd(Todo todo);

	
	/**
	 * 할일 상세 조회
	 * @param todoNo
	 * @return todo // 일치하는 것
	 */
	Todo todoContent(int todoNo);

	/**
	 * 완료 변경
	 * @param todoNo
	 * @return result
	 */
	int todoComplete(int todoNo);

	
	/**
	 * 수정
	 * @param todo
	 * @return result
	 */
	int todoEdit(Todo todo);

	
	/**
	 * 삭제
	 * @param todoNo
	 * @return result
	 */
	int todoDelete(int todoNo);

	
	/**
	 * 
	 * @param todoNo
	 * @return
	 */
	String searchTitle(int todoNo);

	
	/**
	 *  전체 할일 개수 조회
	 * @return
	 */
	int getTotalCount();

	
	/**
	 * 완료 할일 개수 조회
	 * @return
	 */
	int getCompleteCount();

	
	/**
	 * 비동기 전체 리스트 조회
	 * @return
	 */
	List<Todo> getTodoList();

	


}
