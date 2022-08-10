package dev.jmagni.model.condition;

import lombok.Data;

@Data
public class MemberSearchCondition {

    // 회원명, 그룹명, 나이(ageGoe, ageLoe)

    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;

}
