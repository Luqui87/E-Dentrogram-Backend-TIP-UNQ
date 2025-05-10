package com.example.E_Dentogram.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration (
    private val authenticationProvicer: AuthenticationProvider
){
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain=
        http
            .csrf{it.disable()}
            .headers{it.frameOptions{it.disable()}}
            .authorizeHttpRequests{
                it
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers( "/auth", "auth/refresh", "/error", "/h2-console/", "/h2-console/**", "/swagger-ui/**","/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers( "/register","/login")
                    .permitAll()
                    .requestMatchers("/dentist/**")
                    .authenticated()
                    .requestMatchers("/patient/**")
                    .authenticated()
                    .requestMatchers("/tooth/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated()
            }
            .sessionManagement{
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvicer)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()

        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:5174")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")

        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}

