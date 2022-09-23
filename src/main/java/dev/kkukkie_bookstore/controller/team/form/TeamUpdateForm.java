package dev.kkukkie_bookstore.controller.team.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class TeamUpdateForm {

    private Long id;

    @Pattern(regexp = "[가-힣A-Za-z0-9]{1,20}", message = "이름 최대 길이는 20입니다. 한글, 영문자와 숫자만 입력 가능합니다.")
    @NotEmpty
    @NotNull
    private String name;

}
