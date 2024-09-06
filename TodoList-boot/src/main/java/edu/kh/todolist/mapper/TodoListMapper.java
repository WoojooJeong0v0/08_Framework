package edu.kh.todolist.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.todolist.dto.Todo;

@Mapper // 상속받은 클래스까지 생성 후 Bean으로 등록
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

}
