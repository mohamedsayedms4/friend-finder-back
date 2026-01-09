package org.example.friendfinder.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.friendfinder.common.exception.ConflictException;
import org.example.friendfinder.common.exception.UnauthorizedException;
import org.example.friendfinder.dto.AuthRequest;
import org.example.friendfinder.dto.AuthResponse;
import org.example.friendfinder.dto.RefreshRequest;
import org.example.friendfinder.dto.RegisterRequest;
import org.example.friendfinder.mapper.UserMapper;
import org.example.friendfinder.model.Role;
import org.example.friendfinder.model.User;
import org.example.friendfinder.model.UserProfile;
import org.example.friendfinder.model.token.RefreshToken;
import org.example.friendfinder.repository.RefreshTokenRepository;
import org.example.friendfinder.repository.RoleRepository;
import org.example.friendfinder.repository.UserProfileRepository;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.security.JwtService;
import org.example.friendfinder.security.RequestFingerprint;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication business logic.
 *
 * @author Mohamed Sayed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    public AuthResponse register(RegisterRequest req, MultipartFile image, HttpServletRequest request, String deviceId) {
        log.info("Register attempt: email={}", req.getEmail());

        if (userRepository.existsByEmail(req.getEmail())) {
            log.warn("Register rejected: email already registered: email={}", req.getEmail());
            throw new ConflictException("Email already registered.");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        User user = User.builder()
                .email(req.getEmail())
                .role(userRole)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .enabled(true)
                .build();

        user = userRepository.save(user);
        log.info("User created: userId={}, email={}", user.getId(), user.getEmail());

        String storedPath = null;
        if (image != null && !image.isEmpty()) {
            storedPath = fileStorageService.storeProfilePicture(image, user.getId());
            log.info("Profile picture stored: userId={}, path={}", user.getId(), storedPath);
        } else {
            log.debug("No profile picture provided at register: userId={}", user.getId());
        }

        UserProfile profile = UserProfile.builder()
                .user(user)
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .biography(req.getBiography())
                .phoneNumber(req.getPhoneNumber())
                .profilePicture(storedPath)
                .build();

        userProfileRepository.save(profile);
        log.info("User profile saved: userId={}, hasImage={}", user.getId(), storedPath != null);

        return issueTokensForUser(user, request, deviceId);
    }

    @Transactional
    public AuthResponse login(AuthRequest req, HttpServletRequest request, String deviceId) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        if (!auth.isAuthenticated()) throw new UnauthorizedException("Invalid credentials.");

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));

        return issueTokensForUser(user, request, deviceId);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest req, HttpServletRequest request, String deviceId) {
        String presented = req.getRefreshToken();

        Claims claims;
        try {
            claims = jwtService.parseRefreshToken(presented).getBody();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid refresh token.");
        }

        String email = claims.getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token."));

        RefreshToken stored = refreshTokenRepository.findByToken(presented)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not recognized."));

        if (stored.isRevoked() || stored.isExpired()) {
            throw new UnauthorizedException("Refresh token is revoked or expired.");
        }

        String ip = RequestFingerprint.clientIp(request);
        String uaHash = RequestFingerprint.sha256Hex(RequestFingerprint.userAgent(request));
        String deviceHash = RequestFingerprint.sha256Hex(deviceId);

        if (!stored.getIpAddress().equals(ip)) throw new UnauthorizedException("Refresh token is not valid for this IP.");
        if (!stored.getUserAgentHash().equals(uaHash)) throw new UnauthorizedException("Refresh token is not valid for this browser.");
        if (!stored.getDeviceIdHash().equals(deviceHash)) throw new UnauthorizedException("Refresh token is not valid for this device.");

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return issueTokensForUser(user, request, deviceId);
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Not authenticated."));

        var active = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
        active.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(active);
    }

    private AuthResponse issueTokensForUser(User user, HttpServletRequest request, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new UnauthorizedException("Missing device id.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("role", user.getRole().getName());

        String access = jwtService.generateAccessToken(user.getEmail(), claims);
        String refresh = jwtService.generateRefreshToken(user.getEmail(), claims);

        Instant refreshExp = Instant.now().plusSeconds(jwtService.refreshExpiresInSec());

        String ip = RequestFingerprint.clientIp(request);
        String uaHash = RequestFingerprint.sha256Hex(RequestFingerprint.userAgent(request));
        String deviceHash = RequestFingerprint.sha256Hex(deviceId);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(refresh)
                        .expiresAt(refreshExp)
                        .revoked(false)
                        .ipAddress(ip)
                        .userAgentHash(uaHash)
                        .deviceIdHash(deviceHash)
                        .build()
        );

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(access)
                .refreshToken(refresh)
                .accessExpiresInSec(jwtService.accessExpiresInSec())
                .refreshExpiresInSec(jwtService.refreshExpiresInSec())
                .user(userMapper.toUserResponse(user))
                .build();
    }
}
