package dev.jmagni;

import dev.jmagni.model.member.Member;
import dev.jmagni.model.role.MemberRole;
import dev.jmagni.model.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    public static final String SUPER_TEAM_NAME = "ADMIN";

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
            Team adminTeam = new Team(SUPER_TEAM_NAME);
            entityManager.persist(adminTeam);

            Member admin = new Member("admin", 999, adminTeam);
            admin.setLoginId("admin");
            admin.setPassword("admin.123");
            admin.setRole(MemberRole.ADMIN);
            entityManager.persist(admin);

            entityManager.flush();
            entityManager.clear();
        }

    }

}
