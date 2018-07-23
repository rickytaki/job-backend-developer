package com.rickytaki.login;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class LoginApplication {

	@Bean
	public MapperFacade mapperFacade() {
		return new DefaultMapperFactory.Builder().build().getMapperFacade();
	}

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
	}
}
