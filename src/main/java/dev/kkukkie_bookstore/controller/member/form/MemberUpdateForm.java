package dev.kkukkie_bookstore.controller.member.form;

import dev.kkukkie_bookstore.model.team.Team;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class MemberUpdateForm {

    @NotEmpty
    @NotBlank
    private String username;

    @NumberFormat
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
