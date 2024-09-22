package com.files;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class EmployeeApplication {

	@Bean
	public ModelMapper modelMapper () {
		return new ModelMapper();
	}
	public static void main(String[] args) {
		SpringApplication.run(EmployeeApplication.class, args);
		System.out.println("application started");
	}

}
