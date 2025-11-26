package com.example.ijara.security;

import com.example.ijara.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrTelegramId) throws UsernameNotFoundException {

        return userRepository.findByEmailAndActiveTrue(usernameOrTelegramId)
                .or(() -> userRepository.findByTelegramChatIdAndActiveTrue(usernameOrTelegramId))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email or telegramChatId: " + usernameOrTelegramId
                ));
    }
}
