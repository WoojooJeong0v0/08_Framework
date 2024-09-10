package edu.kh.todolist.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.todolist.dto.Todo;

@Mapper // 마이바티스 어노테이션 / 상속받은 클래스까지 생성 후 Bean으로 등록
public interface TodoListMapper {

	
	/**
	 * 할일 목록 조회
	 * @return
	 */
	List<Todo> selectTodoList();

	/**
	 * 완료된 할일 개수 조회
	 * @return
	 */
	int selectCompleteCount();

	/**
	 * 할일 추가
	 * @param todo
	 * @return result
	 */
	int todoAdd(Todo todo);

	
	/**
	 * 상세 조회
	 * @param todoNo
	 * @return todo
	 */
	Todo todoContent(int todoNo);

	
	/**
	 * 할일 완료
	 * @param todoNo
	 * @return result
	 */
	int todoComplete(int todoNo);

	
	/**
	 * 할일 수정
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

	String searchTitle(int todoNo);

	
	/**전체 할일 개수 조회
	 * 
	 * @return
	 */
	int getTotalCount();


	


}
