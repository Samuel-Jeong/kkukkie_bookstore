package dev.jmagni.controller.member.form;

import dev.jmagni.model.team.Team;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class MemberAddForm {

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
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 6자 ~ 20자의 비밀번호여야 합니다.")
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
