package dev.jmagni.controller.member;

import dev.jmagni.model.role.MemberRole;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MemberUpdateForm {

    @NotNull
    private Long id;
    @NotBlank
    private String username;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer age;

    @NotNull
    @NotBlank
    private String loginId;

    @NotNull
    @NotBlank
    private String password;

    @NotNull
    private MemberRole role;

}
