package com.fivault.fivault.service;

import com.fivault.fivault.dto.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public AuthResponse signUp(String email, String password, HttpServletRequest httpRequest) {
        return null;
    }

    public AuthResponse signIn(String email, String password, HttpServletRequest httpRequest) {
        return null;
    }
}
