package dev.jmagni.controller.team;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TeamSaveForm {

    @NotBlank
    private String name;

}
