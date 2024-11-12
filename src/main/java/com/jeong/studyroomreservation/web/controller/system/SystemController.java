package com.jeong.studyroomreservation.web.controller.system;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyWithUserDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompanyMapper;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.service.PendingCompanyService;
import com.jeong.studyroomreservation.web.dto.ResponseDto;
import com.jeong.studyroomreservation.web.dto.pendingcompany.PendingCompanyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j(topic = "[SystemController]")
@RequestMapping("/api/pending-companies")
public class SystemController {

    private final PendingCompanyService pendingCompanyService;
    private final PendingCompanyMapper pendingCompanyMapper;

    //승인 대기중인 업체 여러개 조회
    //GET /api/pending-companies?page=0&size=10&sort=name,asc
    @GetMapping
    public ResponseEntity<ResponseDto<Page<?>>> getPendingCompanies(Pageable pageable) {
//        Page<PendingCompanyDto> pendingCompanies = pendingCompanyService.getPendingCompanies(pageable);
        Page<PendingCompanyWithUserDto> pendingCompanies = pendingCompanyService.getPendingCompanies(pageable);
        ResponseDto<Page<?>> responseBody =
                new ResponseDto<>("Success", pendingCompanies);
        return ResponseEntity.ok().body(responseBody);
    }

    //승인 대기중인 업체 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<PendingCompanyWithUserDto>> getPendingCompany(@PathVariable("id") Long id) {

        PendingCompanyWithUserDto pendingCompany = pendingCompanyService.getPendingCompany(id);
        ResponseDto<PendingCompanyWithUserDto> responseBody =
                new ResponseDto<>("Success", pendingCompany);
        return ResponseEntity.ok().body(responseBody);
    }

    //승인 대기중인 업체 정보 수정
    // 관리자가 수정, 사용자도 수정 가능해야함.
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePendingCompany(@PathVariable("id") Long id,
                                                  @RequestBody @Validated PendingCompanyRequestDto requestDto,
                                                  @AuthenticationPrincipal UserDto userDto) {

        if (!checkPermission(userDto, id)) {
            ResponseDto<String> responseBody =
                    new ResponseDto<>("Access Denied", "No Permission to modify this PendingCompany");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        PendingCompanyDto pendingCompanyDto =
                pendingCompanyService.updatePendingCompany(id, pendingCompanyMapper.requestToDto(requestDto, null));
        ResponseDto<PendingCompanyDto> responseBody = new ResponseDto<>("Success", pendingCompanyDto);
        return ResponseEntity.ok().body(responseBody);
    }

    //거절 && 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deletePendingCompany(@PathVariable("id") Long id,
                                                                    @AuthenticationPrincipal UserDto userDto) {
        if (!checkPermission(userDto, id)) {
            ResponseDto<String> responseBody =
                    new ResponseDto<>("Access Denied", "No Permission to modify this PendingCompany");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }
        pendingCompanyService.deletePendingCompany(id);
        ResponseDto<String> responseBody = new ResponseDto<>("Success", "Delete PendingCompany id = " + id);
        return ResponseEntity.ok().body(responseBody);
    }

    //승인
    // 시스템 관리자가 만들고 사장님 세팅
    @PostMapping("/{id}")
    public ResponseEntity<ResponseDto<CompanyDto>> approvalPendingCompany(@PathVariable("id") Long id) {
        CompanyDto companyDto = pendingCompanyService.approvalPendingCompany(id);
        ResponseDto<CompanyDto> responseBody = new ResponseDto<>("Success", companyDto);

        return ResponseEntity.ok().body(responseBody);
    }

    private Boolean checkPermission(UserDto userDto, Long pendingCompanyId) {
        if (UserRole.ROLE_USER.name().equals(userDto.getRole().name()) ||
                UserRole.ROLE_STUDYROOM_ADMIN.name().equals(userDto.getRole().name())) {
            PendingCompany pendingCompany = pendingCompanyService.findByIdWithUser(pendingCompanyId);
            if (!pendingCompany.getUser().getId().equals(userDto.getId())) {
                return false;
            }
        }
        return true;
    }
}