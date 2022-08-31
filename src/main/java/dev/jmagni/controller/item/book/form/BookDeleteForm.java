package dev.jmagni.controller.item.book.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BookDeleteForm {

    @NotEmpty
    @NotNull
    @NotBlank
    private String isbn;

}
