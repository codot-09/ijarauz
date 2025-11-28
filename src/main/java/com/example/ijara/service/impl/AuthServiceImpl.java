package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.LoginRequest;
import com.example.ijara.dto.request.RegisterRequest;
import com.example.ijara.dto.request.TelegramLoginRequest;
import com.example.ijara.dto.response.LoginResponse;
import com.example.ijara.entity.User;
import com.example.ijara.entity.VerificationCode;
import com.example.ijara.entity.enums.AuthType;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.exception.UnauthorizedException;
import com.example.ijara.repository.UserRepository;
import com.example.ijara.repository.VerificationCodeRepository;
import com.example.ijara.security.JwtProvider;
import com.example.ijara.service.AuthService;
import com.example.ijara.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final VerificationCodeRepository verificationCodeRepository;

    @Value("${telegram.bot.token}")
    private String botToken;

    // -----------------------------------------
    //            TELEGRAM LOGIN
    // -----------------------------------------
    @Override
    public ApiResponse<LoginResponse> telegramLogin(TelegramLoginRequest request) {

        if (!verifyTelegramAuth(request)) {
            return ApiResponse.error("Telegram autentifikatsiyasi muvaffaqiyatsiz");
        }

        User user = userRepository.findByTelegramChatId(request.getChatId())
                .orElseGet(() -> createTelegramUser(request));

        if (user.isDeleted()) {
            return ApiResponse.error("Hisob o‘chirilgan");
        }

        return ApiResponse.success(buildLoginResponse(user));
    }

    private User createTelegramUser(TelegramLoginRequest req) {
        User user = User.builder()
                .telegramChatId(req.getChatId())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .username(req.getUsername())
                .role(UserRole.NORMAL_USER)
                .authType(AuthType.TELEGRAM)
                .active(true)
                .build();
        return userRepository.save(user);
    }

    // -----------------------------------------
    //            REGISTER (EMAIL)
    // -----------------------------------------
    @Override
    public ApiResponse<String> register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            return ApiResponse.error("Email allaqachon ro‘yxatdan o‘tgan");
        }

        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .role(UserRole.NORMAL_USER)
                .authType(AuthType.EMAIL)
                .active(false) // Shart: avval tasdiqlansin
                .build();

        userRepository.save(user);

        verificationCodeService.generateAndSendCode(user);

        return ApiResponse.success("Tasdiqlash kodi emailga yuborildi");
    }

    // -----------------------------------------
    //            LOGIN (EMAIL)
    // -----------------------------------------
    @Override
    public ApiResponse<LoginResponse> login(LoginRequest req) {

        User user = userRepository.findByEmailAndActiveTrue(req.getEmail())
                .orElseThrow(() -> new DataNotFoundException("Foydalanuvchi topilmadi"));

        if (user.isDeleted()) {
            return ApiResponse.error("Hisob o‘chirilgan");
        }

        if (!AuthType.EMAIL.equals(user.getAuthType())) {
            return ApiResponse.error("Bu foydalanuvchi Telegram orqali ro‘yxatdan o‘tgan");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return ApiResponse.error("Parol noto‘g‘ri");
        }

        return ApiResponse.success(buildLoginResponse(user));
    }

    // -----------------------------------------
    //       EMAIL VERIFICATION
    // -----------------------------------------
    @Override
    public ApiResponse<LoginResponse> verifyEmail(Integer code) {

        VerificationCode vc = verificationCodeRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Noto‘g‘ri tasdiqlash kodi"));

        if (vc.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ApiResponse.error("Tasdiqlash kodi muddati o‘tgan");
        }

        User user = vc.getUser();

        if (user.isDeleted()) {
            return ApiResponse.error("Hisob o‘chirilgan");
        }

        user.setActive(true);
        userRepository.save(user);

        verificationCodeRepository.delete(vc);

        return ApiResponse.success(buildLoginResponse(user));
    }

    @Override
    public ApiResponse<String> resetPassword(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new DataNotFoundException("Foydalanuvchi topimadi"));

        user.setActive(false);
        userRepository.save(user);

        verificationCodeService.generateAndSendCode(user);

        return ApiResponse.success("Emailga tasdiqlash uchun kod yuborildi");
    }

    @Override
    public ApiResponse<String> checkCode(Integer code) {
        VerificationCode vc = verificationCodeRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Noto'g'ri tasdiqlash kodi"));

        if (vc.getExpiresAt().isBefore(LocalDateTime.now())){
            return ApiResponse.error("Muddati o'tgan tasdiqlash kodi");
        }

        User user = vc.getUser();

        user.setActive(true);
        userRepository.save(user);

        return ApiResponse.success("Kod tasdiqlandi.Parolni o'zgartirish mumkin");
    }

    @Override
    public ApiResponse<String> setPassword(String newPassword, String email) {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new DataNotFoundException("Foydalanuvchi topilmadi"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ApiResponse.success("Parol o'zgartirildi. Login qilish mumkin");
    }

    // -----------------------------------------
    //       TELEGRAM AUTH VERIFICATION
    // -----------------------------------------
    private boolean verifyTelegramAuth(TelegramLoginRequest req) {
        try {
            TreeMap<String, String> data = new TreeMap<>();
            data.put("id", req.getChatId());
            data.put("first_name", req.getFirstName());
            if (req.getLastName() != null) data.put("last_name", req.getLastName());
            if (req.getUsername() != null) data.put("username", req.getUsername());
            data.put("auth_date", req.getAuthDate());

            String dataCheckString = data.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            // Bot token → SHA256 → secretKey
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = sha256.digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));

            byte[] hash = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            String calculatedHash = toHex(hash);

            return calculatedHash.equalsIgnoreCase(req.getHash());

        } catch (Exception e) {
            return false;
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // -----------------------------------------
    //            BUILD LOGIN RESPONSE
    // -----------------------------------------
    private LoginResponse buildLoginResponse(User user) {
        String subject = user.getTelegramChatId() != null ?
                user.getTelegramChatId() :
                user.getEmail();

        String token = jwtProvider.generateToken(subject);

        return LoginResponse.builder()
                .accessToken(token)
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .build();
    }
}
