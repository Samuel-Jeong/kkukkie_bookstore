package dev.kkukkie_bookstore.controller.item.book.form;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class BookAddForm {

    @NotEmpty
    @NotNull
    /*@Email(message = "이메일 형식이 아닙니다.")*/
    private String name;

    @Pattern(regexp = "[0-9]{1,7}", message = "1~7자리의 숫자만 입력 가능합니다")
    @NotEmpty
    @NotNull
    private String price;

    @Pattern(regexp = "[0-9]{1,6}", message = "1~6자리의 숫자만 입력 가능합니다")
    @NotEmpty
    @NotNull
    private String count;

    @NotEmpty
    @NotNull
    private String isbn;

}
