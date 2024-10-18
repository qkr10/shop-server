package com.capstone.shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 12)
    @NotNull
    private String name;

    @Column(name = "email", length = 100, unique = true)
    @NotNull
    @Size(max = 100)
    private String email;

    @JsonIgnore
    @Column(name = "password", length = 128)
    @NotNull
    private String password;

    @Column(name = "address", length = 128)
    @NotNull
    private String address;

    @Column(name = "phone_number", length = 15)
    @Pattern(regexp = "^[0-9]{7,15}$", message = "전화번호는 7자 이상 15자 이하의 숫자만 포함해야 합니다.")
    @NotNull
    private String phoneNumber;

    @Column(name = "dealing_count")
    private int dealingCount = 0;

    @Column(name = "reputation")
    private int reputation = 30;

    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    @Column(name = "auth_provider", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AuthProvider authProvider;

    @Column(name = "profile_Images", length = 512)
    @NotNull
    private String profileImages;

    @Column(name = "CREATED_AT", updatable = false)
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "MODIFIED_AT")
    private LocalDateTime modifiedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // 생성 시 현재 시간으로 설정
        this.modifiedAt = LocalDateTime.now(); // 생성 시 현재 시간으로 설정
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedAt = LocalDateTime.now(); // 수정 시 현재 시간으로 업데이트
    }

    public User(
            @NotNull @Size(max = 12) String name,
            @NotNull @Size(max = 100) String email,
            @NotNull AuthProvider authProvider
    ) {
        this.name = name;
        this.password = "NO_PASS"; //소셜로그인은 패스워드가 없음
        this.email = email != null ? email : "NO_EMAIL";
    }
}
