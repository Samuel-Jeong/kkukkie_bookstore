package dev.kkukkie_bookstore.service.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class AdminAuthService {

    private final HashSet<String> authCodeSet;
    private final ReentrantLock authCodeLock;

    public AdminAuthService() {
        authCodeSet = new HashSet<>();
        authCodeLock = new ReentrantLock();
    }

    public void addAuthCode(String authCode) {
        authCodeLock.lock();
        try {
            authCodeSet.add(authCode);
        } catch (Exception e) {
            log.warn("AdminAuthService.addAuthCode.Exception", e);
        } finally {
            authCodeLock.unlock();
        }
    }

    public void removeAuthCode(String authCode) {
        authCodeLock.lock();
        try {
            authCodeSet.remove(authCode);
        } catch (Exception e) {
            log.warn("AdminAuthService.removeAuthCode.Exception", e);
        } finally {
            authCodeLock.unlock();
        }
    }
    public boolean isContains(String authCode) {
        authCodeLock.lock();
        try {
            return authCodeSet.contains(authCode);
        } catch (Exception e) {
            log.warn("AdminAuthService.isContains.Exception", e);
            return false;
        } finally {
            authCodeLock.unlock();
        }
    }

}
