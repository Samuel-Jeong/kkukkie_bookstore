package dev.kkukkie_bookstore;

import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.member.role.MemberRole;
import dev.kkukkie_bookstore.model.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.UUID;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitData {

    public static final String SUPER_TEAM_NAME = "ADMIN";
    public static final String GUEST_TEAM_NAME = "GUEST";

    private final InitMemberService initMemberService;

    /**
     * PostConstruct 로직과 Transactional 로직은 분리되어야 한다.
     */
    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {

        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        public void init() {
            //
            Team adminTeam = new Team(SUPER_TEAM_NAME);
            entityManager.persist(adminTeam);

            Member admin = new Member("admin", 999, adminTeam);
            admin.setLoginId("admin");
            admin.setPassword("admin.123");
            admin.setRole(MemberRole.ADMIN);
            entityManager.persist(admin);
            //

            //
            Team guestTeam = new Team(GUEST_TEAM_NAME);
            entityManager.persist(guestTeam);

            Member guest1 = new Member("guest1", 100, guestTeam);
            guest1.setLoginId("guest1");
            guest1.setPassword("guest1");
            guest1.setRole(MemberRole.NORMAL);
            entityManager.persist(guest1);

            Member guest2 = new Member("guest2", 100, guestTeam);
            guest2.setLoginId("guest2");
            guest2.setPassword("guest2");
            guest2.setRole(MemberRole.NORMAL);
            entityManager.persist(guest2);
            //

            //
            Book book1 = new Book(UUID.randomUUID().toString(), "TEST_BOOK_1", 12000, 1, "ISBN_TEST_1");
            entityManager.persist(book1);

            Book book2 = new Book(UUID.randomUUID().toString(), "TEST_BOOK_2", 7500, 1, "ISBN_TEST_2");
            entityManager.persist(book2);
            //

            entityManager.flush();
            entityManager.clear();
        }

    }

}
