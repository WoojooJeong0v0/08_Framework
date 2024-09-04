package edu.kh.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder // 빌더 패턴을 위한 메서드 자동완성 어노테이션
// 빌더 패턴 : 특정한 값으로 초기화된 객체를 쉽게 생성하기 위한 메서드를 만드는 패턴
// 메서드 체이닝을 연쇄적으로 작성해서 호출하는 패턴 
public class Student {

	private String studentNo; // 학번
	private String name; // 이름
	private int age; // 나이
	
	private List<String> hobbyList; // 취미 목록
	
//	public Student studentNo(int age) {
//		this.age = age;
//		return this;
//	}

} // class end
