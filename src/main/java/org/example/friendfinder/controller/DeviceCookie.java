package org.example.friendfinder.controller;

import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

/**
 * Creates and reads a stable device id for the same browser using a cookie.
 *
 * @author Mohamed Sayed
 */
@Slf4j
public final class DeviceCookie {

    private DeviceCookie() {}

    public static final String DEVICE_COOKIE = "DEVICE_ID";

    public static String getOrCreateDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String existing = null;

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (DEVICE_COOKIE.equals(c.getName())) {
                    existing = c.getValue();
                    break;
                }
            }
        }

        if (existing != null && !existing.isBlank()) {
            log.debug("Device cookie found. name={}, valuePresent=true", DEVICE_COOKIE);
            return existing;
        }

        String deviceId = UUID.randomUUID().toString();

        Cookie cookie = new Cookie(DEVICE_COOKIE, deviceId);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setSecure(false); // production: true with HTTPS
        cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year

        response.addCookie(cookie);

        log.info("Device cookie created. name={}, maxAgeSec={}, secure={}, httpOnly={}",
                DEVICE_COOKIE, cookie.getMaxAge(), cookie.getSecure(), cookie.isHttpOnly());

        return deviceId;
    }
}
