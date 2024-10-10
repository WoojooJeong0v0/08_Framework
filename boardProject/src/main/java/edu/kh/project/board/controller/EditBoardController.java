package edu.kh.project.board.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.dto.Board;
import edu.kh.project.board.service.BoardService;
import edu.kh.project.board.service.EditBoardService;
import edu.kh.project.member.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("editBoard")
public class EditBoardController {
	
	private final EditBoardService service;
	// 수정 시 상세 조회 서비스 호출을 위한 의존성주입 DI
	private final BoardService boardService;
	
	/**
	 * @PathVariable 사용 시 정규 표현식 적용 가능하다!
	 * {변수명:정규표현식}
	 */
	
	/**
	 * 게시글 작성 화면으로 전환
	 * {boardCode:[0-9]+} : boardCode는 숫자 1글자 이상만 가능하다는 정규 표현식
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(
			@PathVariable("boardCode") int boardCode) {
		// @PathVariable 지정 시 forward한 html 파일에서도 사용 가능 (리퀘스트 스코프처럼)
		return "board/boardWrite"; // board/boardWrite로 forward하겠다
	}
	
	
	/**
	 * 게시글 등록하기
	 * @param boardCode : 게시판 종류 번호
	 * @param inptBoard : 제출된 key값이 일치하는 필드에 값이 저장된 객체
	 * 										(커맨드객체)
	 * @param loginMember : 로그인한 회원 정보 (글쓴이 회원 번호 필요)
	 * @param images : 제출된 file 타입 input 태그 데이터
	 * @param ra : 리다이렉트 시 request scope로 값 전달
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(
			@PathVariable("boardCode") int boardCode,
			@ModelAttribute Board inputBoard,
			@SessionAttribute("loginMember") Member loginMember,
			@RequestParam("images") List<MultipartFile> images,
			RedirectAttributes ra) {
		
		/* 전달된 파라미터 확인 debug 모드로
		 * 
		 * 제목, 내용, boardCode - > inputBoard 커맨드 객체
		 * 
		 * List<MultipartFile> images  의 크기 (size())
		 * == 제출된 file 타입 input 태그 개수 == 5개
		 * 
		 * ** 선택된 파일이 없더라도 빈칸이 제출된다!!!
		 * 예 ) 0, 2, 4 인덱스만 선택 -> 0, 2, 4는 제출된 값이 있고
		 * 															나머지는 빈칸으로 제출됨!
		 *  
		 *  */
		
		// 1) 작성자 회원 번호를 inputBoard에 세팅
		inputBoard.setMemberNo(loginMember.getMemberNo());
		
		// 2) 서비스 호출 후 결과 반환 받기
		int boardNo = service.boardInsert(inputBoard, images);
		
		// 3) 서비스 결과에 따라 응답 제어
		String path = null;
		String message = null;
		
		if(boardNo == 0) { // 실패
			path = "insert";
			message = "게시글 작성 실패";
		} else {
			path = "/board/" + boardCode + "/" + boardNo; // 상세조회 주소
			message = "게시글이 작성 되었습니다";
//			path = "/board/" + boardCode; // 목록조회 주소 (임시)
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path; // 임시 작성
	}
	
	
	
	/**
	 * 게시글 삭제
	 * - DB에서 boardNo, MemberNo가 일치하는
	 * 	"BOARD" TABLE 행의 BOARD_DEL_FL 컬럼 값을 'Y' 로 변경
	 * @param boardNo
	 * @param loginMember
	 * @param ra
	 * @param referer : 현재 컨트롤러 메서드를 요청한 페이지 주소
	 * 				(이전 화면의 주소 == 상세 조회 페이지)
	 * @return
	 *  - 삭제 성공 시 : "삭제 되었습니다" 메시지 전달
	 *  									+ 해당 게시판 목록으로 redirect
	 *  - 삭제 실패 시 : "삭제 실패" 메시지 전달
	 *  									+ 삭제하려던 게시글 상세조회 페이지로 redirect
	 */
	@PostMapping("delete")
	public String boardDelete(
			@RequestParam("boardNo") int boardNo,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra,
			@RequestHeader("referer") String referer) {
		
//		log.debug("referer: {}", referer);
		// 예 ) http://localhost/board/1/2002
		
		int result = service.deletePost(boardNo, loginMember.getMemberNo());
		String message = null;
		String path = null;
		
		// 로그인을 안 할 경우 삭제 버튼이 안 보이므로
		// 별도로 로그인 여부를 판단할 필요가 없었다...
		
		if(result > 0) {
			message = "삭제 성공했습니다";
			path = "/board/" + referer.split("/")[4]; // 삭제된 게시글이 존재하던 게시판 목록 주소
			
			/** 정규표현식으로 진행
			 * 
			 * 		if(result > 0) { // 성공
			message = "삭제 되었습니다";
			
			String regExp = "/board/[0-9]+";
			
			Pattern pattern = Pattern.compile(regExp);
			Matcher matcher = pattern.matcher(referer);
			
			if(matcher.find()) { // 일치하는 부분을 찾은 경우
				path = matcher.group();
			}
			 * 
			 */
		} else {
			message = "삭제 실패";
			path = referer; // 삭제 요청했던 상세 조회 페이지 주소
		}
		
		ra.addFlashAttribute("message", message);
		
		// redirect:/board/2
		
		return "redirect:" + path;
	}
	
	
	/**
	 * 게시글 수정 화면 전환
	 * @param boardCode : 게시판 종류 구분
	 * @param boardNo : 수정할 게시글 번호
	 * @param loginMember : 로그인한 회원 정보 session
	 * @param ra : redirect 시 request scope 로 데이터 전달
	 * @param model : forward 시 request scope 로 데이터 전달
	 * @return
	 */
	@PostMapping("{boardCode}/{boardNo}/updateView")
	public String updateView(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra,
			Model model) {
		// 등록된 글 모습 그대로 조회하기 위해 select 진행
		// boardCode, boardNo 일치하는 글 조회
		Map<String, Integer> map = Map.of("boardCode", boardCode, "boardNo", boardNo); 
		// 상세조회 재확인
		Board board = boardService.selectDetail(map);
		
		// 게시글이 존재하지 않는 경우
		if(board == null) {
			ra.addFlashAttribute("message", "해당 게시글이 존재하지 않습니다.");
			return "redirect:/board/" + boardCode; // 게시글 목록으로
		}
		
		// 게시글 작성자가 현재 로그인한 회원이 아닌 경우
		if(board.getMemberNo() != loginMember.getMemberNo()) {
			ra.addFlashAttribute("message", "작성자만 수정 가능합니다.");
			return String.format("redirect:/board/%d/%d", boardCode, boardNo); // 상세 조회로 리다이렉트
		}
		
		// 게시글도 존재하고 로그인한 회원이 작성자가 맞을 경우 (정상 작동)
		// 수정화면으로 forward
		
		model.addAttribute("board", board);
		return "board/boardUpdate";
	
	}
	
	
	
	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			@ModelAttribute Board inputBoard, // 커맨드 객체
			@SessionAttribute("loginMember") Member loginMember,
			@RequestParam("images") List<MultipartFile> images, // 이미지 저장 리스트
			@RequestParam(value="deleteOrderList", required = false) String deleteOrderList,
			RedirectAttributes ra) {
		
		// 1. 커맨드 객체 inputBoard에 로그인한 회원 번호 추가
		inputBoard.setMemberNo(loginMember.getMemberNo());
		
		// inputBoard에 세팅된 값
		// boardcode, boardNo, boardTitle, boardContent, memberNo
		
		// 2. 게시글 수정 서비스 호출 후 결과 반환
		int result = service.boardUpdate(inputBoard, images, deleteOrderList);
		
		String message = null;
		if(result > 0) {
			message = "게시글이 수정 되었습니다.";
		} else {
			message = "수정 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return String.format("redirect:/board/%d/%d", boardCode, boardNo); // 상세조회
	}
	
	

} // end
