package com.example.ijara.security;

import com.example.ijara.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String telegramChatId) throws UsernameNotFoundException {
        return userRepository.findByTelegramChatId(telegramChatId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Foydalanuvchi topilmadi: " + telegramChatId
                ));
    }
}
