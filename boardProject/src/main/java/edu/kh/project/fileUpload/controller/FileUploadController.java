package edu.kh.project.fileUpload.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.fileUpload.dto.FileDto;
import edu.kh.project.fileUpload.service.FileUploadService;
import edu.kh.project.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("fileUpload")
public class FileUploadController {
	
	private final FileUploadService service; // final을 작성해야 의존성 주입이 되는 RequiredArgsConstructor
	
	/**
	 * 파일업로드 메인 연습 페이지 전환
	 * @return
	 */
	@GetMapping("main")
	public String fileUploadMain(Model model) {
		
		List<FileDto> fileList = service.selectFileList();
		
		model.addAttribute("fileList", fileList);
		
		return "fileUpload/main";
	}
	
	
	/*
	 * Spring에서 제출된 파일을 처리하는 방법
	 * 
	 * 1) enctype = "multipart/form-data" 로
	 * 		클라이언트가 요청
	 *	
	 * 2) Spring에 내장된 MultipartResolver가 제출된 파라미터들 분류함
	 * 
	 * 	문자열, 숫자 -> String
	 *  파일 -> MultipartFile 인터페이스 구현 객체
	 *  
	 * 3) 컨트롤러 메서드 매개변수로 전달
	 * 	(@RequestParam, @@ModelAttribute)
	 * */
	
	/**
	 * 단일 파일 업로드 
	 * @return
	 */
	@PostMapping("test1")
	public String test1(
			@RequestParam("uploadFile") MultipartFile uploadFile
			) throws IllegalStateException, IOException {
		
		String filePath = service.test1(uploadFile) ;

		log.debug("업로드된 파일 경로: {} ", filePath);
		return "redirect:main";
	}
	

}
