package com.syj;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.syj.controller.interceptor.MiniInterceptor;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/")
				.addResourceLocations("file:E:/CodeSpace/syj_videos_dev/");
	}

	// 声明拦截器bean
	@Bean
	public MiniInterceptor miniInterceptor() {
		return new MiniInterceptor();
	}

	// 注册拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**")
				.addPathPatterns("/video/upload", "/video/uploadCover").addPathPatterns("/bgm/**")
				.addPathPatterns("/video/userLike","/video/userUnLike")
				.excludePathPatterns("/user/queryPublisher");
		
		super.addInterceptors(registry);
	}
}
