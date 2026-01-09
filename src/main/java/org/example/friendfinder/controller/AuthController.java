package org.example.friendfinder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.friendfinder.common.api.ApiResponse;
import org.example.friendfinder.dto.AuthRequest;
import org.example.friendfinder.dto.AuthResponse;
import org.example.friendfinder.dto.RefreshRequest;
import org.example.friendfinder.dto.RegisterRequest;
import org.example.friendfinder.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Authentication endpoints.
 *
 * Base path: /api/v1/auth
 *
 * @author Mohamed Sayed
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "JWT access/refresh authentication APIs")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register (Multipart) - This is the main register endpoint.
     * Body: multipart/form-data
     * - data: JSON (RegisterRequest)
     * - image: file (optional)
     */
//    @Operation(summary = "Register new user (Multipart with optional image)")
//    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApiResponse<AuthResponse>> registerMultipart(
//            @Valid @RequestPart("data") RegisterRequest req,
//            @RequestPart(value = "image", required = false) MultipartFile image,
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) {
//        log.info("Register (Multipart) request received: email={}, hasImage={}",
//                req.getEmail(), image != null && !image.isEmpty());
//
//        String deviceId = DeviceCookie.getOrCreateDeviceId(request, response);
//        AuthResponse data = authService.register(req, image, request, deviceId);
//
//        log.info("Register (Multipart) success: email={}, hasImage={}",
//                req.getEmail(), image != null && !image.isEmpty());
//
//        return ResponseEntity.ok(ApiResponse.ok("Registered successfully.", data));
//    }

//    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApiResponse<AuthResponse>> registerMultipart(
//            @RequestPart("data") String dataJson,
//            @RequestPart(value = "image", required = false) MultipartFile image,
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws Exception {
//        RegisterRequest req = new ObjectMapper().readValue(dataJson, RegisterRequest.class);
//        String deviceId = DeviceCookie.getOrCreateDeviceId(request, response);
//        AuthResponse data = authService.register(req, image, request, deviceId);
//        return ResponseEntity.ok(ApiResponse.ok("Registered successfully.", data));
//    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AuthResponse>> registerMultipart(
            @Valid @RequestPart("data") RegisterRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String deviceId = DeviceCookie.getOrCreateDeviceId(request, response);
        AuthResponse data = authService.register(req, image, request, deviceId);
        return ResponseEntity.ok(ApiResponse.ok("Registered successfully.", data));
    }


    /**
     * Register JSON fallback endpoint (optional).
     * Body: application/json
     * This avoids Postman confusion between raw/multipart on same path.
     */
    @Operation(summary = "Register new user (JSON) - fallback endpoint")
    @PostMapping(value = "/register/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthResponse>> registerJson(@Valid @RequestBody RegisterRequest req,
                                                                  HttpServletRequest request,
                                                                  HttpServletResponse response) {
        log.info("Register (JSON) request received: email={}", req.getEmail());

        String deviceId = DeviceCookie.getOrCreateDeviceId(request, response);
        AuthResponse data = authService.register(req, null, request, deviceId);

        log.info("Register (JSON) success: email={}", req.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Registered successfully.", data));
    }

    @Operation(summary = "Login", description = "Authenticates credentials and returns access + refresh tokens.")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest req,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {
        log.info("Login request received: email={}", req.getEmail());

        String deviceId = DeviceCookie.getOrCreateDeviceId(request, response);
        AuthResponse data = authService.login(req, request, deviceId);

        log.info("Login success: email={}", req.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Logged in successfully.", data));
    }

    @Operation(summary = "Refresh tokens", description = "Rotates refresh token and returns new access + refresh pair.")
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest req,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) {
        log.info("Refresh request received");

        String deviceId = DeviceCookie.getOrCreateDeviceId(request, response);
        AuthResponse data = authService.refresh(req, request, deviceId);

        log.info("Refresh success");
        return ResponseEntity.ok(ApiResponse.ok("Tokens refreshed.", data));
    }

    @Operation(summary = "Logout", description = "Revokes all active refresh tokens for current user.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        log.info("Logout request received: email={}", authentication.getName());

        authService.logout(authentication.getName());

        log.info("Logout success: email={}", authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully.", null));
    }

    @Operation(summary = "Current user identity", description = "Returns authenticated user email (principal).")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<String>> me(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok("Current user.", authentication.getName()));
    }
}
