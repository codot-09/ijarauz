package com.example.ijara.entity;

import com.example.ijara.entity.base.BaseEntity;
import com.example.ijara.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
    @Column(nullable = false,unique = true)
    private String telegramChatId;

    private String username;
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

//    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return telegramChatId;
    }
}
