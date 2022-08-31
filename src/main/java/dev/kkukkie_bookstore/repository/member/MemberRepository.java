package dev.kkukkie_bookstore.repository.member;

import dev.kkukkie_bookstore.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository
        extends JpaRepository<Member, Long>,
            MemberRepositoryCustom // 커스텀한 JPA 기능 사용
{

    // select m from Member m where m.username = :username
    List<Member> findByUsername(String username);

    Optional<Member> findById(Long id);

    List<Member> findAll();

    Optional<Member> findByLoginId(String loginId);


}
