package edu.kh.project.main.service;

import java.util.List;

import edu.kh.project.member.dto.Member;

public interface MainService {

	/**
	 * 전체 회원 조회
	 * @return
	 */
	List<Member> selectMemberList();

}
