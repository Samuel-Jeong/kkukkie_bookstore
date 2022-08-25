package dev.jmagni.controller.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class MemberDeleteForm {

    @NotEmpty
    @NotNull
    @NotBlank
    private String loginId;

}
