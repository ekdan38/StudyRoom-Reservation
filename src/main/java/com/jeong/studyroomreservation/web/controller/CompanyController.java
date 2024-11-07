package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.compnay.CompanyMapper;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.service.CompanyService;
import com.jeong.studyroomreservation.web.dto.CompanyUpdateDto;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
@Slf4j(topic = "[CompanyController]")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    // Company 여러건 조회
    @GetMapping
    public ResponseEntity<ResponseDto<Page<CompanyDto>>> getCompanies(Pageable pageable){
        Page<CompanyDto> companies = companyService.getCompanies(pageable);
        ResponseDto<Page<CompanyDto>> responseBody = new ResponseDto<>("Success", companies);
        return ResponseEntity.ok().body(responseBody);
    }

    // Company 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompany(@PathVariable("id") Long id){
        CompanyDto company = companyService.getCompany(id);
        ResponseDto<CompanyDto> responseBody = new ResponseDto<>("Success", company);
        return ResponseEntity.ok().body(responseBody);
    }


    // Company 정보 수정
    /**
     * STUDYROOM_ADMIN이상 가능
     * 해당 USER가 SYSTEM_ADMIN이면 건너뛰고, STUDYROOM_ADMIM이면 Company조회하고 해당 userId가 일치하는지 확인
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable("id") Long id,
                                           @RequestBody @Validated CompanyUpdateDto updateDto,
                                           BindingResult bindingResult,
                                           @AuthenticationPrincipal UserDto userDto){
        // STUDYROOM_ADMIM이면
        if(UserRole.ROLE_STUDYROOM_ADMIN.name().equals(userDto.getRole().name())){
            Company company = companyService.findById(id);
            if(!company.getUser().getId().equals(userDto.getId())){
                ResponseDto<String> responseBody =
                        new ResponseDto<>("Access Denied", "No Permission to modify this company");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
            }
        }

        if(bindingResult.hasErrors()){
            log.error("Validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }
        CompanyDto companyDto = companyService.updateCompany(id, companyMapper.requestToDto(updateDto));
        ResponseDto<CompanyDto> responseBody = new ResponseDto<>("Success", companyDto);
        return ResponseEntity.ok().body(responseBody);
    }


    // Company 삭제 연관된거 모두 삭제되어야함
    // Todo Company 엔터티에서 cascade랑 orphanl그거 걸자.
    /**
     * STUDYROOM_ADMIN이상 가능
     * 해당 USER가 SYSTEM_ADMIN이면 건너뛰고, STUDYROOM_ADMIM이면 Company조회하고 해당 userId가 일치하는지 확인
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<?>> deleteCompany(@PathVariable("id") Long id,
                                                             @AuthenticationPrincipal UserDto userDto){
        // STUDYROOM_ADMIM이면
        if(UserRole.ROLE_STUDYROOM_ADMIN.name().equals(userDto.getRole().name())){
            Company company = companyService.findById(id);
            if(!company.getUser().getId().equals(userDto.getId())){
                ResponseDto<String> responseBody =
                        new ResponseDto<>("Access Denied", "No Permission to modify this company");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
            }
        }
        companyService.deleteCompany(id);
        ResponseDto<String> responseBody = new ResponseDto<>("Success", "Delete Company id = " + id);
        return ResponseEntity.ok().body(responseBody);
    }
}
