package dev.kkukkie_bookstore.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {

    //@GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "NO SESSION";
        }

        //세션 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(
                        name -> log.debug("session name={}, value={}", name, session.getAttribute(name)
                        )
                );

        log.debug("sessionId={}", session.getId());
        log.debug("getMaxInactiveInterval={}", session.getMaxInactiveInterval());
        log.debug("creationTime={}", new Date(session.getCreationTime()));
        log.debug("lastAccessedTime={}", new Date(session.getLastAccessedTime()));
        log.debug("isNew={}", session.isNew());

        return "PRINT SESSION";

    }
}
