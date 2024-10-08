package edu.kh.project.common.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * 내가 보고 있는 게 어떤 게시판인지 알려주는 용도의,
 * 어디에 있든지 게시판 이름을 볼 수 있도록 
 * 명확하게 표시 (끼워 넣어서 보여줌) 
 */

 /* 참고
  * 
  * 	// Uniform Resource Identifier : 통합 자원 식별자
		  // - 자원 이름(주소)만 봐도 무엇인지 구별할 수 있는 문자열
	  	// 예 )  /editBoard/1/insert
  * 
  */
public class BoardNameInterceptor implements HandlerInterceptor {

	// 후처리
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		/*
		 * 게시판 관련된 요청/응답 가로채서
		 * 현재 어떤 게시판을 조회/삽입/수정 하는 중인지 알려줌
		 * (게시판 이름 끼워 넣기)
		 * */
		
		// 1) application scope 에서 "boardTypeList" 얻어오기
		ServletContext application = request.getServletContext();
		
		List<Map<String, String>> boardTypeList 
		 = (List<Map<String, String>>)application.getAttribute("boardTypeList");
		
		// 2) "/board", "/editBoard" 로 시작하는 요청 중 
		// {boardCode} 부분인 2번째 코드 값 얻어오기
		
		String uri = request.getRequestURI();
		
		try {
			String code = uri.split("/")[2]; // boardCode만 잘라내기
			
			// boardTypeList에서 boardCode 가 같은 경우의 boardName 찾기
			for(Map<String, String> boardType : boardTypeList) {
				String boardCode = String.valueOf(boardType.get("boardCode"));
				
				if (boardCode.equals(code)) { // 같은 경우
					// request scope 에 boardName 세팅
					request.setAttribute("boardName", boardType.get("boardName"));
					break;
				}
			}
			
		} catch (Exception e) {
		}
		
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
}
