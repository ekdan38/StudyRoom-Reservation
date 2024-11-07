package com.jeong.studyroomreservation.web.controller;

import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompanyMapper;
import com.jeong.studyroomreservation.domain.service.PendingCompanyService;
import com.jeong.studyroomreservation.web.dto.pendingcompany.PendingCompanyRequestDto;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.pendingcompany.PendingCompanyResponseDto;
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
    // Company 등록 요청 => PendingCompany
    @PostMapping("/api/pending-companies")
    public ResponseEntity<ResponseDto<?>> createPendingCompany(@RequestBody @Validated PendingCompanyRequestDto requestDto,
                                                  BindingResult bindingResult,
                                                  @AuthenticationPrincipal UserDto userDto){
        if(bindingResult.hasErrors()){
            log.error("Validation Error = {}", bindingResult);
            ResponseDto<BindingResult> responseBody = new ResponseDto<>("Validation Error", bindingResult);
            return ResponseEntity.badRequest().body(responseBody);
        }
        PendingCompanyDto pendingCompanyDto = pendingCompanyService.createAndSave(pendingCompanyMapper.requestToDto(requestDto, userDto.getId()));

        //응답
        PendingCompanyResponseDto responseDto = new PendingCompanyResponseDto(pendingCompanyDto, pendingCompanyDto.getUserId());
        ResponseDto<PendingCompanyResponseDto> responseBody = new ResponseDto<>("Success", responseDto);
        return ResponseEntity.ok().body(responseBody);
    }
}
