package edu.kh.project.myPage.service;

import edu.kh.project.member.dto.Member;

public interface MyPageService {

	/**
	 * 회원 정보 수정
	 * @param inputMember
	 * @return result
	 */
	int updateInfo(Member inputMember);

	/**
	 * 닉네임 중복 검사
	 * @param input
	 * @return 1 or 0
	 */
	int checkNickname(String input);

}
