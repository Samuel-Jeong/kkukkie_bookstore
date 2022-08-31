package dev.kkukkie_bookstore.controller.team.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TeamUpdateForm {

    @NotNull
    private Long id;

    @NotEmpty
    @NotNull
    @NotBlank
    private String name;

}