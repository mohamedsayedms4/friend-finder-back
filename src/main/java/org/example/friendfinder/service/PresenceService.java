package org.example.friendfinder.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final ConcurrentHashMap<Long, Instant> online = new ConcurrentHashMap<>();

    public void markOnline(Long userId) {
        online.put(userId, Instant.now());
    }

    public void markOffline(Long userId) {
        online.remove(userId);
    }

    public boolean isOnline(Long userId) {
        return online.containsKey(userId);
    }

    public Instant lastPing(Long userId) {
        return online.get(userId);
    }
}
