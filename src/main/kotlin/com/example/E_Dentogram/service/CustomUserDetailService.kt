package com.example.E_Dentogram.service

import com.example.E_Dentogram.repository.DentistRepository
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias ApplicationUser = com.example.E_Dentogram.model.Dentist

@Primary
@Service
class CustomUserDetailService(val DentistRepository: DentistRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
        DentistRepository.findByUsername(username)
            ?.mapToUserDetails()
            ?:throw UsernameNotFoundException("Not found")

    private fun ApplicationUser.mapToUserDetails(): UserDetails =
        User.builder()
            .username(this.username)
            .password(this.password)
            .roles(this.role.name)
            .build()

}