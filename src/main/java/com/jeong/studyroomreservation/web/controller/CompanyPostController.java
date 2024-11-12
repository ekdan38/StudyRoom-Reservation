package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostResponseDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostUpdateResponseDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.post.company.CompanyPostMapper;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.service.CompanyPostService;
import com.jeong.studyroomreservation.domain.service.CompanyService;
import com.jeong.studyroomreservation.web.dto.companypost.CompanyPostRequestDto;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.companypost.CompanyPostUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company-post/{companyId}")
@Slf4j(topic = "[CompanyPostController]")
public class CompanyPostController {

    private final CompanyPostMapper companyPostMapper;
    private final CompanyPostService companyPostService;
    private final CompanyService companyService;

    // 글 등록
    // studyroom_admin
    @PostMapping
    public ResponseEntity<?> createPost(@RequestPart(value = "file", required = false) List<MultipartFile> files,
                                        @RequestPart("data") @Validated CompanyPostRequestDto requestDto,
                                        BindingResult bindingResult,
                                        @PathVariable("companyId") Long companyId,
                                        @AuthenticationPrincipal UserDto userDto) {

        // STUDYROOM_ADMIM이면 해당 company에 접근 권한이 있는지 확인.
        if (!checkPermission(userDto, companyId)) {
            ResponseDto<String> responseBody =
                    new ResponseDto<>("Access Denied", "No Permission to modify this companyPost");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        if (bindingResult.hasErrors()) {
            log.error("Validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }

        CompanyPostDto companyPostDto = companyPostMapper.requestToDto(requestDto);
        CompanyPostResponseDto response = companyPostService.createAndSave(companyId, companyPostDto, files);

        ResponseDto<CompanyPostResponseDto> responseBody = new ResponseDto<>("Success", response);
        return ResponseEntity.ok().body(responseBody);
    }

    // 글 조회 여러개
    @GetMapping
    public ResponseEntity<ResponseDto<Page<CompanyPostResponseDto>>> getCompanyPosts(@PathVariable("companyId") Long companyId,
                                                                                     Pageable pageable) {

        Page<CompanyPostResponseDto> responseDtos = companyPostService.getCompanyPosts(companyId, pageable);
        ResponseDto<Page<CompanyPostResponseDto>> responseBody = new ResponseDto<>("Success", responseDtos);
        return ResponseEntity.ok().body(responseBody);
    }

    // 글 조회 단건
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyPost(@PathVariable("companyId") Long companyId,
                                            @PathVariable("id") Long id){

        CompanyPostResponseDto responseDto = companyPostService.getCompanyPost(companyId, id);
        ResponseDto<CompanyPostResponseDto> responseBody = new ResponseDto<>("Success", responseDto);
        return ResponseEntity.ok().body(responseBody);
    }

    // 글 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompanyPost(@RequestPart(value = "file", required = false) List<MultipartFile> files,
                                               @RequestPart("data") @Validated CompanyPostUpdateRequestDto requestDto,
                                               BindingResult bindingResult,
                                               @PathVariable("companyId") Long companyId,
                                               @PathVariable("id") Long id,
                                               @AuthenticationPrincipal UserDto userDto){

        // STUDYROOM_ADMIM이면 해당 company에 접근 권한이 있는지 확인.
        if (!checkPermission(userDto, companyId)) {
            ResponseDto<String> responseBody =
                    new ResponseDto<>("Access Denied", "No Permission to modify this companyPost");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        if (bindingResult.hasErrors()) {
            log.error("Validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }

        CompanyPostDto companyPostDto = companyPostMapper.updateToDto(requestDto);
        CompanyPostUpdateResponseDto responseDto =
                companyPostService.updateCompanyPost(companyId, id, companyPostDto, files, requestDto.getDeleteImages());

        ResponseDto<CompanyPostUpdateResponseDto> responseBody = new ResponseDto<>("Success", responseDto);
        return ResponseEntity.ok().body(responseBody);
    }

    // 글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompanyPost( @PathVariable("companyId") Long companyId,
                                                @PathVariable("id") Long id,
                                                @AuthenticationPrincipal UserDto userDto){

        // STUDYROOM_ADMIM이면 해당 company에 접근 권한이 있는지 확인.
        if (!checkPermission(userDto, companyId)) {
            ResponseDto<String> responseBody =
                    new ResponseDto<>("Access Denied", "No Permission to modify this companyPost");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        companyPostService.deleteCompanyPost(companyId, id);
        ResponseDto<String> responseBody = new ResponseDto<>("Success", "Delete CompanyPost id = " + id);
        return ResponseEntity.ok().body(responseBody);
    }

    private Boolean checkPermission(UserDto userDto, Long companyId) {
        if (UserRole.ROLE_STUDYROOM_ADMIN.name().equals(userDto.getRole().name())) {
            Company company = companyService.findById(companyId);
            if (!company.getUser().getId().equals(userDto.getId())) {
                return false;
            }
        }
        return true;
    }
}
