package com.timesphere.timesphere.config;

import com.timesphere.timesphere.util.JwtFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.timesphere.timesphere.entity.type.Role.ADMIN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain serSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/**").permitAll() // ✅ Cho phép mọi người đăng nhập & đăng ký
                                .requestMatchers("/api").hasRole(ADMIN.name())

//                                .requestMatchers("/api/user/**").permitAll()

//                        .requestMatchers("/api/admin/**").hasRole(ADMIN.name()) // ✅ Chỉ Admin được truy cập
//                        .requestMatchers(GET,"/api/admin/**").hasAuthority(ADMIN_READ.name())
//                        .requestMatchers(POST,"/api/admin/**").hasAuthority(ADMIN_CREATE.name())
//                        .requestMatchers(PUT,"/api/admin/**").hasAuthority(ADMIN_UPDATE.name())
//                        .requestMatchers(DELETE,"/api/admin/**").hasAuthority(ADMIN_DELETE.name())
//
//                        .requestMatchers("/api/premium/**").hasRole(PREMIUM.name()) // ✅ Chỉ Premium User được truy cập
//                        .requestMatchers(GET,"/api/premium/**").hasAuthority(PREMIUM_READ.name())
//                        .requestMatchers(POST,"/api/premium/**").hasAuthority(PREMIUM_CREATE.name())
//                        .requestMatchers(PUT,"/api/premium/**").hasAuthority(PREMIUM_UPDATE.name())
//                        .requestMatchers(DELETE,"/api/premium/**").hasAuthority(PREMIUM_DELETE.name())
//
//                        .requestMatchers("/api/user/**").hasAnyRole(FREE.name(), PREMIUM.name()) // ✅ Người dùng thông thường & Premium đều được phép
//                        .requestMatchers(GET,"/api/user/**").hasAnyAuthority(FREE_READ.name(),PREMIUM_READ.name())
//                        .requestMatchers(POST,"/api/user/**").hasAnyAuthority(FREE_CREATE.name(), PREMIUM_CREATE.name())
//                        .requestMatchers(PUT,"/api/user/**").hasAnyAuthority(FREE_UPDATE.name(), PREMIUM_UPDATE.name())
//                        .requestMatchers(DELETE,"/api/user/**").hasAnyAuthority(FREE_DELETE.name(), PREMIUM_DELETE.name())

                                .anyRequest().authenticated()

                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // 👈 frontend port
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // nếu gửi cookie hoặc Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities"); // Xác nhận trường authorities
//        grantedAuthoritiesConverter.setAuthorityPrefix(""); // Không thêm ROLE_ trước quyền hạn
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
//        return jwtAuthenticationConverter;
//    }
}
