package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.UpdateProfileRequest;
import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.exception.ForbiddenException;
import com.example.ijara.repository.UserRepository;
import com.example.ijara.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ApiResponse<UserResponse> getProfile(User user) {
        return ApiResponse.success(toResponse(user));
    }

    @Override
    public ApiResponse<Page<UserResponse>> searchUsers(String query, UserRole role, Pageable pageable) {
        User current = getCurrentUser();
        if (!current.getRole().equals(UserRole.ADMIN)) {
            throw new ForbiddenException("Faqat admin qidiruv qila oladi");
        }
        Page<User> page = userRepository.searchUsers(query, role.name(), pageable);
        return ApiResponse.success(page.map(this::toResponse));
    }

    @Override
    public ApiResponse<UserResponse> getUserById(UUID userId) {
        User current = getCurrentUser();
        User target = getUser(userId);

        if (!current.getRole().equals(UserRole.ADMIN) && !current.getId().equals(userId)) {
            throw new ForbiddenException("Faqat o‘z profilini ko‘rish mumkin");
        }
        return ApiResponse.success(toResponse(target));
    }

    @Override
    @Transactional
    public ApiResponse<String> updateProfile(User user, UpdateProfileRequest req) {
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());

        User saved = userRepository.save(user);
        return ApiResponse.success("Ma'lumotlar yangilandi");
    }

    @Override
    @Transactional
    public ApiResponse<String> blockUser(UUID userId) {
        User current = getCurrentUser();
        if (!current.getRole().equals(UserRole.ADMIN)) throw new ForbiddenException();

        User target = getUser(userId);
        if (target.getRole().equals(UserRole.ADMIN)) {
            return ApiResponse.error("Admin bloklanmaydi");
        }
        target.setActive(false);
        userRepository.save(target);
        return ApiResponse.success("Foydalanuvchi bloklandi");
    }

    @Override
    @Transactional
    public ApiResponse<String> unblockUser(UUID userId) {
        User current = getCurrentUser();
        if (!current.getRole().equals(UserRole.ADMIN)) throw new ForbiddenException();

        User target = getUser(userId);
        target.setActive(true);
        userRepository.save(target);
        return ApiResponse.success("Foydalanuvchi faollashtirildi");
    }

    @Override
    @Transactional
    public ApiResponse<String> changeRole(User user,UUID targetId, UserRole newRole) {
        if (!user.getRole().equals(UserRole.ADMIN)){
            throw new ForbiddenException();
        }

        User target = userRepository.findById(targetId)
                        .orElseThrow(() -> new DataNotFoundException("Foydalanuvchi topilmadi"));

        target.setRole(newRole);

        userRepository.save(target);
        return ApiResponse.success("Rol o‘zgartirildi: " + newRole);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ForbiddenException("Autentifikatsiya talab qilinadi");
        }
        return (User) auth.getPrincipal();
    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Foydalanuvchi topilmadi"));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .telegramChatId(u.getTelegramChatId())
                .identifier(u.getUsername())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .role(u.getRole())
                .active(u.isActive())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
