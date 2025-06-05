package com.timesphere.timesphere;

import com.timesphere.timesphere.dto.RegisterRequest;
import com.timesphere.timesphere.entity.Role;
import com.timesphere.timesphere.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.timesphere.timesphere.entity.Role.*;

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
					.role(ADMIN)
					.build();
			System.out.println("Admin token: " + service.register(admin).getAccessToken());

			var free = RegisterRequest.builder()
					.first_name("free")
					.last_name("free")
					.email("free@gmail.com")
					.password("password")
					.role(FREE)
					.build();
			System.out.println("Free user token: " + service.register(free).getAccessToken());

			var premium = RegisterRequest.builder()
					.first_name("premium")
					.last_name("premium")
					.email("premium@gmail.com")
					.password("password")
					.role(PREMIUM)
					.build();
			System.out.println("Premium user token: " + service.register(premium).getAccessToken());
		};
	}
}
