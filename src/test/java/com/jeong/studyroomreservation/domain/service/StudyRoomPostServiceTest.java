package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostResponseDto;
import com.jeong.studyroomreservation.domain.dto.post.studyroom.StudyRoomPostUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.file.File;
import com.jeong.studyroomreservation.domain.entity.file.StudyRoomPostFile;
import com.jeong.studyroomreservation.domain.entity.post.studyroom.StudyRoomPost;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.FileRepository;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.domain.repository.StudyRoomPostRepository;
import com.jeong.studyroomreservation.domain.repository.StudyRoomRepository;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import com.jeong.studyroomreservation.domain.s3.S3ImageUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class StudyRoomPostServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    StudyRoomRepository studyRoomRepository;

    @Autowired
    StudyRoomPostRepository studyRoomPostRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    StudyRoomPostService studyRoomPostService;

    @Autowired
    S3ImageUtil s3ImageUtil;

    @Autowired
    FileRepository fileRepository;

    @Test
    @Transactional
    @DisplayName("StudyRoomPost 생성, 저장, 이미지 o")
    public void createAndSave_WithImages() {
        //given
        StudyRoom studyRoom = createAndSaveStudyRoom();
        Long studyRoomId = studyRoom.getId();
        em.flush();
        em.clear();

        List<MultipartFile> files = createImages();
        StudyRoomPostDto studyRoomPostDto = new StudyRoomPostDto("Title", "Content");

        //when
        StudyRoomPostResponseDto responseDto = studyRoomPostService.createAndSave(studyRoomId, studyRoomPostDto, files);

        //then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo("Title");
        assertThat(responseDto.getContent()).isEqualTo("Content");
        assertThat(responseDto.getImages().size()).isEqualTo(2);

        //clear
        List<String> cleanupImageList = responseDto.getImages();
        for (String image : cleanupImageList) {
            s3ImageUtil.deleteImageFromS3(image);
        }
    }


    @Test
    @Transactional
    @DisplayName("StudyRoomPost 생성, 저장, 이미지 x")
    public void createAndSave_WitouthImages() {
        //given
        StudyRoom studyRoom = createAndSaveStudyRoom();
        Long studyRoomId = studyRoom.getId();
        em.flush();
        em.clear();

        List<MultipartFile> files = new ArrayList<>();
        StudyRoomPostDto studyRoomPostDto = new StudyRoomPostDto("Title", "Content");

        //when
        StudyRoomPostResponseDto responseDto = studyRoomPostService.createAndSave(studyRoomId, studyRoomPostDto, files);

        //then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo("Title");
        assertThat(responseDto.getContent()).isEqualTo("Content");
        assertThat(responseDto.getImages().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("StudyRoomPost 페이징 조회")
    public void getStudyRoomPosts() {
        //given
        StudyRoom studyRoom = createAndSaveStudyRoom();
        createAndSaveStudyRoomPosts(studyRoom);

        Long studyRoomId = studyRoom.getId();

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
        em.flush();
        em.clear();

        //when
        Page<StudyRoomPostResponseDto> page = studyRoomPostService.getStudyRoomPosts(studyRoomId, pageRequest);

        //then
        List<StudyRoomPostResponseDto> content = page.getContent();
        assertThat(content.size()).isEqualTo(2); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(4); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    @Transactional
    @DisplayName("StudyRoomPost 단건 조회,  이미지 o")
    public void getStudyRoomPost_WithImages(){
        //given
        StudyRoom studyRoom = createAndSaveStudyRoom();
        Long studyRoomId = studyRoom.getId();

        StudyRoomPost studyRoomPost = createAndSaveStudyRoomPost(studyRoom);
        Long studyRoomPostId = studyRoomPost.getId();

        List<MultipartFile> files = createImages();
        List<String> cleanupImageList = new ArrayList<>();

        for (MultipartFile file : files) {
            String storeName = s3ImageUtil.upload(file);

            String extention = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(file.getOriginalFilename(), storeName, file.getSize(), extention);
            fileRepository.save(StudyRoomPostFile.createStudyRoomPostFile(fileDto, studyRoomPost));
            cleanupImageList.add(storeName);
        }
        em.flush();
        em.clear();

        //when
        StudyRoomPostResponseDto responseDto = studyRoomPostService.getStudyRoomPost(studyRoomId, studyRoomPostId);

        //then
        assertThat(responseDto.getId()).isEqualTo(studyRoomPostId);
        assertThat(responseDto.getImages().size()).isEqualTo(2);

        //clear
        for (String image : cleanupImageList) {
            s3ImageUtil.deleteImageFromS3(image);
        }
    }

    @Test
    @Transactional
    @DisplayName("StudyRoomPost 단건 조회,  이미지 x")
    public void getStudyRoomPost_WithoutImages(){
        //given
        StudyRoom studyRoom = createAndSaveStudyRoom();
        Long studyRoomId = studyRoom.getId();

        StudyRoomPost studyRoomPost = createAndSaveStudyRoomPost(studyRoom);
        Long studyRoomPostId = studyRoomPost.getId();

        em.flush();
        em.clear();

        //when
        StudyRoomPostResponseDto responseDto = studyRoomPostService.getStudyRoomPost(studyRoomId, studyRoomPostId);

        //then
        assertThat(responseDto.getId()).isEqualTo(studyRoomPostId);
        assertThat(responseDto.getImages().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("StudyRoomPost 수정")
    public void updateStudyRoomPost(){
        //given
        StudyRoom studyRoom = createAndSaveStudyRoom();
        Long studyRoomId = studyRoom.getId();

        StudyRoomPost studyRoomPost = createAndSaveStudyRoomPost(studyRoom);
        Long studyRoomPostId = studyRoomPost.getId();

        List<MultipartFile> files = createImages();
        List<String> deleteFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String storeName = s3ImageUtil.upload(file);

            String extention = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(file.getOriginalFilename(), storeName, file.getSize(), extention);
            fileRepository.save(StudyRoomPostFile.createStudyRoomPostFile(fileDto, studyRoomPost));
            deleteFiles.add(storeName);
        }

        em.flush();
        em.clear();

        //when
        List<MultipartFile> newImages = createImages();
        StudyRoomPostDto studyRoomPostDto = new StudyRoomPostDto("NewTitle", "NewContent");
        StudyRoomPostUpdateResponseDto responseDto =
                studyRoomPostService.updateStudyRoomPost(studyRoomId, studyRoomPostId, studyRoomPostDto, files, deleteFiles);

        //then
        assertThat(responseDto.getId()).isEqualTo(studyRoomPostId);
        assertThat(responseDto.getTitle()).isEqualTo("NewTitle");
        assertThat(responseDto.getContent()).isEqualTo("NewContent");
        assertThat(responseDto.getNewImages().size()).isEqualTo(2);
        assertThat(responseDto.getDeleteImages().size()).isEqualTo(2);

        //clear
        List<String> cleanupImageList = responseDto.getNewImages();
        for (String image : cleanupImageList) {
            s3ImageUtil.deleteImageFromS3(image);
        }
    }

    @Test
    @Transactional
    @DisplayName("StudyRoomPost 삭제")
    public void deleteStudyRoomPost(){
        //given
        StudyRoom studyRoom = createAndSaveStudyRoom();
        Long studyRoomId = studyRoom.getId();

        StudyRoomPost studyRoomPost = createAndSaveStudyRoomPost(studyRoom);
        Long studyRoomPostId = studyRoomPost.getId();

        List<MultipartFile> files = createImages();

        for (MultipartFile file : files) {
            String storeName = s3ImageUtil.upload(file);

            String extention = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(file.getOriginalFilename(), storeName, file.getSize(), extention);
            fileRepository.save(StudyRoomPostFile.createStudyRoomPostFile(fileDto, studyRoomPost));
        }
        em.flush();
        em.clear();

        //when
        studyRoomPostService.deleteStudyRoomPost(studyRoomId, studyRoomPostId);

        em.flush();
        em.clear();

        //then
        Optional<StudyRoomPost> optionalStudyRoomPost = studyRoomPostRepository.findById(studyRoomPostId);
        assertThat(optionalStudyRoomPost).isNotPresent();

        List<File> studyRoomPostFiles = fileRepository.findStudyRoomPostFilesByStudyRoomPostId(studyRoomPostId);
        assertThat(studyRoomPostFiles.size()).isEqualTo(0);

    }

    private void createAndSaveStudyRoomPosts(StudyRoom studyRoom) {
        for (int i = 0; i < 4; i++) {
            StudyRoomPostDto studyRoomPostDto = new StudyRoomPostDto("Title" + (i + 1), "Content" + (i + 1));
            studyRoomPostRepository.save(StudyRoomPost.createStudyRoomPost(studyRoomPostDto, studyRoom));
        }
    }

    private StudyRoomPost createAndSaveStudyRoomPost(StudyRoom studyRoom) {
        StudyRoomPostDto studyRoomPostDto = new StudyRoomPostDto("Title", "Content");
        return studyRoomPostRepository.save(StudyRoomPost.createStudyRoomPost(studyRoomPostDto, studyRoom));
    }


    private StudyRoom createAndSaveStudyRoom() {
        Company company = createAndSaveCompany();
        StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom", 10, 10000, true, false, true);
        return studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));
    }

    private Company createAndSaveCompany() {
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-4235", UserRole.ROLE_STUDYROOM_ADMIN);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company", "Description", "Suwon", "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(18, 0));
        return companyRepository.save(Company.createCompany(companyDto, user));
    }

    private List<MultipartFile> createImages() {
        List<MultipartFile> files = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MockMultipartFile imageFile = new MockMultipartFile(
                    "file" + (i + 1),              // 필드 이름
                    "test" + (i + 1) + "-image.jpg",          // 파일 이름
                    "image/jpeg",              // 파일 유형
                    new byte[]{1, 2, 3, 4, 5}  // 파일 내용
            );
            files.add(imageFile);
        }
        return files;
    }
}
