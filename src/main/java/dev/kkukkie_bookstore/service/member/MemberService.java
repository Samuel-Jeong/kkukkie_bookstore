package dev.kkukkie_bookstore.service.member;

import dev.kkukkie_bookstore.controller.member.form.MemberAddForm;
import dev.kkukkie_bookstore.controller.member.form.MemberRegisterForm;
import dev.kkukkie_bookstore.controller.member.form.MemberUpdateForm;
import dev.kkukkie_bookstore.model.file.image.ImageExtensionException;
import dev.kkukkie_bookstore.model.file.image.ImageFile;
import dev.kkukkie_bookstore.model.file.image.ImageSizeException;
import dev.kkukkie_bookstore.model.item.book.Book;
import dev.kkukkie_bookstore.model.member.Member;
import dev.kkukkie_bookstore.model.team.Team;
import dev.kkukkie_bookstore.repository.item.BookRepository;
import dev.kkukkie_bookstore.repository.member.MemberRepository;
import dev.kkukkie_bookstore.security.PasswordService;
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

    private final PasswordService passwordService;

    public MemberService(Environment environment,
                         BookRepository bookRepository, MemberRepository memberRepository,
                         PasswordService passwordService) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;

        this.profileImgBasePath = environment.getProperty("spring.servlet.multipart.location");
        this.profileMaxFileSize = FileManager.getSizeFromUnit(environment.getProperty("spring.servlet.multipart.maxFileSize"));

        this.passwordService = passwordService;

        allowedExtensions.add("jpeg");
        allowedExtensions.add("jpg");
    }

    public Member findById(long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public void delete(Member member) {
        memberRepository.delete(member);
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

    public void saveProfileImage(Member member, MultipartFile profileImgFile, String prevProfileImgId)
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
        String newImgName = prevProfileImgId;
        if (newImgName == null || newImgName.isEmpty()) {
            newImgName = UUID.randomUUID() + "." + extension;
        }

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
                    username,
                    Integer.parseInt(age),
                    team
            );
            member.setPassword(passwordService.encryptPassword(password, member));

            // 프로파일 이미지 선택은 Option
            if (profileImgFile != null) {
                saveProfileImage(member, profileImgFile, null);
            }
        } catch (Exception e) {
            if (e.getClass().equals(ImageSizeException.class)) {
                bindingResult.reject("ImageSizeException", new Object[]{profileImgFile.getSize()}, "프로필 이미지 크기가 너무 큽니다.");
            } else if (e.getClass().equals(ImageExtensionException.class)) {
                bindingResult.reject("ImageExtensionException", new Object[]{allowedExtensions.toArray()}, "등록할 프로필 이미지 파일의 확장자는 지원하지 않습니다.");
            } else {
                bindingResult.reject("InputException", new Object[]{}, "입력값 확인이 필요합니다.");
            }
        }
        return member;
    }

    public void updateMember(MemberUpdateForm memberUpdateForm, BindingResult bindingResult, Member member) {
        if (member != null) {
            try {
                member.setUsername(memberUpdateForm.getUsername());
                member.setAge(Integer.parseInt(memberUpdateForm.getAge()));
                member.setPassword(passwordService.encryptPassword(memberUpdateForm.getPassword(), member));
                member.setRole(memberUpdateForm.getRole());
                member.setTeam(memberUpdateForm.getTeam());

                // 프로파일 이미지 선택은 Option
                MultipartFile profileImgFile = memberUpdateForm.getProfileImgFile();
                if ((profileImgFile != null)
                        && (profileImgFile.getOriginalFilename() != null && !profileImgFile.getOriginalFilename().isEmpty())) {
                    // 기존에 프로파일 이미지가 있으면 해당 파일 삭제
                    String prevProfileImgId = deletePrevProfileImage(member);

                    // 새로운 프로파일 이미지 저장
                    saveProfileImage(member, profileImgFile, prevProfileImgId);
                }
            } catch (Exception e) {
                if (e.getClass().equals(ImageSizeException.class)) {
                    bindingResult.reject("ImageSizeException", new Object[]{memberUpdateForm.getProfileImgFile().getSize()}, "프로필 이미지 크기가 너무 큽니다.");
                } else if (e.getClass().equals(ImageExtensionException.class)) {
                    bindingResult.reject("ImageExtensionException", new Object[]{allowedExtensions.toArray()}, "등록할 프로필 이미지 파일의 확장자는 지원하지 않습니다.");
                } else {
                    bindingResult.reject("InputException", new Object[]{}, "입력값 확인이 필요합니다.");
                }
            }
        }
    }

    public String deletePrevProfileImage(Member member) {
        ImageFile prevProfileImgFile = member.getProfileImgFile();
        if (prevProfileImgFile != null) {
            // 삭제할 파일 이름은 UUID (저장할 때 랜덤으로 생성된 UUID 가 파일 이름)
            FileManager.deleteFile(
                    new File(FileManager.concatFilePath(
                            prevProfileImgFile.getLocalBasePath(), prevProfileImgFile.getId()
                    ))
            );
            return prevProfileImgFile.getId();
        }
        return null;
    }

    public void checkDuplicateMemberAtRegisterByLoginId(MemberRegisterForm memberRegisterForm,
                                                         BindingResult bindingResult) {
        memberRepository.findByLoginId(
                memberRegisterForm.getLoginId()).ifPresent(
                foundMember ->
                        bindingResult.reject(
                                "MemberAlreadyExistByLoginId", new Object[]{foundMember.getId()}, "로그인 ID 가 이미 존재합니다."
                        )
        );
    }

    public void checkDuplicateMemberAtAddByLoginId(MemberAddForm memberAddForm,
                                                    BindingResult bindingResult) {
        memberRepository.findByLoginId(memberAddForm.getLoginId())
                .ifPresent(
                        foundMember ->
                                bindingResult.reject(
                                        "MemberAlreadyExist",
                                        new Object[]{foundMember.getId()},
                                        "로그인 ID 가 이미 존재합니다."
                                )
                );
    }

}
