package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.company.CompanyResponseDto;
import com.jeong.studyroomreservation.domain.dto.company.CompanyUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.file.FileDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.file.CompanyFile;
import com.jeong.studyroomreservation.domain.entity.file.File;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.repository.FileRepository;
import com.jeong.studyroomreservation.domain.repository.UserRepository;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import com.jeong.studyroomreservation.domain.repository.PendingCompanyRepository;
import com.jeong.studyroomreservation.domain.s3.S3ImageUtil;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompanyServiceTest {

    @Autowired
    CompanyService companyService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    S3ImageUtil s3ImageUtil;

    @Autowired
    PendingCompanyRepository pendingCompanyRepository;


    @Test
    @Transactional
    @DisplayName("Company 페이징 조회")
    public void getCompanies() {
        //given
        createAndSaveCompanies();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));


        //when
        Page<CompanyResponseDto> page = companyService.getCompanies(pageRequest);

        //then
        List<CompanyResponseDto> content = page.getContent();
        assertThat(content.size()).isEqualTo(2); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(4); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    @Transactional
    @DisplayName("Company 단건 조회")
    public void getCompany() {
        //given
        Long companyId = createAndSaveCompany().getId();

        //when
        CompanyResponseDto responseDto = companyService.getCompany(companyId);

        //then
        assertThat(responseDto.getId()).isEqualTo(companyId);
    }

    @Test
    @Transactional
    @DisplayName("Company 수정(정보 수정, 이미지 등록)")
    public void updateCompany_newImages() {
        //given
        Long companyId = createAndSaveCompany().getId();
        CompanyDto companyDto = new CompanyDto("New Company", "New Description", "New Suwon", "010-1111-1111" , LocalTime.of(9, 0), LocalTime.of(18, 0));

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",              // 필드 이름
                "test-image.jpg",          // 파일 이름
                "image/jpeg",              // 파일 유형
                new byte[]{1, 2, 3, 4, 5}  // 파일 내용
        );

        List<MultipartFile> files = new ArrayList<>();
        files.add(imageFile);

        //when
        CompanyUpdateResponseDto responseDto = companyService.updateCompany(companyId, companyDto, files, null);
        List<String> newImages = responseDto.getNewImages();

        List<File> companyFilesByCompanyId = fileRepository.findCompanyFilesByCompanyId(companyId);

        //then
        for (File file : companyFilesByCompanyId) {
            assertThat(file.getFileType()).isEqualTo("jpg");
            assertThat(file.getOriginalFileName()).isEqualTo("test-image.jpg");
            for (String newImage : newImages) {
                assertThat(file.getS3FileName()).isEqualTo(newImage);
                s3ImageUtil.deleteImageFromS3(newImage);
            }
        }

    }

    @Test
    @Transactional
    @DisplayName("Company 수정(정보 수정, 이미지 삭제)")
    public void updateCompany_deleteImage() {
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();
        CompanyDto companyDto = new CompanyDto("New Company", "New Description", "New Suwon", "010-1111-1111" , LocalTime.of(9, 0), LocalTime.of(18, 0));

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",              // 필드 이름
                "test-image.jpg",          // 파일 이름
                "image/jpeg",              // 파일 유형
                new byte[]{1, 2, 3, 4, 5}  // 파일 내용
        );
        String storeFilename = s3ImageUtil.upload(imageFile);//s3등록
        String extention = imageFile.getOriginalFilename()
                .substring(imageFile.getOriginalFilename().lastIndexOf(".") + 1);

        FileDto fileDto = new FileDto(imageFile.getOriginalFilename(), storeFilename, imageFile.getSize(), extention);

        CompanyFile companyFile = fileRepository.save(CompanyFile.createCompanyFile(fileDto, company));

        List<MultipartFile> files = new ArrayList<>();

        List<String> deleteFiles = new ArrayList<>();
        deleteFiles.add(storeFilename);

        //when
        CompanyUpdateResponseDto responseDto = companyService.updateCompany(companyId, companyDto, files, deleteFiles);
        List<String> newImages = responseDto.getNewImages();
        List<File> companyFilesByCompanyId = fileRepository.findCompanyFilesByCompanyId(companyId);

        //then
        assertThat(newImages.size()).isEqualTo(0);
        assertThat(companyFilesByCompanyId.size()).isEqualTo(0);
        assertThat(responseDto.getDeleteImages().contains(storeFilename)).isTrue();

    }

    @Test
    @Transactional
    @DisplayName("Company 수정(정보 수정,이미지 등록, 이미지 삭제)")
    public void updateCompany_NewImages_And_DeleteImage() {
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();
        CompanyDto companyDto = new CompanyDto("New Company", "New Description", "New Suwon", "010-1111-1111", LocalTime.of(9, 0), LocalTime.of(18, 0));

        MockMultipartFile imageFile = new MockMultipartFile(
                "file",              // 필드 이름
                "test-image.jpg",          // 파일 이름
                "image/jpeg",              // 파일 유형
                new byte[]{1, 2, 3, 4, 5}  // 파일 내용
        );
        String storeFilename = s3ImageUtil.upload(imageFile);//s3등록
        String extention = imageFile.getOriginalFilename()
                .substring(imageFile.getOriginalFilename().lastIndexOf(".") + 1);

        FileDto fileDto = new FileDto(imageFile.getOriginalFilename(), storeFilename, imageFile.getSize(), extention);

        CompanyFile companyFile = fileRepository.save(CompanyFile.createCompanyFile(fileDto, company));


        MockMultipartFile imageFile2 = new MockMultipartFile(
                "file2",              // 필드 이름
                "test2-image.jpg",          // 파일 이름
                "image/jpeg",              // 파일 유형
                new byte[]{1, 2, 3, 4, 5}  // 파일 내용
        );

        List<MultipartFile> files = new ArrayList<>();
        files.add(imageFile2);

        List<String> deleteFiles = new ArrayList<>();
        deleteFiles.add(storeFilename);

        //when
        CompanyUpdateResponseDto responseDto = companyService.updateCompany(companyId, companyDto, files, deleteFiles);
        List<String> newImages = responseDto.getNewImages();
        List<File> companyFilesByCompanyId = fileRepository.findCompanyFilesByCompanyId(companyId);

        //then
        assertThat(newImages.size()).isEqualTo(1);
        assertThat(companyFilesByCompanyId.size()).isEqualTo(1);
        assertThat(responseDto.getDeleteImages().contains(storeFilename)).isTrue();

        for (File file : companyFilesByCompanyId) {
            assertThat(file.getFileType()).isEqualTo("jpg");
            assertThat(file.getOriginalFileName()).isEqualTo("test2-image.jpg");
            for (String newImage : newImages) {
                assertThat(file.getS3FileName()).isEqualTo(newImage);
                s3ImageUtil.deleteImageFromS3(newImage);
            }
        }
    }

    @Test
    @Transactional
    @DisplayName("PedingCompany 승인")
    public void approvalPendingCompany(){
        //given
        UserDto userDto = new UserDto("username", "password@", "user", "user@gmail.com", "010-0000-0000", UserRole.ROLE_USER);
        User user = userRepository.save(User.createUser(userDto));
        Long userId = user.getId();

        PendingCompanyDto pendingCompanyDto = new PendingCompanyDto(userId, "Company", "description", "Suwon", "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(9, 0));
        PendingCompany savedPendingCompany = pendingCompanyRepository.save(PendingCompany.createPendingCompany(pendingCompanyDto, user));

        //when
        CompanyDto companyDto = companyService.approvalPendingCompany(pendingCompanyDto, user);

        //then
        assertThat(companyDto.getId()).isNotNull();
        assertThat(companyDto.getName()).isEqualTo(savedPendingCompany.getName());
        assertThat(companyDto.getLocation()).isEqualTo(savedPendingCompany.getLocation());

    }

    @Test
    @Transactional
    @DisplayName("Company 삭제")
    public void deleteCompany(){
        //given
        Company company = createAndSaveCompany();
        Long companyId = company.getId();

        //when
        companyService.deleteCompany(companyId);
        Optional<Company> optionalCompany = companyRepository.findById(companyId);

        //then
        assertThat(optionalCompany).isNotPresent();
    }





    private void createAndSaveCompanies() {
        for (int i = 0; i < 4; i++) {
            UserDto userDto = new UserDto("username" + (i + 1), "password@", "user",
                    "user" + (i + 1) + "@gmail.com", "010-0000-" + 1000 + (i + 1), UserRole.ROLE_STUDYROOM_ADMIN);
            User user = userRepository.save(User.createUser(userDto));

            CompanyDto companyDto = new CompanyDto("Company" + (i + 1), "Description", "Suwon", "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(18, 0));
            Company company = companyRepository.save(Company.createCompany(companyDto, user));
        }
    }

    private Company createAndSaveCompany() {
        UserDto userDto = new UserDto("username", "password@", "user",
                "user@gmail.com", "010-0000-0000", UserRole.ROLE_STUDYROOM_ADMIN);
        User user = userRepository.save(User.createUser(userDto));

        CompanyDto companyDto = new CompanyDto("Company", "Description", "Suwon", "010-0000-0000", LocalTime.of(9, 0), LocalTime.of(18, 0));
        Company company = companyRepository.save(Company.createCompany(companyDto, user));
        return company;
    }
}