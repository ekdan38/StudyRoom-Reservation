package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomResponseDto;
import com.jeong.studyroomreservation.domain.dto.studyroom.StudyRoomUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.file.File;
import com.jeong.studyroomreservation.domain.entity.file.StudyRoomFile;
import com.jeong.studyroomreservation.domain.entity.stuydroom.StudyRoom;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.FileRepository;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.domain.repository.StudyRoomRepository;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import com.jeong.studyroomreservation.domain.s3.S3ImageUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest

class StudyRoomServiceTest {

    @Autowired
    StudyRoomService studyRoomService;

    @Autowired
    StudyRoomRepository studyRoomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    S3ImageUtil s3ImageUtil;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    @DisplayName("StudyRoom 생성, 저장, 이미지 x")
    public void createAndSave_WithoutImages(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom", 10, 10000, true, false, true);

        //when
        StudyRoomResponseDto responseDto = studyRoomService.createAndSave(companyId, studyRoomDto, null);

        em.flush();
        em.clear();

        //then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getName()).isEqualTo("StudyRoom");
        assertThat(responseDto.getCapacity()).isEqualTo(10);
        assertThat(responseDto.getPrice()).isEqualTo(10000);
        assertThat(responseDto.getTv()).isTrue();
        assertThat(responseDto.getWifi()).isFalse();
        assertThat(responseDto.getWhiteBoard()).isTrue();
        assertThat(responseDto.getImages().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("StudyRoom 생성, 저장, 이미지 o")
    public void createAndSave_WithImages(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom", 10, 10000, true, false, true);

        List<MultipartFile> files = createImages();

        //when
        StudyRoomResponseDto responseDto = studyRoomService.createAndSave(companyId, studyRoomDto, files);
        Long studyRoomId = responseDto.getId();

        em.flush();
        em.clear();

        //then
        List<File> studyRoomFiles = fileRepository.findStudyRoomFilesByStudyRoomId(studyRoomId);
        int cnt = 1;
        for (File studyRoomFile : studyRoomFiles) {
            assertThat(studyRoomFile.getId()).isNotNull();
            assertThat(studyRoomFile.getS3FileName()).isNotNull();
            assertThat(studyRoomFile.getOriginalFileName()).isEqualTo("test" + (cnt++) + "-image.jpg");
            s3ImageUtil.deleteImageFromS3(studyRoomFile.getS3FileName());

        }
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getName()).isEqualTo("StudyRoom");
        assertThat(responseDto.getCapacity()).isEqualTo(10);
        assertThat(responseDto.getPrice()).isEqualTo(10000);
        assertThat(responseDto.getTv()).isTrue();
        assertThat(responseDto.getWifi()).isFalse();
        assertThat(responseDto.getWhiteBoard()).isTrue();
        assertThat(responseDto.getImages().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @DisplayName("StudyRoom 단건 조회, 이미지 x")
    public void getStudyRoom_WithoutImages(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        StudyRoom studyRoom = createAndSaveStudyRoom(company);
        Long studyRoomId = studyRoom.getId();

        em.flush();
        em.clear();

        //when
        StudyRoomResponseDto responseDto = studyRoomService.getStudyRoom(companyId, studyRoomId);

        //then
        assertThat(responseDto.getId()).isEqualTo(studyRoomId);
        assertThat(responseDto.getName()).isEqualTo(studyRoom.getName());
    }

    @Test
    @Transactional
    @DisplayName("StudyRoom 단건 조회, 이미지 o")
    public void getStudyRoom_WithImages(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        StudyRoom studyRoom = createAndSaveStudyRoom(company);
        Long studyRoomId = studyRoom.getId();

        //이미지 수동 저장.
        List<MultipartFile> images = createImages();
        List<String> cleanupImageList = new ArrayList<>();

        for (MultipartFile image : images) {
            String storeName = s3ImageUtil.upload(image);

            String extention = image.getOriginalFilename()
                    .substring(image.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(image.getOriginalFilename(), storeName, image.getSize(), extention);
            fileRepository.save(StudyRoomFile.createStudyRoomFile(fileDto, studyRoom));
            cleanupImageList.add(storeName);
        }
        em.flush();
        em.clear();

        //when
        StudyRoomResponseDto responseDto = studyRoomService.getStudyRoom(companyId, studyRoomId);

        //then
        assertThat(responseDto.getId()).isEqualTo(studyRoomId);
        assertThat(responseDto.getName()).isEqualTo(studyRoom.getName());
        assertThat(responseDto.getImages().size()).isEqualTo(2);

        //clear
        for (String image : cleanupImageList) {
            s3ImageUtil.deleteImageFromS3(image);
        }
    }

    //새로 등록하는 이미지 o, 지우는 이미지.
    @Test
    @Transactional
    @DisplayName("StudyRoom 수정")
    public void updateStudyRoom(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        StudyRoom studyRoom = createAndSaveStudyRoom(company);
        Long studyRoomId = studyRoom.getId();

        //이미지 수동 저장.
        List<MultipartFile> images = createImages();
        List<String> deleteFiles = new ArrayList<>();

        for (MultipartFile image : images) {
            String storeName = s3ImageUtil.upload(image);

            String extention = image.getOriginalFilename()
                    .substring(image.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(image.getOriginalFilename(), storeName, image.getSize(), extention);
            fileRepository.save(StudyRoomFile.createStudyRoomFile(fileDto, studyRoom));
            deleteFiles.add(storeName);
        }
        em.flush();
        em.clear();

        List<MultipartFile> newImages = createImages();

        //when
        StudyRoomDto updateDto = new StudyRoomDto("newStudyRoom", 5, 100, true, false, true);
        StudyRoomUpdateResponseDto responseDto = studyRoomService.updateStudyRoom(companyId, studyRoomId, updateDto, newImages, deleteFiles);

        //then
        assertThat(responseDto.getId()).isEqualTo(studyRoomId);
        assertThat(responseDto.getName()).isEqualTo("newStudyRoom");
        assertThat(responseDto.getCapacity()).isEqualTo(5);
        assertThat(responseDto.getPrice()).isEqualTo(100);
        assertThat(responseDto.getNewImages().size()).isEqualTo(2);
        assertThat(responseDto.getDeleteImages().size()).isEqualTo(2);

        //clear
        List<String> responseDtoNewImages = responseDto.getNewImages();
        for (String responseDtoNewImage : responseDtoNewImages) {
            s3ImageUtil.deleteImageFromS3(responseDtoNewImage);
        }
    }

    @Test
    @Transactional
    @DisplayName("StudyRoom 삭제")
    public void deleteStudyRoom(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        StudyRoom studyRoom = createAndSaveStudyRoom(company);
        Long studyRoomId = studyRoom.getId();

        //이미지 수동 저장.
        List<MultipartFile> images = createImages();
        List<String> cleanupImageList = new ArrayList<>();

        for (MultipartFile image : images) {
            String storeName = s3ImageUtil.upload(image);

            String extention = image.getOriginalFilename()
                    .substring(image.getOriginalFilename().lastIndexOf(".") + 1);

            FileDto fileDto = new FileDto(image.getOriginalFilename(), storeName, image.getSize(), extention);
            fileRepository.save(StudyRoomFile.createStudyRoomFile(fileDto, studyRoom));
            cleanupImageList.add(storeName);
        }
        em.flush();
        em.clear();

        //when
        studyRoomService.deleteStudyRoom(companyId, studyRoomId);

        em.flush();
        em.clear();

        //then
        Optional<StudyRoom> optionalStudyRoom = studyRoomRepository.findByCompanyIdAndIdWithStudyRoomFiles(companyId, studyRoomId);
        assertThat(optionalStudyRoom).isNotPresent();
        List<File> studyRoomFiles = fileRepository.findStudyRoomFilesByStudyRoomId(studyRoomId);
        assertThat(studyRoomFiles.size()).isEqualTo(0);

        //clear
        for (String image : cleanupImageList) {
            s3ImageUtil.deleteImageFromS3(image);
        }
    }





    private StudyRoom createAndSaveStudyRoom(Company company){
        StudyRoomDto studyRoomDto = new StudyRoomDto("StudyRoom", 10, 10000, true, false, true);
        return studyRoomRepository.save(StudyRoom.createStudyRoom(studyRoomDto, company));
    }


    private Company createAndSaveCompany(){
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-5431-0000", UserRole.ROLE_STUDYROOM_ADMIN);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company", "Description", "Suwon", "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(18, 0));
        return companyRepository.save(Company.createCompany(companyDto, user));
    }

    private List<MultipartFile> createImages(){
        List<MultipartFile> files = new ArrayList<>();
        for(int i  = 0; i < 2; i++){
            MockMultipartFile imageFile = new MockMultipartFile(
                    "file" + (i + 1),              // 필드 이름
                    "test" +  (i + 1) + "-image.jpg",          // 파일 이름
                    "image/jpeg",              // 파일 유형
                    new byte[]{1, 2, 3, 4, 5}  // 파일 내용
            );
            files.add(imageFile);
        }
        return files;
    }

}