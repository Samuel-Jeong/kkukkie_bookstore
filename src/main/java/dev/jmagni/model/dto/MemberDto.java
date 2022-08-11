package dev.jmagni.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import dev.jmagni.model.role.MemberRole;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {

    private long id;
    private String username;
    private int age;

    private String loginId;

    private MemberRole role;

    @QueryProjection
    public MemberDto(long id, String username, int age, String loginId, MemberRole role) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.loginId = loginId;
        this.role = role;
    }

}
