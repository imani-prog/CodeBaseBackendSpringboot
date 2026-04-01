package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.User;
import com.example.codebasebackend.Entities.UserRole;
import com.example.codebasebackend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsernameFallsBackToEmailLookup() {
        User user = User.builder()
                .username("timothy")
                .email("timothyimani6@gmail.com")
                .passwordHash("$2a$10$7f2H8XpBqA6m1Dzu4CFx6.x2MFPQfEBqA6IHZV7V1lFRjs0G0v7xG")
                .role(UserRole.PATIENT)
                .build();

        when(userRepository.findByUsernameIgnoreCase("timothyimani6@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("timothyimani6@gmail.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("timothyimani6@gmail.com");

        assertEquals("timothy", details.getUsername());
        assertEquals(user.getPasswordHash(), details.getPassword());
    }

    @Test
    void loadUserByUsernameThrowsWhenIdentifierNotFound() {
        when(userRepository.findByUsernameIgnoreCase("missing")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("missing"));
    }
}

