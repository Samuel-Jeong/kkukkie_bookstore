package dev.kkukkie_bookstore;

import dev.kkukkie_bookstore.model.file.image.ImageFile;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.member.role.MemberRole;
import dev.kkukkie_bookstore.model.team.Team;
import dev.kkukkie_bookstore.security.PasswordService;
import dev.kkukkie_bookstore.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.File;
import java.util.Collection;

@Slf4j
@Profile({"dev", "prod", "server"})
@Component
@RequiredArgsConstructor
public class InitAdminData {

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

        private static final String DEFAULT_PROFILE_IMG_NAME = "cookie1.jpeg";

        private final EntityManager entityManager;

        private final Environment environment;

        private final PasswordService passwordService;

        public InitMemberService(EntityManager entityManager, Environment environment, PasswordService passwordService) {
            this.entityManager = entityManager;
            this.environment = environment;
            this.passwordService = passwordService;
        }

        public Collection<Team> findAllTeamsByName(String name) {
            Query query = entityManager.createQuery("SELECT t FROM Team t WHERE t.name=:name");
            query.setParameter("name", name);
            return (Collection<Team>) query.getResultList();
        }

        public Collection<Member> findAllMembersByName(String username) {
            Query query = entityManager.createQuery("SELECT m FROM Member m WHERE m.username=:username");
            query.setParameter("username", username);
            return (Collection<Member>) query.getResultList();
        }

        @Transactional
        public void init() {
            // admin team 중복 확인
            Team adminTeam;
            Collection<Team> teams = findAllTeamsByName(SUPER_TEAM_NAME);
            if (teams.isEmpty()) {
                adminTeam = new Team(SUPER_TEAM_NAME);
                entityManager.persist(adminTeam);
            } else {
                adminTeam = teams.stream().findFirst().orElse(null);
            }

            // admin member 중복 확인
            Collection<Member> members = findAllMembersByName("admin");
            if (!members.isEmpty()) {
                Member member = members.stream().findFirst().orElse(null);
                if (member != null) {
                    member.setLoginId("admin");
                    member.setPassword(passwordService.encryptPassword("admin.123", member));
                    member.setRole(MemberRole.ADMIN);
                    entityManager.persist(member);
                }
            } else {
                createAdmin(adminTeam);
            }
            //

            entityManager.flush();
            entityManager.clear();
        }

        private void createAdmin(Team adminTeam) {
            Member admin = new Member("admin", 999, adminTeam);
            admin.setLoginId("admin");
            admin.setPassword(passwordService.encryptPassword("admin.123", admin));
            admin.setRole(MemberRole.ADMIN);

            String profileImgBasePath = environment.getProperty("spring.servlet.multipart.location");
            File file = new File(profileImgBasePath, DEFAULT_PROFILE_IMG_NAME);
            ImageFile profileImgFile = new ImageFile(
                    DEFAULT_PROFILE_IMG_NAME,
                    DEFAULT_PROFILE_IMG_NAME,
                    MemberService.PROFILE_IMG_URI,
                    profileImgBasePath,
                    file.length()
            );
            admin.setProfileImgFile(profileImgFile);

            entityManager.persist(admin);
        }

    }

}
