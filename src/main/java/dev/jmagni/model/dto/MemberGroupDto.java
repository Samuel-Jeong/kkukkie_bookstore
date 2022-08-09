package dev.jmagni.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberGroupDto {

    private Long memberId;
    private String username;
    private int age;

    private Long groupId;
    private String groupName;

    @QueryProjection
    public MemberGroupDto(Long memberId, String username, int age, Long groupId, String groupName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.groupId = groupId;
        this.groupName = groupName;
    }

}
