package dev.kkukkie_bookstore.controller.member.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class MemberDeleteForm {

    @NotEmpty
    @NotNull
    private String loginId;

}
