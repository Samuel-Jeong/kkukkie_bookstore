package dev.kkukkie_bookstore.controller.item.book.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class BookUpdateForm {

    @Pattern(regexp = "[0-9]{1,7}", message = "1~7자리의 숫자만 입력 가능합니다")
    @NotNull
    private String price;

    @NotNull
    @Pattern(regexp = "[0-9]{1,6}", message = "1~6자리의 숫자만 입력 가능합니다")
    private String count;

}
