package edu.kh.project.myPage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.dto.Member;
import edu.kh.project.myPage.mapper.MyPageMapper;

@Transactional // 서비스 내에 메서드 수행 중 UnCheckedException 발생 시 rollback 수행
// 아니면 메서드 종료 시 commit 수행 (< 이 부분은 트랜잭션 매니저가 따로 해줌)
@Service // Service 역할 명시 , Bean 등록
public class MyPageServiceImpl implements MyPageService {

	@Autowired // 등록된 Bean 중에서 같은 자료형의 Bean을 의존성 주입 (DI)  
	private MyPageMapper mapper;
	
	@Autowired // 의존성 주입 (DI) 
	private BCryptPasswordEncoder encoder;

	@Override
	public int updateInfo(Member inputMember) {
		
		// 만약 주소가 입력되지 않은 경우(,,) null 로 변경
		if(inputMember.getMemberAddress().equals(",,")) {
			inputMember.setMemberAddress(null);
			// UPDATE 구문 수행 시 MEMBER_ADDRESS 컬럼 값이 NULL이 됨
			// 추후 데이터 조회 시 문제될 수 있음
		}
		
		return mapper.updateInfo(inputMember);
		// DML 수행 하려고 하니까 트랜잭션 처리가 필요해!
	}

	
	/**
	 * 닉네임 중복 검사
	 */
	@Override
	public int checkNickname(String input) {
		return mapper.checkNickname(input) ;
	}

	
	/**
	 * 비밀번호 변경
	 * @param currentPw
	 * @param newPw
	 * @param loginMember
	 * @return result
	 */
	@Override
	public int changePw(String currentPw, String newPw, Member loginMember) {
		
		// 1) 입력받은 현재 비밀번호가 로그인한 회원의 비밀번호와 일치하는지 검사
		// 현재 비밀번호가 암호화 되어 있기 때문에 바로 DB에서 확인할 수 없음
		// BCryptPasswordEncoder.matches(평문, 암호문) 이용하여 일치 검사
		
		// 비밀번호가 일치하지 않는다면
		if (encoder.matches(currentPw, loginMember.getMemberPw()) == false) {
			return 0;
		}
		
		// 2) 현재 비밀번호가 일치했으니까
		// 새로 받은 비밀번호를 암호화해서 DB에 저장하기
		String encPw = encoder.encode(newPw); // 새 비밀번호 암호화
		
		// 3) DB 비밀번호 변경 (사용 parameter = 회원 번호, 암호화된 새 비밀번호)
		loginMember.setMemberPw(encPw); // 얕은 복사가 일어나서 세션에 저장된 회원 정보 중 Pw를 변경한다
		return mapper.changePw(loginMember.getMemberNo(), encPw);
	}


	/**
	 * 회원 탈퇴
	 */
	@Override
	public int secession(String memberPw, Member loginMember) {
		
		// 1) 비밀번호 일치 검사
		if( encoder.matches(memberPw, loginMember.getMemberPw()) == false) {
			return 0; // 다를 경우엔 0을 반환한다!
		}
		
		// 2) 회원 탈퇴 mapper 호출 (update)
		
		return mapper.secession(loginMember.getMemberNo());
	}
	
} // end
