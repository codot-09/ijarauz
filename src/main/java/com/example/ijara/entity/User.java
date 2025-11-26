package com.example.ijara.entity;

import com.example.ijara.entity.base.BaseEntity;
import com.example.ijara.entity.enums.AuthType;
import com.example.ijara.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Column(unique = true, length = 50)
    private String telegramChatId;

    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 100)
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AuthType authType = AuthType.EMAIL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean deleted = false;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (email == null && telegramChatId == null) {
            throw new IllegalStateException("User must have email or telegramChatId");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return AuthType.EMAIL.equals(this.authType) ? passwordHash : "";
    }

    @Override
    public String getUsername() {
        return AuthType.EMAIL.equals(this.authType) ? email : telegramChatId;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; }

    public String getFullName() {
        return (firstName != null ? firstName : "") +
                (lastName != null ? " " + lastName : "");
    }
}
