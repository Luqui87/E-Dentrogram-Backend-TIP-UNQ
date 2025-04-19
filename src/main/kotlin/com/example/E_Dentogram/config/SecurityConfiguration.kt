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
                    .requestMatchers( "/auth", "auth/refresh", "/error", "/h2-console/", "/h2-console/**", "/swagger-ui/**","/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/register","/login")
                    .permitAll()
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

}