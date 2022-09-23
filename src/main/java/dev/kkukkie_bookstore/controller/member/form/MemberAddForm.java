package dev.kkukkie_bookstore.controller.member.form;

import dev.kkukkie_bookstore.model.team.Team;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class MemberAddForm {

    @NotNull
    private MultipartFile profileImgFile;

    @NotEmpty
    @NotNull
    @Pattern(regexp = "[A-Za-z0-9]{1,10}", message = "ID 최대 길이는 10입니다. 영문자와 숫자만 입력 가능합니다.")
    private String loginId;

    @NotEmpty
    @NotNull
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{6,20}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 6자 ~ 20자의 비밀번호여야 합니다.")
    private String password;

    @NotEmpty
    @Pattern(regexp = "[가-힣A-Za-z0-9]{1,10}", message = "이름 최대 길이는 10입니다. 한글, 영문자와 숫자만 입력 가능합니다.")
    private String username;

    @Pattern(regexp = "[0-9]{1,3}", message = "1~3자리의 숫자만 입력 가능합니다")
    @NotNull
    private String age;

    @NotEmpty
    @NotNull
    private String role;

    @NotNull
    private Team team;

}
