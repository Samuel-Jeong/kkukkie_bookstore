package dev.kkukkie_bookstore.model.board.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BoardAddDto {

    @NotEmpty
    @NotNull
    private String title;

    private String content;

}
