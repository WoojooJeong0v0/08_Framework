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
	 *  (임시 메모리, 임계값, 한 번에 몇 개 넘기는지 등 설정해줘야 함)
	 *  fileConfig, config.properties 등에서 진행
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
			@RequestParam("uploadFile") MultipartFile uploadFile // 임시저장된 파일을 참조하는 객체
			) throws IllegalStateException, IOException {
		
		String filePath = service.test1(uploadFile) ;

		log.debug("업로드된 파일 경로: {} ", filePath);
		return "redirect:main";
	}
	
	
	/**
	 * 단일 파일 업로드2 + 일반 데이터
	 * @param uploadFile : 업로드 되어 있는 임시저장된 파일을 참조하는 객체
	 * @param fileName : 원본 이름으로 지정될 파일 명
	 * @return
	 */
	@PostMapping("test2")
	public String test2(
			@RequestParam("fileName") String fileName,
			@RequestParam("uploadFile") MultipartFile uploadFile // 업로드된 파일이 임시 저장되어 있으니 그걸 참조하는 객체
			) throws IllegalStateException, IOException {
		
		String filePath = service.test2(uploadFile, fileName);
		
		log.debug("업로드된 파일 경로 : {}", filePath);
				
		return "redirect:main";
	}
	
	
	@PostMapping("test3")
	public String test3(
			@RequestParam("uploadFile") MultipartFile uploadFile) {
		
		String filePath = service.test3(uploadFile);
		log.debug("업로드된 파일 경로 : {}", filePath);
		return "redirect:main";
	}
	
	

} // end
