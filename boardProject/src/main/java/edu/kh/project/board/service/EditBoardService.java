package edu.kh.project.board.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.dto.Board;

public interface EditBoardService {

	// 새 글 작성
	int boardInsert(Board inputBoard, List<MultipartFile> images);

}
