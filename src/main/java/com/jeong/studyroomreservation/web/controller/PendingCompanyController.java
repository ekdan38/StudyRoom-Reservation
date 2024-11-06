package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompanyMapper;
import com.jeong.studyroomreservation.domain.service.PendingCompanyService;
import com.jeong.studyroomreservation.web.dto.PendingCompanyRequestDto;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j(topic = "CompanyController")
@RequiredArgsConstructor
public class PendingCompanyController {

    private final PendingCompanyService pendingCompanyService;
    private final PendingCompanyMapper pendingCompanyMapper;
    /**
     * 스터디 룸 회사 등록 요청은 간단하게 정보만으로 요청 하도록 하자.
     */
    // 스터디 룸 등록
    @PostMapping("/api/pending-companies")
    public ResponseEntity<ResponseDto<?>> createCompany(@RequestBody @Validated PendingCompanyRequestDto requestDto,
                                           BindingResult bindingResult,
                                           @AuthenticationPrincipal UserDto userDto){
        if(bindingResult.hasErrors()){
            log.error("validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }

        PendingCompanyDto pendingCompanyDto = pendingCompanyService.save(pendingCompanyMapper.requestToDto(requestDto, userDto));
        ResponseDto<PendingCompanyDto> responseBody = new ResponseDto<>("PendingCompany request received successfully.", pendingCompanyDto);
        return ResponseEntity.ok().body(responseBody);
    }
}
