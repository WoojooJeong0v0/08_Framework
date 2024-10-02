package edu.kh.project.board.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	@GetMapping("{boardCode}")
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

}
