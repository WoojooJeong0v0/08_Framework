package edu.kh.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kh.demo.mapper.TestMapper;

// @Service
// service 역할 (비즈니스 로직 처리) 임을 명시
// bean 등록 (== Spring이 관리하는 객체 == IOC)


@Service
public class UserServiceImpl implements UserService {
	
	/*
	 * @Autowired
	 * - 등록된 Bean 중에서 
	 * 자료형이 같은 Bean을 얻어와서 필드에 대입해준다
	 * == DI (의존성 주입) // 내가 하는 게 아니면 의존이라고 생각해도 괜찮다!
	 */
	
	@Autowired
	private TestMapper mapper;

	@Override
	public String selectUserName(int userNo) {
		
		return mapper.selectUserName(userNo);
	}

}
