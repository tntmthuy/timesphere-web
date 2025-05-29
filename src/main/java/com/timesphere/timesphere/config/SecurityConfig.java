package com.timesphere.timesphere.config;

import com.timesphere.timesphere.controller.AdminController;
import com.timesphere.timesphere.entity.Role;
import com.timesphere.timesphere.repository.UserRepository;
import com.timesphere.timesphere.util.JwtFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.timesphere.timesphere.entity.Permission.*;
import static com.timesphere.timesphere.entity.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepo;

    @Bean
    public SecurityFilterChain serSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // ✅ Cho phép mọi người đăng nhập & đăng ký

                        .requestMatchers("/admin/**").hasRole(ADMIN.name()) // ✅ Chỉ Admin được truy cập
                        .requestMatchers(GET,"/admin/**").hasAuthority(ADMIN_READ.name())
                        .requestMatchers(POST,"/admin/**").hasAuthority(ADMIN_CREATE.name())
                        .requestMatchers(PUT,"/admin/**").hasAuthority(ADMIN_UPDATE.name())
                        .requestMatchers(DELETE,"/admin/**").hasAuthority(ADMIN_DELETE.name())

                        .requestMatchers("/premium/**").hasRole(PREMIUM.name()) // ✅ Chỉ Premium User được truy cập
                        .requestMatchers(GET,"/premium/**").hasAuthority(PREMIUM_READ.name())
                        .requestMatchers(POST,"/premium/**").hasAuthority(PREMIUM_CREATE.name())
                        .requestMatchers(PUT,"/premium/**").hasAuthority(PREMIUM_UPDATE.name())
                        .requestMatchers(DELETE,"/premium/**").hasAuthority(PREMIUM_DELETE.name())

                        .requestMatchers("/user/**").hasAnyRole(FREE.name(), PREMIUM.name()) // ✅ Người dùng thông thường & Premium đều được phép
                        .requestMatchers(GET,"/user/**").hasAnyAuthority(FREE_READ.name(),PREMIUM_READ.name())
                        .requestMatchers(POST,"/user/**").hasAnyAuthority(FREE_CREATE.name(), PREMIUM_CREATE.name())
                        .requestMatchers(PUT,"/user/**").hasAnyAuthority(FREE_UPDATE.name(), PREMIUM_UPDATE.name())
                        .requestMatchers(DELETE,"/user/**").hasAnyAuthority(FREE_DELETE.name(), PREMIUM_DELETE.name())

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
