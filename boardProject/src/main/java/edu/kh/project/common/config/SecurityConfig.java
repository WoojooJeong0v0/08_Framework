package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration // 서버 실행 될 때 객체로 만들어져 내부 메서드 모두 수행함
							 // -> 서버에 적용될 설정, Bean 생성에 사용 (DBconfig에 자세한 설명있음)
public class SecurityConfig {
	
	@Bean // 메서드에서 반환된 객체를 Spring Bean으로 등록한다!! 는 어노테이션
				// -> Bean 으로 등록되면 생성된 객체를 Spring이 관리하는 IOC 적용
				// -> 필요한 곳에 의존성 주입(DI) 가능해짐!!
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
