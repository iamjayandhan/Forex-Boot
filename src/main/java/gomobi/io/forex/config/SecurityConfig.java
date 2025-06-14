package gomobi.io.forex.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gomobi.io.forex.security.JwtFilter;
import gomobi.io.forex.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/auth/fetchAll").permitAll() //[TEST]

                .requestMatchers(HttpMethod.POST, "/api/stocks").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/stocks").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/stocks").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/stocks/paginated").authenticated()
                
                .requestMatchers(HttpMethod.GET, "/api/stocks").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/auth/logout").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/auth/update").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated() //[TEST]
                .requestMatchers(HttpMethod.GET, "/api/auth/**").authenticated()
                
                .requestMatchers("/api/portfilio/**").authenticated()
                
                .requestMatchers("/api/fpx/**").permitAll()
                
                .requestMatchers("/api/watchlist/**").authenticated()
                //.requestMatchers("/api/stocks/**").authenticated()
                .anyRequest().permitAll() // /api/otp/sendOTP
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);  // Using CustomUserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider()));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
