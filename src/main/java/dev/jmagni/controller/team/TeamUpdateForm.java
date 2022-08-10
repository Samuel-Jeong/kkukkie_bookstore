package dev.jmagni.controller.team;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TeamUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

}
