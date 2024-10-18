package com.capstone.shop.user.v1.controller;

import com.capstone.shop.dto.AccessTokenResponse;
import com.capstone.shop.dto.ApiResponse;
import com.capstone.shop.dto.SignInRequest;
import com.capstone.shop.dto.SignUpRequest;
import com.capstone.shop.entity.User;
import com.capstone.shop.exception.ResourceNotFoundException;
import com.capstone.shop.security.CurrentUser;
import com.capstone.shop.security.UserPrincipal;
import com.capstone.shop.user.v1.repository.UserRepository;
import com.capstone.shop.user.v1.service.AuthServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthServiceImpl authService;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        Map<String, String> tokens = authService.signIn(signInRequest.getEmail(), signInRequest.getPassword());
        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        return ResponseEntity.ok(new AccessTokenResponse(accessToken, refreshToken));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signUpUser(
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getAuthProvider(),
                signUpRequest.getAddress(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getProfileImages(),
                signUpRequest.getRole()
        );
        // 회원 가입 성공 API 리턴
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }
    //TODO : REDIRECT 토큰을 통한 토큰 재발급, OAUTH2활성화
    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal){
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
