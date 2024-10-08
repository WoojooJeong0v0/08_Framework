package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.kh.project.common.interceptor.BoardNameInterceptor;
import edu.kh.project.common.interceptor.BoardTypeInterceptor;

@Configuration // 서버 실행 시 내부 메서드 모두 실행 됨
public class InterceptorConfig implements WebMvcConfigurer {
 // WebMvcConfigurer 상속
	
	// BoardTypeInterceptor 클래스를 Bean 으로 등록
	@Bean // 메서드 반환된 객체를 Bean 으로 등록
	public BoardTypeInterceptor boardTypeInterceptor() {
		return new BoardTypeInterceptor();
	}
	
	// BoardNameInterceptor 클래스를 Bean 으로 등록
	@Bean
	public BoardNameInterceptor boardNameInterceptor() {
		return new BoardNameInterceptor();
	}
	
	
	
	// 요청/응답 가로채 동작할 인터셉터 추가
	// == 해당 메서드에 작성된 인터셉터 객체가 동작
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// boardTypeInterceptor Bean 을 인터셉터로 등록(registry)
		registry.addInterceptor(boardTypeInterceptor())
			.addPathPatterns("/**") // "/" 이하 모든 요청 가로챔
			.excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico"); // 가로채지 말아라!
		
		// /board 또는 /editBoard 이하 모든 요청 가로채서 동작하는
		// BoardNameInterceptor 등록
		registry.addInterceptor(boardNameInterceptor())
		.addPathPatterns("/board/**", "/editBoard/**");
		
	}
	
	
	
	
	
} // end
