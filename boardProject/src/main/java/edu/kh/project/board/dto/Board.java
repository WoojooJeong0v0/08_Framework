package edu.kh.project.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// DTD (Data Transfer Object) : 계층간 데이터 전달용 객체
// - 계층 : Controller, Service, DB 등 구분(로직) 

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Board {
	
	// 행 번호
	private int rnum;

	// BOARD 테이블 컬럼과 매핑되는 필드 작성
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriteDate;
	private String boardUpdateDate;
	private int readCount;
	private String boardDelFl;
	private int memberNo;
	private int boardCode;
	// 테이블과 똑같은 게 아니라 보드와 관련된 데이터 필요한 것만 가지고 옴!
	
	// MEMBER 테이블 JOIN용 컬럼
	private String memberNickname;
	
	// 목록 조회 시 댓글 / 좋아요 수 상관 쿼리 결과
	private int commentCount;
	private int likeCount;
	
	// 추가 작성 예정
	
}
