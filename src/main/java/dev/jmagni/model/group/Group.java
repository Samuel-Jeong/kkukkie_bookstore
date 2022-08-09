package dev.jmagni.model.group;

import dev.jmagni.model.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {

    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "group")
    @ToString.Exclude
    private List<Member> members = new ArrayList<>();

    @Transient
    private final ReentrantLock memberLock = new ReentrantLock();

    public Group(String name) {
        this.name = name;
    }

    public void addMember(Member member) {
        if (member == null) { return; }

        memberLock.lock();
        try {
            if (!members.contains(member)) {
                members.add(member);
            }
        } catch (Exception e) {
            //ignore
        } finally {
            memberLock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
