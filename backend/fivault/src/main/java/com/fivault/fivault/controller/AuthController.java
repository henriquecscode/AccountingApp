package com.fivault.fivault.controller;

import com.fivault.fivault.dto.AuthResponse;
import com.fivault.fivault.dto.SignInRequest;
import com.fivault.fivault.dto.SignUpRequest;
import com.fivault.fivault.service.AuthService;
import com.fivault.fivault.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(
            @RequestBody SignUpRequest request,
            HttpServletRequest httpRequest
    ) {
        try {
            AuthResponse response = authService.signUp(
                    request.email(),
                    request.password(),
                    httpRequest
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, null, e.getMessage(), null));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, null, e.getMessage(), null));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(
            @RequestBody SignInRequest request,
            HttpServletRequest httpRequest
    ){
        try {
            AuthResponse response = authService.signIn(
                    request.email(),
                    request.password(),
                    httpRequest
            );
            return ResponseEntity.ok(response);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, e.getMessage(), null));
        }
        catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse(null, null, e.getMessage(), null));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, null, e.getMessage(), null));
        }
    }
}
