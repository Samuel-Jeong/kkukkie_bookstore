package dev.kkukkie_bookstore;

import dev.kkukkie_bookstore.model.file.image.ImageFile;
import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.member.role.MemberRole;
import dev.kkukkie_bookstore.model.team.Team;
import dev.kkukkie_bookstore.service.member.MemberService;
import dev.kkukkie_bookstore.util.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.management.RuntimeErrorException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class InitTestData {

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

        private static final String DEFAULT_PROFILE_IMG_NAME = "cookie1.jpeg";

        private final EntityManager entityManager;

        private final Environment environment;

        public InitMemberService(EntityManager entityManager, Environment environment) {
            this.entityManager = entityManager;
            this.environment = environment;
        }

        @Transactional
        public void init() {
            // Get the default profile image
            String profileImgBasePath = environment.getProperty("spring.servlet.multipart.location");
            File file = new File(profileImgBasePath, DEFAULT_PROFILE_IMG_NAME);
            //

            //
            Team adminTeam = new Team(SUPER_TEAM_NAME);
            entityManager.persist(adminTeam);

            // 테스트용 프로파일 이미지 생성
            String imageId = DEFAULT_PROFILE_IMG_NAME;

            // 이미지 이름 UUID 로 테스트
            /*String extension = FilenameUtils.getExtension(DEFAULT_PROFILE_IMG_NAME);
            String imageId = UUID.randomUUID() + "." + extension;
            try {
                FileUtil.copyFile(
                        new File(Objects.requireNonNull(
                                FileManager.concatFilePath(profileImgBasePath, DEFAULT_PROFILE_IMG_NAME))
                        ),
                        new File(Objects.requireNonNull(
                                FileManager.concatFilePath(profileImgBasePath, imageId))
                        )
                );
            } catch (IOException e) {
                throw new RuntimeErrorException(new Error(e));
            }*/

            ImageFile profileImgFile = new ImageFile(
                    imageId,
                    DEFAULT_PROFILE_IMG_NAME,
                    MemberService.PROFILE_IMG_URI,
                    profileImgBasePath,
                    file.length()
            );

            Member admin = new Member("admin", 999, adminTeam);
            admin.setLoginId("admin");
            admin.setPassword("admin.123");
            admin.setRole(MemberRole.ADMIN);
            admin.setProfileImgFile(profileImgFile);
            entityManager.persist(admin);
            //

            //
            Team guestTeam = new Team(GUEST_TEAM_NAME);
            entityManager.persist(guestTeam);

            Member guest1 = new Member("guest1", 100, guestTeam);
            guest1.setLoginId("guest1");
            guest1.setPassword("guest1");
            guest1.setRole(MemberRole.NORMAL);
            guest1.setProfileImgFile(profileImgFile);
            entityManager.persist(guest1);

            Member guest2 = new Member("guest2", 100, guestTeam);
            guest2.setLoginId("guest2");
            guest2.setPassword("guest2");
            guest2.setRole(MemberRole.NORMAL);
            guest2.setProfileImgFile(profileImgFile);
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
