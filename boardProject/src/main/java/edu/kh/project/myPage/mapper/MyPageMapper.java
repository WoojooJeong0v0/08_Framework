package edu.kh.project.myPage.mapper;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.dto.Member;

@Mapper // 상속받은 클래스 구현(xml과 연결해주는 것) + Bean 등록
public interface MyPageMapper {

	/**
	 * 정보 수정
	 * @param inputMember
	 * @return result
	 */
	int updateInfo(Member inputMember);

	/**
	 * 닉네임 중복 검사
	 * @param input
	 * @return result
	 */
	int checkNickname(String input);

}
