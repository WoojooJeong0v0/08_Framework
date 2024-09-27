package edu.kh.project.fileUpload.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.exception.FileUploadFailException;
import edu.kh.project.common.util.FileUtil;
import edu.kh.project.fileUpload.dto.FileDto;
import edu.kh.project.fileUpload.mapper.FileUploadMapper;
import lombok.RequiredArgsConstructor;



//@Transactional
//- Unchecked Excpetion 발생 시 Rollback 수행

//@Transactional(rollbackFor = Exception.class)
//- Exception 이하 예외 발생 시 Rollback 수행
//== Checked, Unchecked 가리지 않고 예외 발생 시 롤백
@Transactional(rollbackFor = Exception.class)
@Service
@RequiredArgsConstructor
@PropertySource("classpath:/config.properties")
//config.properties에 작성된 내용을 얻어와 사용하겠다
// 어떤 값인지는 @Value로 시작하는 필드 녀석들
public class FileUploadServiceImpl implements FileUploadService {

	private final FileUploadMapper mapper;
	
	// 인터넷 요청 주소  (/images/test/)
	@Value("${my.test.web-path}")
	private String testWebPath;
	
	// 파일 저장 폴더 경로
	@Value("${my.test.folder-path}")
	private String testFolderPath;
	
	
	
	
	// 단일 파일 업로드
	@Override
	public String test1(MultipartFile uploadFile) throws IllegalStateException, IOException {
		
		// MultipartFile이 제공하는 메서드
		
		// - getSize() : 파일 크기
		// - isEmpty() : 업로드한 파일이 없을 경우 true
		// - getOriginalFileName() : 원본 파일 명
		// - transferTo(경로) : 
		//    메모리 또는 임시 저장 경로에 업로드된 파일을
		//    원하는 경로에 전송(서버 어떤 폴더에 저장할지 지정)
		
		// 1) 업로드된 파일이 있는지 검사
		if(uploadFile.isEmpty()) { // 업로드 X
			return null;
		}
		
		// 2) 지정된 경로에 파일 저장
		/* String path = "C:/uploadFiles/"; */
		
		File forder = new File(testFolderPath);
		
		if(forder.exists() == false) {
			forder.mkdirs(); // make directory 라는 뜻 (directory 는 폴더) // 폴더생성
		}
		
		
		/**
		 * DB에 업로드되는 파일 정보를 INSERT
		 * - DB INSERT - > 파일 저장 순서로 동작
		 * 	만약 파일 저장 중 예외가 발생하면 
		 *  -> @Transactional 어노테이션이 rollback 실행
		 *   -> INSERT가 취소 됨
		 */
		
		/* 원본 파일명을 중복되지 않는 이름으로 변경 */
		String rename = FileUtil.rename(uploadFile.getOriginalFilename());
		
		// fileDto 객체 만들어 INSERT에 필요한 정보를 set
		FileDto file = FileDto.builder()
										.fileOriginalName(uploadFile.getOriginalFilename())
										.fileRename(rename)
										.filePath(testWebPath)
										.build();
		int result = mapper.fileInsert(file);
		
		
		// 업로드 되어 메모리 또는 임시 저장 폴더에
		// 저장된 파일을 지정된 경로(path)로 전달(옮기는) 코드
		// 지정된 경로에 파일 저장
		uploadFile.transferTo(new File(testFolderPath + rename));
		// 예외 생겨서 던짐 
		
		// (이전)저장된 파일 경로 반환 path
		// (이후)웹에서 접근 가능한 파일경로 URL 반환
		return testWebPath + rename;
	}



 /**
  * 파일 목록 조회
  */
	@Override
	public List<FileDto> selectFileList() {
		return mapper.selectFileList();
	}



	/**
	 * 업로드된 파일 원본명을 fileName으로 변환해서 저장하자
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@Override
	public String test2(MultipartFile uploadFile, String fileName) throws IllegalStateException, IOException {
		
		// 1) 업로드된 파일이 있는지 검사
		// if(uploadFile.getSize() <= 0)
		// if(uploadFile.getOriginalFilename().equals(""))
		if(uploadFile.isEmpty()) { // 권장!!
			return null; // 업로드된 파일이 없으면 null 반환
		}
		
		
		// 2) 제출된 fileName이 없다면 기존 파일명 유지
		// 확장자 추출하기
		int index = uploadFile.getOriginalFilename().lastIndexOf(".");
		String ext = uploadFile.getOriginalFilename().substring(index);
		
		String originalName = 
				fileName.equals("") 
					? uploadFile.getOriginalFilename()
					: fileName + ext; // 삼항연산자에 확장자 더해 주기
		
		// 3) 파일명 변경하기 
		String rename = FileUtil.rename(originalName);
		
		// 4) DB에 파일 정보부터 INSERT
		FileDto file = FileDto.builder()
										.fileOriginalName(originalName) // 원본명
										.fileRename(rename) // 변경명
										.filePath(testWebPath) // 웹 접근 주소
										.build();
		
		int result = mapper.fileInsert(file);
		
		// 5) 지정된 폴더로 임시저장된 업로드 파일 옮기기 (전송)
		// TransperTo
		
		uploadFile.transferTo(
				new File(testFolderPath + rename)); // 예외처리 구문 넣기
		
		return testWebPath + rename;
	}


	@Override
	public String test3(MultipartFile uploadFile) {
		
		// 1) 업로드 파일 있는지 검사
		if(uploadFile.isEmpty()) return null;
		
		// 2) 파일명 변경하기 // 같은 이름이 있으면 덮어씌워지니까
		String rename = FileUtil.rename(uploadFile.getOriginalFilename());
		
		// 3) 데이터를 파일 정보에 INSERT
		FileDto file = 
				FileDto.builder() // 빌더로 만드는 건 static 메서드? (찾아보기)
				.fileOriginalName(uploadFile.getOriginalFilename()) // 원본이름 
				.fileRename(rename) // 바꾼 이름
				.filePath(testWebPath) // 접근 경로
				.build();
		
		int result = mapper.fileInsert(file);
		// Insert 성공 하면 트랜잭션에 데이터가 존재
		
		// 4) 지정된폴더로 임시저장된 파일 옮기기
		try {
			uploadFile.transferTo(new File(testFolderPath + rename));
			
//			// 사용자 정의 예외 테스트
//			int a = 1;
//			if(a == 1) throw new RuntimeException();
			
		} catch (Exception e) {
			e.printStackTrace();
			
			// transferTo는 Checked Exception 을 던지기 때문에
			// 1 ) throws 또는 try catch 무조건 작성해야 함
			// 2 ) throws 작성 시 호출하는 메서드에서 
			// 		 추가 예외 처리 코드를 작성해야 하는 번거로움 존재
			// 3 ) 다만 try catch 작성 시
			//		 메서드 내부에서 예외가 처리 되기 때문에
			// 		 메서드 종료 시 예외 던져지지 않아서 @Transactinal이
			//		 예외 발생에도 roll back 을 수행하지 않게 된다
			
			// [추천 해결 방법]
			// * try catch 작성해서 Checked Exception 을 처리
			// - 호출하는 메서드에 throws 구문 작성 안 함!
			
			// * Unchecked Exception 형태의 사용자 정의 예외를 강제 발생 시킴
			// - @Transactinal 어노테이션에 roll back For 속성 작성 안 해도 롤백 처리 가능!
			
			// 예외 강제 발생
			// Unchecked Exception 은 컴파일러가 자동으로 throws 구문 작성
			// 예외 발생 시 호출부로 던지게 된다!
			throw new FileUploadFailException();
		}
		
		return testWebPath + rename;
	}
	
	
	
	
	
} // end
