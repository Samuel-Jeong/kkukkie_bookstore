package dev.kkukkie_bookstore.service.member;

import dev.kkukkie_bookstore.controller.member.form.MemberUpdateForm;
import dev.kkukkie_bookstore.model.file.image.ImageExtensionException;
import dev.kkukkie_bookstore.model.file.image.ImageFile;
import dev.kkukkie_bookstore.model.file.image.ImageSizeException;
import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.team.Team;
import dev.kkukkie_bookstore.repository.item.BookRepository;
import dev.kkukkie_bookstore.repository.member.MemberRepository;
import dev.kkukkie_bookstore.util.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.management.RuntimeErrorException;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MemberService {

    private final List<String> allowedExtensions = new ArrayList<>();

    private final String profileImgBasePath;
    private final int profileMaxFileSize;

    public static final String PROFILE_IMG_URI = "images";

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public MemberService(Environment environment,
                         BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;

        this.profileImgBasePath = environment.getProperty("spring.servlet.multipart.location");
        this.profileMaxFileSize = FileManager.getSizeFromUnit(environment.getProperty("spring.servlet.multipart.maxFileSize"));

        allowedExtensions.add("jpeg");
        allowedExtensions.add("jpg");
    }

    @Transactional
    public boolean addBookToList(long memberId, String bookId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            Book book = bookRepository.findById(bookId).orElse(null);
            if (book == null) { return false; }

            Book foundBook = findBookByIdFromMember(member, bookId);
            if (foundBook != null) { return false; }

            Integer count = book.getCount();
            if (count > 0) {
                book.setCount(count - 1);
                bookRepository.save(book);

                member.getBooks().add(book);
                memberRepository.save(member);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Transactional
    public void removeBookFromList(long memberId, String bookId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null) {
            Book book = bookRepository.findById(bookId).orElse(null);
            if (book == null) { return; }

            Integer count = book.getCount();
            book.setCount(count + 1);
            bookRepository.save(book);

            member.getBooks().remove(book);
            memberRepository.save(member);
        }
    }

    public Book findBookByIdFromMember(Member member, String bookId) {
        return member.getBooks().stream().filter(
                item1 -> item1.getId().equals(bookId)
        ).findAny().orElse(null);
    }

    public void saveProfileImage(Member member, MultipartFile profileImgFile)
            throws RuntimeErrorException, IOException {
        // 파일 최대 저장 크기 확인
        if (profileImgFile.getSize() >= profileMaxFileSize) {
            throw new ImageSizeException();
        }

        String fileName = profileImgFile.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) { return; }

        String extension = FilenameUtils.getExtension(fileName);
        if (extension.isEmpty() || !allowedExtensions.contains(extension)) {
            throw new ImageExtensionException();
        }

        // 파일 이름 그대로 저장하지 않고 UUID 로 변경해서 저장
        String newImgName = UUID.randomUUID() + "." + extension;

        // 저장
        File file = new File(profileImgBasePath, newImgName);
        profileImgFile.transferTo(file);

        // Resize
        resizeImage(file, extension);

        member.setProfileImgFile(
                new ImageFile(
                        newImgName, // UUID 를 id 로 설정해서 관리
                        fileName, // 실제 파일 이름
                        PROFILE_IMG_URI, // 클라이언트가 요청할 REST URI
                        profileImgBasePath, // 실제 파일 저장 경로 (파일 이름 제외)
                        profileImgFile.getSize() // 실제 파일 크기
                )
        );
    }

    private void resizeImage(File file, String extension) throws IOException {
        Image originalImage = ImageIO.read(file);
        int originWidth = originalImage.getWidth(null);
        int originHeight = originalImage.getHeight(null);
        int newWidth = 300;
        if (originWidth > newWidth) {
            // Reduce width & height
            int newHeight = (originHeight * newWidth) / originWidth;
            BufferedImage newImage = new BufferedImage(
                    newWidth, newHeight,
                    BufferedImage.TYPE_INT_RGB
            );

            // Redraw
            Image resizeImage = originalImage
                    .getScaledInstance(
                            newWidth, newHeight,
                            Image.SCALE_SMOOTH
                    );
            Graphics graphics = newImage.getGraphics();
            graphics.drawImage(resizeImage, 0, 0, null);
            graphics.dispose();

            // Save
            ImageIO.write(newImage, extension, file);
        }
    }

    public Member saveMember(String loginId, String password,
                              String username, String age,
                              Team team,
                              MultipartFile profileImgFile,
                              BindingResult bindingResult) {
        Member member = null;
        try {
            member = new Member(
                    loginId,
                    password,
                    username,
                    Integer.parseInt(age),
                    team
            );

            // 프로파일 이미지 선택은 Option
            if (profileImgFile != null) {
                saveProfileImage(member, profileImgFile);
            }
        } catch (Exception e) {
            if (e.getClass().equals(ImageSizeException.class)) {
                bindingResult.reject("ImageSizeException", new Object[]{profileImgFile.getSize()}, null);
            } else if (e.getClass().equals(ImageExtensionException.class)) {
                bindingResult.reject("ImageExtensionException", new Object[]{allowedExtensions.toArray()}, null);
            } else {
                bindingResult.reject("InputException", new Object[]{}, null);
            }
        }
        return member;
    }

    public void updateMember(MemberUpdateForm memberUpdateForm, BindingResult bindingResult, Member member) {
        if (member != null) {
            try {
                member.setUsername(memberUpdateForm.getUsername());
                member.setAge(Integer.parseInt(memberUpdateForm.getAge()));
                member.setPassword(memberUpdateForm.getPassword());
                member.setRole(memberUpdateForm.getRole());
                member.setTeam(memberUpdateForm.getTeam());

                // 프로파일 이미지 선택은 Option
                MultipartFile profileImgFile = memberUpdateForm.getProfileImgFile();
                if ((profileImgFile != null)
                        && (!profileImgFile.getName().isEmpty())) {
                    // 기존에 프로파일 이미지가 있으면 해당 파일 삭제
                    deletePrevProfileImage(member);

                    // 새로운 프로파일 이미지 저장
                    saveProfileImage(member, profileImgFile);
                }
            } catch (Exception e) {
                if (e.getClass().equals(ImageSizeException.class)) {
                    bindingResult.reject("ImageSizeException", new Object[]{memberUpdateForm.getProfileImgFile().getSize()}, null);
                } else if (e.getClass().equals(ImageExtensionException.class)) {
                    bindingResult.reject("ImageExtensionException", new Object[]{allowedExtensions.toArray()}, null);
                } else {
                    bindingResult.reject("InputException", new Object[]{}, null);
                }
            }
        }
    }

    public void deletePrevProfileImage(Member member) {
        ImageFile prevProfileImgFile = member.getProfileImgFile();
        if (prevProfileImgFile != null) {
            // 삭제할 파일 이름은 UUID (저장할 때 랜덤으로 생성된 UUID 가 파일 이름)
            FileManager.deleteFile(
                    new File(FileManager.concatFilePath(
                            prevProfileImgFile.getLocalBasePath(), prevProfileImgFile.getId()
                    ))
            );
        }
    }

}
