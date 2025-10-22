package com.example.crm.config;
import com.example.crm.security.JwtFilter; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; import org.springframework.web.cors.CorsConfiguration; import org.springframework.web.cors.UrlBasedCorsConfigurationSource; import org.springframework.web.filter.CorsFilter; import java.util.List;
@Configuration @EnableMethodSecurity public class SecurityConfig {
    private final JwtFilter jwtFilter; public SecurityConfig(JwtFilter jwtFilter){ this.jwtFilter=jwtFilter; }
    @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http = http.cors().and().csrf().disable();
        http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
        http.authorizeHttpRequests().requestMatchers("/api/register","/api/login","/api/social/google","/api/social/google/exchange","/api/token/refresh").permitAll().anyRequest().authenticated();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean public BCryptPasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean public CorsFilter corsFilter(){ UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource(); CorsConfiguration config=new CorsConfiguration(); config.setAllowCredentials(true); config.setAllowedOrigins(List.of("http://localhost:5173","http://127.0.0.1:8000")); config.setAllowedHeaders(List.of("*")); config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); source.registerCorsConfiguration("/**", config); return new CorsFilter(source);} 
}
