package com.jeong.studyroomreservation.web.controller.system;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompanyMapper;
import com.jeong.studyroomreservation.domain.service.CompanyService;
import com.jeong.studyroomreservation.domain.service.PendingCompanyService;
import com.jeong.studyroomreservation.domain.service.UserService;
import com.jeong.studyroomreservation.web.dto.PendingCompanyRequestDto;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j(topic = "[SystemController]")
@RequestMapping("/api/pending-companies")
public class SystemController {

    private final PendingCompanyService pendingCompanyService;
    private final CompanyService companyService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PendingCompanyMapper pendingCompanyMapper;

    //승인 대기중인 업체 여러개 조회
    //GET /api/pending-companies?page=0&size=10&sort=name,asc
    @GetMapping
    public ResponseEntity<ResponseDto<Page<PendingCompanyDto>>> getPendingCompanies(Pageable pageable){
        Page<PendingCompanyDto> pageDto = pendingCompanyService.getPendingCompanies(pageable);
        ResponseDto<Page<PendingCompanyDto>> responseBody = new ResponseDto<>("Get PendingCompanies Success", pageDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //승인 대기중인 업체 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<PendingCompanyDto>> getPendingCompany(@PathVariable("id") Long id){
        PendingCompanyDto pendingCompanyDto = pendingCompanyService.getPendingCompany(id);
        ResponseDto<PendingCompanyDto> responseBody = new ResponseDto<>("Get PendingCompany Success", pendingCompanyDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //승인 대기중인 업체 정보 수정
    // 관리자가 수정, 사용자도 수정 가능해야함.
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<PendingCompanyDto>> updatePendingCompany(@PathVariable("id") Long id,
                                                  @RequestBody PendingCompanyRequestDto requestDto,
                                                  @AuthenticationPrincipal UserDto userDto){
        PendingCompanyDto pendingCompanyDto =
                pendingCompanyService.updatePendingCompany(id, pendingCompanyMapper.requestToDto(requestDto, userDto));
        ResponseDto<PendingCompanyDto> responseBody = new ResponseDto<>("Update Success", pendingCompanyDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //거절 && 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> rejectCompany(@PathVariable("id") Long id){

        pendingCompanyService.deletePendingCompany(id);
        ResponseDto<String> responseBody =
                new ResponseDto<>("Reject Pending Company Success", "PendingCompany id = " + id);
        return ResponseEntity.ok().body(responseBody);
    }

    //승인
    // 시스템 관리자가 만들고 사장님 세팅
    @PostMapping("/{id}")
    public ResponseEntity<ResponseDto<CompanyDto>> approvalCompany(@PathVariable("id") Long id){

        // 해당하는 pendingCompany 조회
        PendingCompanyDto pendingCompanyDto = pendingCompanyService.getPendingCompany(id);
        // 해당하는 pendingComanpy 삭제
        // 해당하는 pendingCompany => Company에 등록
        // 같은 트랜잭션에서

        CompanyDto savedCompanyDto = pendingCompanyService
                .approvalPendingCompany(id, pendingCompanyDto);
        ResponseDto<CompanyDto> responseBody =
                new ResponseDto<>("Approval Pending Company Success", savedCompanyDto);
        return ResponseEntity.ok().body(responseBody);
    }
}
