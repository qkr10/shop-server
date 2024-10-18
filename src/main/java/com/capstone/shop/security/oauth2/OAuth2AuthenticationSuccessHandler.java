package com.capstone.shop.security.oauth2;

import com.capstone.shop.config.AppProperties;
import com.capstone.shop.entity.UserRefreshToken;
import com.capstone.shop.exception.BadRequestException;
import com.capstone.shop.entity.User;
import com.capstone.shop.security.TokenProvider;
import com.capstone.shop.security.UserPrincipal;
//import com.capstone.shop.util.CookieUtils;
import com.capstone.shop.user.v1.repository.UserRefreshTokenRepository;
import com.capstone.shop.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.capstone.shop.util.JwtTokenUtil;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final TokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;


    //oauth2인증이 성공적으로 이뤄졌을 때 실행된다
    //token을 포함한 uri을 생성 후 인증요청 쿠키를 비워주고 redirect 한다.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        //String targetUrl = determineTargetUrl(request, response, authentication);
        //String targetUrl = "http://localhost:3000/";
        String Uri = "http://localhost:3000/oauth2/redirect";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(Uri);
        Long userId = ((UserPrincipal)authentication.getPrincipal()).getId();

        String token = jwtTokenUtil.generateToken(userId);

        builder.queryParam("token", token);

        String targetUrl = builder.build().toUriString();

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //token을 생성하고 이를 포함한 프론트엔드로의 uri를 생성한다.
    //아니 redirect URI 왜 안되냐고?????????????????????????????? <<< 이제돼ㅑ씅ㅁ
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        Long userId = ((UserPrincipal)authentication.getPrincipal()).getId();
        String token = jwtTokenUtil.generateToken(userId);
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }
    //토큰 리프래쉬 로직




    //인증정보 요청 내역을 쿠키에서 삭제한다.
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    //application.properties에 등록해놓은 Redirect uri가 맞는지 확인한다. (app.redirect-uris)
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}