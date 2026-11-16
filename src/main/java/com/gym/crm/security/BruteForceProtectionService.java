package com.gym.crm.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BruteForceProtectionService {

    public static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MS = 5 * 60 * 1000; // 5 minutes in milliseconds

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> blockTimeCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockTimeCache.remove(username);
    }

    public void loginFailed(String username) {
        if (isBlocked(username)) {
            return;
        }

        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            blockTimeCache.put(username, System.currentTimeMillis());
        }
    }

    public boolean isBlocked(String username) {
        Long blockTimestamp = blockTimeCache.get(username);
        if (blockTimestamp == null) {
            return false;
        }

        long elapsedTime = System.currentTimeMillis() - blockTimestamp;
        if (elapsedTime > BLOCK_DURATION_MS) {
            // 5 minutes have passed, unblock the user
            blockTimeCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }

        return true;
    }
}