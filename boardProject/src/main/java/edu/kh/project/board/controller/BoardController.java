package edu.kh.project.board.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.dto.Board;
import edu.kh.project.board.dto.Pagination;
import edu.kh.project.board.service.BoardService;
import edu.kh.project.member.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("board")
public class BoardController {
	
	private final BoardService service;
	
	/** 게시글 목록 조회
	 * @PathVariable 이용!
	 * @param boardCode : 게시판 종류 번호
	 * @param cp : 현재 조회하려는 목록 페이지 번호 (current page 약자)
	 * 						 필수 아님, 없으면 1로 진행 (required, defaultValue)
	 * @param model : forward 시 데이터 전달하는 용도 객체(request scope)
	 *  */
	@GetMapping("{boardCode:[0-9]+}")
	public String selectBoardList(
			@PathVariable("boardCode") int boardCode,
			@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
			Model model) {
		
		// 서비스 호출 후 결과 반환 (목록이라서 list 같지만 map 으로 받음!!)
		// - 목록 조회인데 Map 인 이유는?
		// --> 서비스에서 여러 결과를 만들어야 하는데
		// 메서드는 반환을 1개만 할 수 있기 때문에
		// Map 으로 묶어서 반환 받을 예정
		Map<String, Object> map = service.selectBoardList(boardCode, cp);
		
		// map에 묶여있는 값 풀어넣기
		List<Board> boardList = (List<Board>)map.get("boardList");
		Pagination pagination = (Pagination)map.get("pagination");
		
//		// 정상조회 되었는지 log 확인
//		for(Board b : boardList) log.debug(b.toString());
//		log.debug(pagination.toString());
		
		model.addAttribute("boardList", boardList);
		model.addAttribute("pagination", pagination);
		
		
		return "board/boardList";
	}
	
	
	/** 게시글 상세 조회
	 * @param boardCode : 게시판 종류
	 * @param boardNo   : 게시글 번호
	 * @param model     : forward  시 request scope 값 전달 객체
	 * @param ra        : redirect 시 request scope 값 전달 객체
	 * @param loginMember : 로그인한 회원 정보, 로그인 안 되어 있으면 null 반환되도록 설정
	 * @param req  : 요청 관련 데이터를 담고 있는 객체 (쿠키 포함)
	 * @param resp : 응답 방법 담고 있는 객체 (쿠키 생성, 쿠키를 클라이언트에게 전달)
	 * @throws ParseException 
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String boardDetail(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			Model model,
			RedirectAttributes ra,
			@SessionAttribute(value="loginMember", required=false) Member loginMember,
			HttpServletRequest req,
			HttpServletResponse resp
			) throws ParseException {
		
		// 1) SQL 수행에 필요한 파라미터 Map 으로 묶기
		Map<String, Integer> map = new HashMap<>();
				map.put("boardCode", boardCode);
				map.put("boardNo", boardNo);
				
		/*  로그인이 되어 있는 경우 memberNo 를 map 에 추가  */
		if(loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}
		
		// 2) 서비스 호출 후 결과 반환 받기
		// 조회 결과를 Board로 받아옴
		
		Board board = service.selectDetail(map);
		
		/* 게시글 상세조회 결과가 없을 경우 */
		if(board == null) {
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다");
			return "redirect:/board/" + boardCode;
		}
		
		
		/* ----조회수 증가 시작---- */
		
		// 로그인한 회원의 작성글이 아닐 경우 + 비회원
		if(loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {
			
			// 1. 요청에 담겨있는 모든 쿠키 얻어오기
			Cookie[] cookies = null;
			Cookie c = null;
			
			if(req.getCookies() != null) {
			cookies = req.getCookies();
				for(Cookie temp : cookies) {
					// Cookie는 Map 형식처럼 K:V 형태 (name=value)
					
					// 클라이언트로부터 전달 받은 쿠키에
					// "readBoardNo" 라는 key(name)가 존재하는 경우
					// == 기존에 읽은 게시글 번호를 저장한 쿠키 존재하는 경우
					if(temp.getName().equals("readBoardNo")) {
						c = temp;
						break;
					}
				}
			}
			
			int result = 0; // service 호출 결과 저장
			
			// 이전에 readBoardNo 라는 name 을 가진 쿠키가 없을 경우
			// 24년 기준 쿠키에 , . - _ / 사용 안 됨
			// readBoardNo=[1000][2000][3000]....
			if(c == null) {
				c = new Cookie("readBoardNo", "[" + boardNo + "]");
				
				/* DB에서 해당 게시글 조회 수를 
				 * 1 증가 시키는 서비스 호출 */
				
				result = service.updateReadCount(boardNo);
				
			} else { // 이전에 readBoard name 쿠키가 있을 경우
				// readBoardNo=[1000][2000][3000]....
				
				// 현재 읽은 게시글 번호가 쿠키에 없다면
				// == 해당 글은 오늘 처음 읽음
				if(c.getValue().contains(boardNo + "") == false) {
					c.setValue(c.getValue() + "[" + boardNo + "]");
					
					// DB에서 조회 수 증가
					result = service.updateReadCount(boardNo);
				}				
			}
			
			// 2. 조회수 증가된 경우 쿠키 세팅하기
			if(result > 0) { // 조회수가 증가된 경우
				
				// 미리 조회된 조회수 데이터와 DB 조회수를 동기화 
				board.setReadCount(board.getReadCount() + 1);
				
				// 읽은 글 번호가 저장된 쿠키(변수 c)가
				// 어떤 주소 요청 시 서버로 전달될지 지정
				c.setPath("/"); // "/" 이하 모든 요청에 쿠키 포함된다
				
				/* 쿠키 수명 지정 (어려우니 재확인 필수) */
				// 다음 날 00시 00분 00초가 되면 삭제 
				// == 오늘 23시 59분 59초까지 유지
				
				// - 다음 날 00시 00분 00초까지 남은 시간 계산해서
				// 쿠키에 세팅 진행
				
				// Calendar 객체 : 시간을 저장하는 객체
				// Calendar.getInstance() : 현재 시간이 저장된 객체 반환
				Calendar cal = Calendar.getInstance();
				cal.add(cal.DATE, 1); // 1일 더하기
				// 예 ) 2024-10-08 10:14:30 -> 2024-10-09 10:14:30 
				
				// 날짜 데이터를 지정된 포맷의 문자열로 변경하는 객체
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				// 경험과 검색을 통해 이런 코드를 늘려갈 수 있음!!
				
				// java.util.Date import 진행
				Date currentDay = new Date(); // 현재 시간 2024-10-08 10:14:30
				
				// 내일 (24시간 후)
				// Calendar 객체 -> Date 타입으로 변환
				Date b = new Date(cal.getTimeInMillis()); // 2024-10-09 10:14:30 
				
				// sdf.format(b) == 2024-10-09
				// sdf.parse("2024-10-09") == 2024-10-09 00:00:00 Date 변환
				Date nextDay =  sdf.parse(sdf.format(b));
				
				// 다음날 자정 - 현재 시간 결과를 초 s 단위로 얻어오기
				// == 다음날 0시 0분 0초까지 몇 초 남았나 계산 
				long diff = (nextDay.getTime() - currentDay.getTime()) / 1000; // 초단위 계산을 위해 1000을 나눔
				
				// 쿠키 수명 설정
				c.setMaxAge((int)diff); // 강제 형변환
				
				// 응답 객체에 쿠키를 추가해서 응답 시 클라이언트에게 전달하게 만듬
				resp.addCookie(c);				
			}
		}
			
		/* ----조회수 증가 완료---- */
		
		
		model.addAttribute("board", board);
		
		// 조회된 이미지 목록이 있을 경우
		if(board.getImageList().isEmpty() == false) {
			
			// 썸네일 없으면 0부터 3번 index
			// 썸네일 있으면 1부터 4번 index
			int start = 0; // for문 시작값(index) 지정
			
			// 썸네일 있을 경우
			if(board.getThumbnail() != null) {
				// if(board.getImageList().get(0).getImgOrder() != 0)
				start = 1;
			}
			
			model.addAttribute("start", start); // 0 또는 1
		}
		
		
		return "board/boardDetail";
	}
	
	
	/**
	 * 좋아요 체크/해제 
	 * @param boardNo
	 * @return map (check , clear 상태 / 좋아요 개수 반환 )
	 */
	@ResponseBody
	@PostMapping("like")
	public Map<String, Object> boardLike(
			@RequestBody int boardNo,
			@SessionAttribute("loginMember") Member loginMember
			) {
		
		int memberNo = loginMember.getMemberNo();
		
		return service.boardLike(boardNo, memberNo);
	}
	
	
	

} // end
