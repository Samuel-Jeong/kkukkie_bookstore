package dev.jmagni;

import dev.jmagni.model.group.Group;
import dev.jmagni.model.member.Member;
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
            Group groupA = new Group("groupA");
            Group groupB = new Group("groupB");
            entityManager.persist(groupA);
            entityManager.persist(groupB);

            for (int i = 0; i < 100; i++) {
                Group selectedGroup = (i % 2 == 0) ? groupA : groupB;
                Member member = new Member("member" + i, i, selectedGroup);
                entityManager.persist(member);
            }

            entityManager.flush();
            entityManager.clear();
        }

    }

}
