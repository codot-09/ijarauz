package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.AdminLoginRequest;
import com.example.ijara.dto.request.LoginRequest;
import com.example.ijara.dto.response.LoginResponse;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.exception.UnauthorizedException;
import com.example.ijara.repository.UserRepository;
import com.example.ijara.security.JwtProvider;
import com.example.ijara.service.AuthService;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        if (!verifyTelegramAuth(request)) {
            return ApiResponse.error("Invalid Telegram signature");
        }
        User user = userRepository.findByTelegramChatId(request.getChatId())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .telegramChatId(request.getChatId())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .active(true)
                            .role(UserRole.NORMAL_USER)
                            .build();
                    return userRepository.save(newUser);
                });
        if (!user.isActive()) {
            throw new UnauthorizedException("Kirish taqiqlanadi");
        }
        String token = jwtProvider.generateToken(user.getTelegramChatId());
        LoginResponse response = new LoginResponse(token, user.getRole().name());
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<LoginResponse> adminLogin(AdminLoginRequest request) {
        User admin = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Admin topilmadi"));
        if (!encoder.matches(request.getPassword(), admin.getPassword())){
            return ApiResponse.error("Ma'lumotlar noto'g'ri");
        }
        String token = jwtProvider.generateToken(admin.getTelegramChatId());
        LoginResponse response = new LoginResponse(token, admin.getRole().name());
        return ApiResponse.success(response);
    }

    private boolean verifyTelegramAuth(LoginRequest request) {
        try {
            String botToken = "8262839503:AAGeXC5t_TwuvJH5A0ZZuR6hoHzKuV_5CPg";
            Map<String, String> data = new TreeMap<>();
            data.put("auth_date", request.getAuthDate());
            data.put("first_name", request.getFirstName());
            if (request.getLastName() != null) data.put("last_name", request.getLastName());
            if (request.getUsername() != null) data.put("username", request.getUsername());
            data.put("id", request.getChatId());
            String dataCheckString = data.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] hmac = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            String calculatedHash = DatatypeConverter.printHexBinary(hmac).toLowerCase();
            return calculatedHash.equals(request.getHash());
        } catch (Exception e) {
            return false;
        }
    }
}
