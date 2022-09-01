package dev.kkukkie_bookstore.model.member;

import dev.kkukkie_bookstore.model.base.BaseEntity;
import dev.kkukkie_bookstore.model.item.base.Item;
import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.model.member.role.MemberRole;
import dev.kkukkie_bookstore.model.team.Team;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String role = MemberRole.NORMAL;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    @ToString.Exclude
    private Team team;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Book> books = new ArrayList<>();

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

    public Member(String loginId, String password, String username, int age, Team team) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.age = age;
        this.team = team;
    }

    public Member(String username, int age) {
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;

        if (team != null) {
            changeTeam(team);
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

    private void changeTeam(Team team) {
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
                ", role=" + role +
                '}';
    }
}
