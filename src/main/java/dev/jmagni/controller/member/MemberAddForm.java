package dev.jmagni.controller.member;

import dev.jmagni.model.team.Team;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class MemberAddForm {

    @NotEmpty
    @NotBlank
    private String username;

    @NotNull
    @Range(min = 1, max = 1000)
    private Integer age;

    @NotEmpty
    @NotNull
    @NotBlank
    private String loginId;

    @NotEmpty
    @NotNull
    @NotBlank
    private String password;

    @NotEmpty
    @NotNull
    @NotBlank
    private String role;

    @NotNull
    private Team team;

}
