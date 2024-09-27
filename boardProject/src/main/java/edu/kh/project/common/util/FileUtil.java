package edu.kh.project.common.util;

import java.text.SimpleDateFormat;

public class FileUtil {

	//파일명 뒤에 붙는 숫자 (1~99999 반복 되는 형태)
	public static int seqNum = 1;
	
	
	/**
	 * 전달 받은 원본 파일명을 시간 형태의 파일명으로 변경
	 * 
	 * 예시 ) apple.jpg -> 20240927093712_00001.jpg
	 * @param originalFileName
	 * @return rename
	 */
	public static String rename(String originalFileName) {
		
		// 1) 확장자 추출하기 (.jpg 등)
		// 원본 파일명 뒤에서부터 검색해서 처음 찾은 "." 의 index 를 반환
		int index = originalFileName.lastIndexOf("."); 
		// 원본 파일명 "." 부터 끝까지 잘라낸 문자열 == .확장자 같은 모양이 됨
		String ext = originalFileName.substring(index); // 지정된 위치(괄호 안)부터 끝까지 String 을 자른다
		
		// 2) 현재 시간 "yyyyMMddHHmmss" 형태 문자열로 반환받기
		// SimpleDateFormat : 시간을 지정된 패턴 문자열로 바꿔주는 객체
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		// new java.util.Date() : 현재 시간을 저장한 객체 생성
		String time = sdf.format(new java.util.Date());
		
		// 3) seqNum (시퀀스) 이용한 숫자 생성
		// %05d : 정수가 들어갈 5칸짜리 오른쪽 정렬 패턴, 
		// 				빈 칸엔 0을 채운다
		String number = String.format("%05d", seqNum);
		
		seqNum++;
		
		if(seqNum == 100000) seqNum = 1; // 1~99999 반복
		
		// 20240927093712_00001.jpg
		return time + "_" + number + ext;
	}
}
