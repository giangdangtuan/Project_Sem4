package web.thaiticketmajor.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import web.thaiticketmajor.Services.AuthenticationService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = { "/index","/css/user/**","/user/login", "/concert/payment", "/user/signup", "/admin/login", "user/them", "/category/them", "/images/**","/image/**", "/css/admin/assets/**"};

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Autowired
    private AuthenticationService authenticationService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình quyền truy cập
        httpSecurity
            .authorizeHttpRequests(authorize -> 
                authorize
                    .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                    .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS).permitAll()
                    .anyRequest().authenticated()
            );

        // Cấu hình resource server cho JWT
        httpSecurity.oauth2ResourceServer(oauth2 -> 
            oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.addFilterBefore(jwtCookieFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public JwtCookieFilter jwtCookieFilter() {
        return new JwtCookieFilter(authenticationService); // Nếu cần pass service vào
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
