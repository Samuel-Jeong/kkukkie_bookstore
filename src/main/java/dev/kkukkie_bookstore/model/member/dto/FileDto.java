package dev.kkukkie_bookstore.model.member.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDto {

    private String originalFileName;
    private String contentType;

    public FileDto(String originalFileName, String contentType) {
        this.originalFileName = originalFileName;
        this.contentType = contentType;
    }

}
