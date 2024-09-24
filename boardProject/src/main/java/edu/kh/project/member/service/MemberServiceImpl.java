package edu.kh.project.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.kh.project.member.dto.Member;
import edu.kh.project.member.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

// 왜 Service 인터페이스 상속 받을까?
// - 팀 프로젝트, 유지 보수에 도움이 되기 때문
// (오버라이딩, 업캐스팅 등을 활용할 수 있기도 하고)
// + AOP Proxy 적용 위해서!!

@Slf4j // 암호화 확인하기
@Service // 비즈니스 로직 처리하는 역할 명시 + Bean 등록 (IOC)
public class MemberServiceImpl implements MemberService {
	
	@Autowired // 등록된 Bean 중에서 같은 타입의 Bean을 대입 (DI)
	private MemberMapper mapper;
	
	@Autowired // BCryptPasswordEncoder 암호화 객체에 의존성 주입 받기
	private BCryptPasswordEncoder encoder;
	
	
	/**
	 * 비밀번호 암호화
	 *  - 하는 이유 : 평문 상태로 비밀번호 저장하면 안 됨!
	 *  
	 *  - 먼 옛날 방식 : 데이터 -> 암호화, 암호화 데이터 -> 복호화 -> 원본 데이터
	 *  
	 *  - 약간 과거 또는 현재 : 데이터 암호화만 가능 (SHA 방식) -> 복호화 방법 제공X
	 *  		-> 단점 : 마구잡이 대입해서 만들어진 암호화 데이터 테이블에 뚫림
	 *  
	 *  - 요즘 많이 사용하는 방식 : BCrypt 암호화 (Spring Security 에 기본 내장)
	 *  - 입력된 문자열(비밀번호)에 salt 를 추가한 후 암호화
   *		-> 암호화 할 때 마다 결과가 다름
   *		-> DB에 입력받은 비밀번호를 암호화해서 넘겨줘도 비교 불가능
   *		-> BCrypt 가 함께 제공하는 평문, 암호화 데이터 비교 메서드인 matches() 이용
   *			(같으면 true, 다르면 false)
   *
   * 			--> matches() 메서드는 자바에서 동작하는 메서드
   * 			 -> DB에 저장된 암호화된 비밀번호 조회해서 가져와야 한다!
	 */

	// 로그인 서비스
	@Override
	public Member login(String memberEmail, String memberPw) {
		
		// 암호화 테스트
//		log.debug("memberPw : {}", memberPw);
//		log.debug("암호화된 memberPw : {}", encoder.encode(memberPw));
		
		// 1. memberEmail 이 일치하는 회원의 정보를 DB에서 조회 (비밀번호 포함!)
		Member loginMember = mapper.login(memberEmail);
		
		// 2. EMAIL (아이디) 이 일치하는 회원 정보 없을 경우
		if(loginMember == null) return null;
		
//		// 3. DB에서 조회된 비밀번호와 입력받은 비밀번호가 같은지 확인하는 코드
//		log.debug("비밀번호 일치? : {}" , encoder.matches(memberPw, loginMember.getMemberPw()));
		
			// 입력 받은 비밀 번호와 DB에서 조회된 비밀 번호가 일치하지 않을 때
			if( !encoder.matches(memberPw, loginMember.getMemberPw()) ) {
				return null;
			}
		
		// 4. 로그인 결과 반환
		return loginMember;
	}


	@Override
	public int signUp(Member inputMember) {
		
		// 1) 비밀번호 암호화 (BCrypt)
		String encPw = encoder.encode(inputMember.getMemberPw()); // 입력한 거 get으로 얻어오고
		inputMember.setMemberPw(encPw); // set을 해둠
		
		// 2) 주소 미입력(",,") 시 null 로 변경
		if(inputMember.getMemberAddress().equals(",,")) {
			inputMember.setMemberAddress(null);
		}
		
		// text 타입 input 은 값 작성 안 되면 "" (빈칸)
		// checkbox , radio 체크가 안 되면 null
		
		
		// 3) mapper 호출 후 결과 반환
		return mapper.signUp(inputMember);
	}


	@Override
	public int emailCheck(String email) {
		return mapper.emailCheck(email);
	}


	@Override
	public int nickCheck(String nick) {
		return mapper.nickCheck(nick);
	}

}
