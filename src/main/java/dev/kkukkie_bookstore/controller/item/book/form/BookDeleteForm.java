package dev.kkukkie_bookstore.controller.item.book.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BookDeleteForm {

    @NotEmpty
    @NotNull
    private String isbn;

}
