package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostResponseDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.file.CompanyPostFile;
import com.jeong.studyroomreservation.domain.entity.file.File;
import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPost;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.FileRepository;
import com.jeong.studyroomreservation.domain.repository.CompanyPostRepository;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
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
class CompanyPostServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    CompanyPostService companyPostService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    CompanyPostRepository companyPostRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    S3ImageUtil s3ImageUtil;

    @Test
    @Transactional
    @DisplayName("CompanyPost 생성, 저장, 이미지 o")
    public void createAndSave_WithImages() {
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        em.flush();
        em.clear();

        List<MultipartFile> files = createImages();
        CompanyPostDto companyPostDto = new CompanyPostDto("Title", "Content");

        //when
        CompanyPostResponseDto responseDto = companyPostService.createAndSave(companyId, companyPostDto, files);

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
    @DisplayName("CompanyPost 생성, 저장, 이미지 x")
    public void createAndSave_WithoutImages() {
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        em.flush();
        em.clear();

        CompanyPostDto companyPostDto = new CompanyPostDto("Title", "Content");

        List<MultipartFile> files = new ArrayList<>();

        //when
        CompanyPostResponseDto responseDto = companyPostService.createAndSave(companyId, companyPostDto, files);

        //then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo("Title");
        assertThat(responseDto.getContent()).isEqualTo("Content");
        assertThat(responseDto.getImages().size()).isEqualTo(0);

    }

    @Test
    @Transactional
    @DisplayName("CompanyPost 페이징 조회")
    public void getCompanyPosts() {
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        createAndSaveCompanyPosts(company);
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));

        em.flush();
        em.clear();

        //when
        Page<CompanyPostResponseDto> page = companyPostService.getCompanyPosts(companyId, pageRequest);

        //then
        List<CompanyPostResponseDto> content = page.getContent();
        assertThat(content.size()).isEqualTo(2); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(4); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }


    @Test
    @Transactional
    @DisplayName("CompanyPost 단건 조회, 이미지 o")
    public void getCompanyPost_WithImages() {
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        CompanyPost companyPost = createAndSaveCompanyPost(company);
        Long companyPostId = companyPost.getId();

        List<MultipartFile> images = createImages();
        List<String> cleanupImageList = new ArrayList<>();

        for (MultipartFile image : images) {
            String storeName = s3ImageUtil.upload(image);

            String extention = image.getOriginalFilename()
                    .substring(image.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(image.getOriginalFilename(), storeName, image.getSize(), extention);
            fileRepository.save(CompanyPostFile.createCompanyPostFile(fileDto, companyPost));
            cleanupImageList.add(storeName);
        }
        em.flush();
        em.clear();

        //when
        CompanyPostResponseDto responseDto = companyPostService.getCompanyPost(companyId, companyPostId);

        //then
        assertThat(responseDto.getId()).isEqualTo(companyPostId);
        assertThat(responseDto.getImages().size()).isEqualTo(2);

        //clear
        for (String image : cleanupImageList) {
            s3ImageUtil.deleteImageFromS3(image);
        }
    }



    @Test
    @Transactional
    @DisplayName("CompanyPost 단건 조회, 이미지 x")
    public void getCompanyPost_WithoutImages() {
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        CompanyPost companyPost = createAndSaveCompanyPost(company);
        Long companyPostId = companyPost.getId();

        em.flush();
        em.clear();

        //when
        CompanyPostResponseDto responseDto = companyPostService.getCompanyPost(companyId, companyPostId);

        //then
        assertThat(responseDto.getId()).isEqualTo(companyPostId);
    }


    @Test
    @Transactional
    @DisplayName("CompanyPost 수정")
    public void updateCompanyPost(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        CompanyPost companyPost = createAndSaveCompanyPost(company);
        Long companyPostId = companyPost.getId();

        List<MultipartFile> images = createImages();
        List<String> deleteFiles = new ArrayList<>();

        for (MultipartFile image : images) {
            String storeName = s3ImageUtil.upload(image);

            String extention = image.getOriginalFilename()
                    .substring(image.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(image.getOriginalFilename(), storeName, image.getSize(), extention);
            fileRepository.save(CompanyPostFile.createCompanyPostFile(fileDto, companyPost));
            deleteFiles.add(storeName);
        }

        em.flush();
        em.clear();

        //when
        List<MultipartFile> newImages = createImages();
        CompanyPostDto companyPostDto = new CompanyPostDto("NewTitle", "NewContent");
        CompanyPostUpdateResponseDto responseDto = companyPostService
                .updateCompanyPost(companyId, companyPostId, companyPostDto, newImages, deleteFiles);

        //then
        assertThat(responseDto.getId()).isEqualTo(companyPostId);
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
    @DisplayName("CompanyPost 삭제")
    public void deleteCompanyPost(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        CompanyPost companyPost = createAndSaveCompanyPost(company);
        Long companyPostId = companyPost.getId();

        List<MultipartFile> images = createImages();

        for (MultipartFile image : images) {
            String storeName = s3ImageUtil.upload(image);

            String extention = image.getOriginalFilename()
                    .substring(image.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(image.getOriginalFilename(), storeName, image.getSize(), extention);
            fileRepository.save(CompanyPostFile.createCompanyPostFile(fileDto, companyPost));
        }

        em.flush();
        em.clear();

        //when
        companyPostService.deleteCompanyPost(companyId, companyPostId);

        em.flush();
        em.clear();

        //then
        Optional<CompanyPost> optionalCompanyPost = companyPostRepository.findById(companyPostId);
        assertThat(optionalCompanyPost).isNotPresent();

        List<File> companyPostFiles = fileRepository.findCompanyPostFilesByCompanyPostId(companyPostId);
        assertThat(companyPostFiles.size()).isEqualTo(0);
    }






    private void createAndSaveCompanyPosts(Company company) {
        for (int i = 0; i < 4; i++) {
            CompanyPostDto companyPostDto = new CompanyPostDto("Title" + (i + 1), "Content" + (i + 1));
            companyPostRepository.save(CompanyPost.createCompanyPost(companyPostDto, company));
        }
    }

    private CompanyPost createAndSaveCompanyPost(Company company) {
        CompanyPostDto companyPostDto = new CompanyPostDto("Title", "Content");
        return companyPostRepository.save(CompanyPost.createCompanyPost(companyPostDto, company));
    }

    private Company createAndSaveCompany() {
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-0000", UserRole.ROLE_STUDYROOM_ADMIN);
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