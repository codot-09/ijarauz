package com.example.ijara.component;

import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Override
    public void run(String... args) throws Exception {
        if (ddl.equals("create") || ddl.equals("create-drop")){
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .role(UserRole.ADMIN)
                    .active(true)
                    .telegramChatId("1234567")
                    .build();

            userRepository.save(admin);
        }
    }
}
