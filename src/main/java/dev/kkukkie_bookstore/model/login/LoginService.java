package dev.kkukkie_bookstore.model.login;

import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    public boolean validateId(String loginId) {
        return memberRepository.findByLoginId(loginId).isPresent();
    }

    /**
     * @return null 로그인 실패
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

}
