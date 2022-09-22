package dev.kkukkie_bookstore.controller.file.image;

import dev.kkukkie_bookstore.util.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@RestController
public class ImageController {

    private final Environment environment;

    public ImageController(Environment environment) {
        this.environment = environment;
    }

    // http://127.0.0.1:8080/images/20cb9808-e248-406a-871b-01a0f63b4d9b.jpeg
    @GetMapping(value = "**/images/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProfileImage(@PathVariable("name") String name) throws IOException {
        String filePath = FileManager.concatFilePath(environment.getProperty("spring.servlet.multipart.location"), name);
        InputStream imageStream = new FileInputStream(
                Objects.requireNonNull(filePath)
        );

        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        //log.info("@@@ [{}] imageByteArray.length: {}", filePath, imageByteArray.length);
        if (imageByteArray.length > 0) {
            return new ResponseEntity<>(imageByteArray, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
