package dev.kkukkie_bookstore.service.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AdminAuthService {

    // Key: authCode / Value: createdTime
    private final ConcurrentHashMap<String, Long> authCodeMap;

    public AdminAuthService(Environment environment) {
        authCodeMap = new ConcurrentHashMap<>();

        // Remove old key
        String keyTimeoutString = environment.getProperty("kakao.keyTimeout");
        if (keyTimeoutString != null) {
            long keyTimeout = Long.parseLong(keyTimeoutString);

            ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10);
            scheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        long currentTime = System.currentTimeMillis();
                        ConcurrentHashMap<String, Long> clonedMap = clone();
                        for (Map.Entry<String, Long> entry : clonedMap.entrySet()) {
                            if (entry == null) { continue; }

                            String authCode = entry.getKey();
                            Long createdTime = entry.getValue();
                            if ((currentTime - createdTime) >= keyTimeout) {
                                removeAuthCode(authCode);
                                if (!isContains(authCode)) {
                                    log.info("Old auth code is deleted. ({})", authCode);
                                }
                            }
                        }
                    },
                    0, 2, TimeUnit.SECONDS
            );
        }
    }

    public void addAuthCode(String authCode) {
        authCodeMap.putIfAbsent(authCode, System.currentTimeMillis());
        log.info("New auth code is added. ({})", authCode);
    }

    public void removeAuthCode(String authCode) {
        authCodeMap.remove(authCode);
        log.info("Auth code is deleted ({})", authCode);
    }

    public boolean isContains(String authCode) {
        return authCodeMap.containsKey(authCode);
    }

    public ConcurrentHashMap<String, Long> clone() {
        return new ConcurrentHashMap<>(authCodeMap);
    }

}
