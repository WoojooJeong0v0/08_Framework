package edu.kh.project.main.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.main.mapper.MainMapper;
import edu.kh.project.member.dto.Member;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor // 생성자 생성->생성자 방식 의존성 주입(필드 여러 개 한 번에)
public class MainServiceImpl implements MainService {
	
	private final MainMapper mapper; // DI 의존성 주입 되어 있음

	@Override
	public List<Member> selectMemberList() {
		
		return mapper.selectMemberList();
	}

}
