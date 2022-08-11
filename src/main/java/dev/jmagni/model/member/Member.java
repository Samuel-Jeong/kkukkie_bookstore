package dev.jmagni.model.member;

import dev.jmagni.model.base.BaseEntity;
import dev.jmagni.model.role.MemberRole;
import dev.jmagni.model.team.Team;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String loginId;
    private String password;

    private String username;
    private int age;

    private MemberRole role = MemberRole.NORMAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @ToString.Exclude
    private Team team;

    public Member(String username) {
        this(username, 0);
    }

    public Member(String loginId, String password, String username) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.age = 0;
        this.team = null;
    }

    public Member(String username, int age) {
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;

        if (team != null) {
            changeGroup(team);
        }
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        setCreateDateTime(now);
        setLastModifiedDateTime(now);

        setCreatedBy(username);
        setLastModifiedBy(username);
    }

    @PreUpdate
    public void preUpdate() {
        setLastModifiedDateTime(LocalDateTime.now());
        setLastModifiedBy(username);
    }

    private void changeGroup(Team team) {
        this.team = team;
        team.addMember(this);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", loginId='" + loginId + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
