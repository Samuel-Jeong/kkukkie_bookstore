package dev.kkukkie_bookstore.controller.team.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class TeamSaveForm {

    @Pattern(regexp = "[A-Za-z0-9]{1,10}", message = "이름 최대 길이는 10입니다. 영문자와 숫자만 입력 가능합니다.")
    @NotEmpty
    @NotNull
    private String name;

}
