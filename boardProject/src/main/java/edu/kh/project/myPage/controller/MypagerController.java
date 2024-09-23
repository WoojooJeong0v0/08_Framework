package edu.kh.project.myPage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.dto.Member;
import edu.kh.project.myPage.service.MyPageService;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.GetProxy;

// Session Attributes 사용법
// 1. Model 을 이용하여 값을  Request scope 에서 Session scope 로 변경
// 2. @SessionAttribute 를 이용해 
//    Session Attribute"s" 에 의해 session 등록된 값을 얻어올 수 있음

@Slf4j
// login 성공했을 때 Request scope 에서 session scope 로 올려준다
@SessionAttributes({"loginMember"})
@Controller
@RequestMapping("myPage")
public class MypagerController {
	
	@Autowired // service 의존성 주입
	private MyPageService service;
	
	
	/**
	 * 마이페이지 (내 정보) 전환
	 * forward 시켜서 진짜 보여주는 역할을 하는 녀석!
	 * @param loginMember : 세선에 저장된 로그인 한 회원 정보
	 * @param model : 데이터 전달하는 용도의 객체 (기본 값은 Request scope)
	 * @return
	 */
	@GetMapping("info")
	public String info(
			@SessionAttribute("loginMember") Member loginMember,
			Model model	) {
		
		// 로그인 회원 정보 주소가 있을 경우!
		if(loginMember.getMemberAddress() != null) {
			// 주소를 , 기준으로 쪼개서 String 배열 형태로 반환 받아
			String[] arr = loginMember.getMemberAddress().split(",");
			
		// "04540,서울 중구 남대문로 120,2층"
		// -> {"04540", "서울 중구 남대문로 120", "2층"}
		// 			0 indet					1										2
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
			// 만약 DB에서 세 번째 , 뒤에 아무것도 없으면 오류가 나기 때문에 JS 나 유효성 검사를 더 해야 함!
		}
		
		return "myPage/myPage-info";
	}
	
	
	/**
	 * 내 정보 수정
	 * @param inputMember : 수정할 닉네임, 전화번호, 주소를 넘겨 받음
	 * @param loginMember : 현재 로그인된 회원 정보 (session)
	 * 											session 에 저장된 Member 객체 주소가 반환됨
	 * 											== session 에 저장된 Member 객체 데이터를 수정할 수 있다!
	 * @param ra : 리다이렉트 시 request scope로 값 전달
	 * 참조형은 주소를 저장하는 것임! (얕은 복사) (같은 주소 받으면 다 같은 걸 참조함)
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(
		@ModelAttribute Member inputMember,
		@SessionAttribute("loginMember") Member loginMember,
		RedirectAttributes ra) {
																			// session에 직접 연결된 상태 (session 제어가능)
		
		// @SessionAttribute("key")
		// - @SessionAttribute"s"를 통해 session 에 올라간 값을 얻어오는
		//   어노테이션
		
		// - 사용 방법 
		// 1) 클래스 위에  @SessionAttribute"s" 어노테이션을 작성하고
		//    해당 클래스에서 꺼내서 사용할 값의 key 를 작성
		//    --> 그럼 session 값을 미리 얻어와 놓음
		
		// 2) 필요한 메서드 매개 변수에
		//    @SessionAttribute("key")를 작성하면
		//    해당 key 와 일치하는 session 값을 얻어와서 대입
		
		// 1. inputMember에 로그인된 회원 번호 추가
		int memberNo = loginMember.getMemberNo();
		inputMember.setMemberNo(memberNo);
		// 회원 번호, 닉네임, 전화번호, 주소 4개 같이 담긴 상태
		
		// 2. 회원 정보 수정 Service 호출 후 결과 반환 받기
		int result = service.updateInfo(inputMember);
		
		// 3. 수정 결과에 따라 메시지 지정
		String message = null;
		if(result > 0) {message = "수정 성공!";
			// 4. 수정 성공 시 session 저장된 로그인 회원 정보를
			// 수정 값으로 변경해서 DB와 같은 데이터를 갖도록 함 (== 동기화)
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			//	loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
		}
		else 					 message = "수정 실패!";
		
		ra.addFlashAttribute("message", message);
		// 항상 header footer 가 있어서 메시지가 출력이 될 것이다
		
		// 동기화되지 않아서 화면에 안 보이니까 그 부분 수정해야 함
		// session 데이터 동기화 -> 3번 아래 if 문 참고
		
		// 주소에 담긴 값은 데이터 처리를 해야 하므로 
		// 마이페이지 내 정보 전환으로 넘어가라!
		
		return "redirect:info"; // /myPage/info GET방식 요청
	}
	
	
	/**
	 * (비동기) 닉네임 중복 검사
	 * @param input
	 * @return 0 : 중복 아님 / 1 : 중복
	 */
	@ResponseBody // 요청해서 응답을 받아야 하는 응답 본문 (Ajax 코드) 에 값 그대로 반환
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("input") String input) {
		return service.checkNickname(input);
	}
	// 컨트롤러에서는 타임리프 접두사, 접미사가 자동으로 붙는다
	// 1이나 0을 그대로 가져와야 함
	// 를 하려면 위에 @ResponseBody 붙임
	
	
	// 비밀번호 변경 화면으로 전환하는 용도
	@GetMapping("changePw")
	public String changePw() {
		
		
		// 접두사 : classpath:/templates/
		// 접미사 : .html
		return "myPage/myPage-changePw";
	}
	
	
	/**
	 * 실제 비밀번호 변경 수행하는 용도
	 * @param currentPw : 현재 비밀번호 (보안요소)
	 * @param newPw : 변경하는 새 비밀번호
	 * @param loginMember : 세션에서 얻어온 로그인한 회원 정보
	 * @return
	 */
	@PostMapping("changePw")
	public String changePw(
			@RequestParam("currentPw") String currentPw,
			@RequestParam("newPw") String newPw,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra // 리다이렉트 후 1회성 사용하고 싶은 데이터가 있다면 사용
			) {
		
		//서비스 호출 후 결과 반환 받기
		int result = service.changePw(currentPw, newPw, loginMember);
		
		String message = null;
		String path = null;
		
		// 결과에 따른 응답 제어
		if(result > 0) {
			message = "비밀번호 변경 되었습니다";
			path = "info"; // 내 정보 페이지로 redirect 
		} else { // 실패
			message = "현재 비밀번호가 일치하지 않습니다";
			path = "changePw"; // 비밀번호 변경 페이지로 redirect
		}
		
		ra.addFlashAttribute("message", message);
		
		// 현재 컨트롤러 메서드 매핑 주소 : /myPage/changePw (POST)
		// 리다이렉트 주소 : (제일 앞에 슬래시 시작 안 하면 상대 경로로 작성한 거니까)
		// 										/myPage/info  ,  /myPage/changePw (GET) 리다이렉트는 무조건 GET
		return "redirect:" + path; // 상대경로
	}
	
	
	/**
	 * 회원 탈퇴 페이지로 전환
	 * @return
	 */
	@GetMapping("secession")
	public String secession() {
		return "myPage/myPage-secession";
	}
	
	
	/**
	 * 회원 탈퇴 수행
	 * @param memberPw : 입력된 비밀번호 (현재 비밀번호 일치)
	 * @param loginMember : 로그인한 회원 정보 (session에서 얻어오기)
	 * @param ra : 리다이렉트 시 request scope 데이터를 잠시 session에 전달
	 * @param status : @SessinAtrrtibute 로 관리되는 세션 데이터 상태 제어 (세선 만료 가능)
	 * @return
	 */
	@PostMapping("secession")
	public String secession(
			@RequestParam("memberPw") String memberPw,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra,
			SessionStatus status			// 맨 위로 올라가면 관리되는 세션 확인 가능
			) {
		
		// 서비스 호출 후 결과 반환 받기
		int result = service.secession(memberPw, loginMember);
		
		String message = null;
		String path = null;
		
		// 결과에 따른 응답 제어
		if(result > 0) {
			message = "탈퇴 처리 되었습니다";
			path = "/";  // 메인페이지로 리다이렉트
			status.setComplete(); // 세션 만료 -> 현재 로그인 상태를 로그아웃으로
		} else { // 실패
			message = "현재 비밀번호가 일치하지 않습니다";
			path = "secession"; // 현재 페이지(탈퇴) 리다이렉트
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	

 }//end
