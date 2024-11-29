package com.capstone.shop.user.v1.controller;

import com.capstone.shop.user.v1.dto.ApiResponse;
import com.capstone.shop.user.v1.service.TemporaryPasswordEmailSendService;
import com.capstone.shop.user.v1.service.VerificationEmailSendService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthFindPasswordController {
    private final TemporaryPasswordEmailSendService temporaryPasswordEmailSendService;
    @PostMapping("/lostmyPassword")
    public ResponseEntity<ApiResponse> sendTemporaryPasswordAndChangePassword(@RequestParam @Email String email){
        try{
            temporaryPasswordEmailSendService.sendTemporaryPasswordAndChangePassword(email);
            return ResponseEntity.ok(new ApiResponse(true, "임시비번 전송 성공."));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "임시비번 전송 실패."));
        }
    }
}
