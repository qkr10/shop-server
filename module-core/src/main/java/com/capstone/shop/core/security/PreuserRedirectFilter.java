package com.capstone.shop.core.security;

import com.capstone.shop.core.domain.entity.User;
import com.capstone.shop.core.domain.enums.Role;
import com.capstone.shop.core.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
@RequiredArgsConstructor
public class PreuserRedirectFilter extends OncePerRequestFilter {
    private static final String ADDITIONAL_INFO_URL = "https://induk.shop/additionalInfo";
    private final UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // authentication 유효성검사
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없음"));

            if (user.getRole() == Role.ROLE_PREUSER) {
                // 이미 추가 정보 입력 페이지라면 리다이렉트 안함
                String requestURI = request.getRequestURI();
                System.out.println("Request URI: " + requestURI);
                if (!requestURI.equals("/additionalInfo")) {
                    response.sendRedirect(ADDITIONAL_INFO_URL);

                    return; //다음체이닝 방지
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
