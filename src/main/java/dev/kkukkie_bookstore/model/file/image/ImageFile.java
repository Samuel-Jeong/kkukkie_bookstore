package dev.kkukkie_bookstore.model.file.image;


import dev.kkukkie_bookstore.util.FileManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Slf4j
@Data
@Embeddable
public class ImageFile {

    @Column(name = "profile_img_file_id")
    private final String id;
    @Column(name = "profile_img_file_name")
    private final String name;
    @Column(name = "profile_img_file_uri_base_path")
    private final String uriBasePath;
    @Column(name = "profile_img_file_local_base_path")
    private final String localBasePath;
    @Column(name = "profile_img_file_size")
    private final long size;

    public ImageFile(String id, String name, String uriBasePath, String localBasePath, long size) {
        this.id = id;
        this.name = name;
        this.uriBasePath = uriBasePath;
        this.localBasePath = localBasePath;
        this.size = size;
    }

    public ImageFile() {
        this.id = UUID.randomUUID().toString();
        this.name = "";
        this.uriBasePath = "";
        this.localBasePath = "";
        this.size = 0;
    }

    public String getUriFullPath() {
        return FileManager.concatFilePath(uriBasePath, id); // 실제 파일 이름을 외부에 노출하지 않음
    }

    public String getLocalFullPath() {
        return FileManager.concatFilePath(localBasePath, id); // 실제 파일 이름을 외부에 노출하지 않음
    }

}
