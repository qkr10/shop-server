package com.capstone.shop.user.v1.service;

import com.capstone.shop.dto.SignUpRequest;
import com.capstone.shop.entity.AuthProvider;
import com.capstone.shop.entity.Role;

import java.util.Map;

public interface AuthService {
    Map<String, String> signIn(String email, String password);
    SignUpRequest signUpUser(String name, String email, String password, AuthProvider authProvider, String phoneNumber, String address, String profileImages, Role role);
}
