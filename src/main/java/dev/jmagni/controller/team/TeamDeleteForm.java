package dev.jmagni.controller.team;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TeamDeleteForm {

    @NotEmpty
    @NotNull
    @NotBlank
    private String name;

}
