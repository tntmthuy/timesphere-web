package com.timesphere.timesphere;

import com.timesphere.timesphere.dto.RegisterRequest;
import com.timesphere.timesphere.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TimesphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimesphereApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			var admin = RegisterRequest.builder()
					.first_name("admin")
					.last_name("admin")
					.email("admin@gmail.com")
					.password("password")
					.build();
		};
	}
}
