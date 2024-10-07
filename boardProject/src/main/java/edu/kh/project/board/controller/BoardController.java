package edu.kh.project.board.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.dto.Board;
import edu.kh.project.board.dto.Pagination;
import edu.kh.project.board.service.BoardService;
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
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String boardDetail(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			Model model,
			RedirectAttributes ra
			) {
		
		// 1) SQL 수행에 필요한 파라미터 Map 으로 묶기
		Map<String, Integer> map = 
				Map.of("boardCode", boardCode,
						"boardNo", boardNo); // 이렇게 만드는 Map 값 수정 안 됨
		
		// 2) 서비스 호출 후 결과 반환 받기
		// 조회 결과를 Board로 받아옴
		
		Board board = service.selectDetail(map);
		
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
	
	

} // end
