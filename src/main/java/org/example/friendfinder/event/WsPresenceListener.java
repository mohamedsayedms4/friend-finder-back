package org.example.friendfinder.event;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.repository.UserRepository;
import org.example.friendfinder.service.PresenceService;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WsPresenceListener {

    private final PresenceService presenceService;
    private final UserRepository userRepository;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        Authentication auth = (Authentication) event.getUser();
        if (auth == null) return;

        String email = auth.getName();

        userRepository.findByEmail(email).ifPresent(user -> {
            presenceService.markOnline(user.getId());
        });
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Authentication auth = (Authentication) event.getUser();
        if (auth == null) return;

        String email = auth.getName();

        userRepository.findByEmail(email).ifPresent(user -> {
            presenceService.markOffline(user.getId());
        });
    }
}
