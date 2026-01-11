package org.example.friendfinder.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.example.friendfinder.security.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WsAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // نشتغل فقط على CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || authHeader.isBlank()) {
                return message;
            }

            if (!authHeader.startsWith("Bearer ")) {
                return message;
            }

            String token = authHeader.substring(7).trim();
            if (token.isEmpty()) return message;

            try {
                Jws<Claims> jws = jwtService.parseAccessToken(token);
                String email = jws.getBody().getSubject(); // subject = email عندك

                if (email != null && !email.isBlank()) {
                    Authentication auth =
                            new UsernamePasswordAuthenticationToken(email, null, List.of());
                    accessor.setUser(auth);
                }
            } catch (Exception ex) {
                // توكن غير صالح/منتهي => نخلي الاتصال بدون auth (هيترفض في controller لو اشترطت auth)
                // ممكن كمان تعمل accessor.setUser(null);
            }
        }

        return message;
    }
}
