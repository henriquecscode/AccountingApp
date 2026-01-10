package com.fivault.fivault.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${app.cookie.secure:true}")
    private boolean secure;

    @Value("${app.cookie.same-site:Strict}")
    private String   sameSitePolicy;

    @Value("${app.cookie.domain:}")
    private String domain;

        @Value("${app.refresh-token.max-age:2678400}")  // 31 days in seconds
    private int refreshTokenMaxAge;

    /**
     * Create a refresh token cookie
     * @param token The refresh token value
     * @return Configured cookie ready to add to response
     */
    public ResponseCookie createRefreshTokenCookie(String token) {

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(secure)  // From config
                .path("/")
                .maxAge(refreshTokenMaxAge)
                .sameSite(sameSitePolicy);  // "Lax", "Strict", or "None"

        if (!domain.isEmpty()) {
            builder.domain(domain);
        }

        return builder.build();

//        Cookie cookie = new Cookie("refreshToken", token);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(secure);  // From config
//        cookie.setPath("/");
//        cookie.setMaxAge(refreshTokenMaxAge);
//        cookie.setAttribute("SameSite", sameSitePolicy);
//
//        if (!domain.isEmpty()) {
//            cookie.setDomain(domain);
//        }
//
//        return cookie;
    }

    /**
     * Create a cookie to delete the refresh token
     * @return Cookie that will delete the refresh token
     */
    public Cookie createDeleteRefreshTokenCookie() {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // Delete immediately

        if (!domain.isEmpty()) {
            cookie.setDomain(domain);
        }

        return cookie;
    }

    /**
     * Extract refresh token from request cookies
     * @param cookies Array of cookies from request
     * @return Refresh token value or null if not found
     */
    public String getRefreshTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}