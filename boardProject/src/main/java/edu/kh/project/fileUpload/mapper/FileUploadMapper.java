package edu.kh.project.fileUpload.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.fileUpload.dto.FileDto;

@Mapper
public interface FileUploadMapper {

	/**
	 * 단일) 파일 1개 정보 DB에 삽입
	 * 파일 이름만 저장하고 있는 것!
	 * @param file
	 * @return result
	 */
	int fileInsert(FileDto file);

	/**
	 * 전체 파일 목록
	 * @return fileList
	 */
	List<FileDto> selectFileList();

}
