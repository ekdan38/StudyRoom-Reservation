package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.company.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.pendingcompany.PendingCompanyWithUserDto;
import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompanyMapper;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserMapper;
import com.jeong.studyroomreservation.domain.entity.user.UserRole;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.PendingCompanyNotFoundException;
import com.jeong.studyroomreservation.domain.repository.PendingCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "[PendingCompanyService]")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PendingCompanyService {

    private final PendingCompanyRepository pendingCompanyRepository;
    private final PendingCompanyMapper pendingCompanyMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final CompanyService companyService;


    // pendingCompany 생성, 저장
    // user조회 쿼리 1번
    // 저장 쿼리 1번
    @Transactional
    public PendingCompanyDto createAndSave(PendingCompanyDto dto){
        // pendingCompany 생성
        Long userId = dto.getUserId();
        User user = userService.findById(userId); //조회 쿼리 1번

        PendingCompany pendingCompany = PendingCompany.createPendingCompany(dto, user);
        PendingCompany savedPendingCompany = pendingCompanyRepository.save(pendingCompany); // 저장 쿼리 1번
        return pendingCompanyMapper.entityToDto(savedPendingCompany, userId);
    }

    // 페이지로 pendingCompany들 조회
    // 조회 쿼리 1번 + 페이지 크기 n 에 따라 n번 User 조회.
    public Page<PendingCompanyWithUserDto> getPendingCompanies(Pageable pageable){
        Page<PendingCompany> page = pendingCompanyRepository.findAll(pageable); //조회 쿼리 1 번

        Page<PendingCompanyWithUserDto> map = page
                .map(p -> new PendingCompanyWithUserDto(
                        pendingCompanyMapper.entityToDto(p, null),
                        userMapper.entityToUserDto(p.getUser())));
        return map;
    }

    // pendingCompany 단건 조회
    // 조회 쿼리 2번
    public PendingCompanyWithUserDto getPendingCompany(Long id){
        PendingCompany pendingCompany = pendingCompanyRepository.findById(id)
                .orElseThrow(() -> new PendingCompanyNotFoundException(ErrorCode.PENDING_COMPANY_NOT_FOUND));//조회 쿼리 1번

        UserDto userDto = userMapper.entityToUserDto(pendingCompany.getUser()); // 조회 쿼리 1번

        PendingCompanyDto pendingCompanyDto = pendingCompanyMapper.entityToDto(pendingCompany, null);
        return new PendingCompanyWithUserDto(pendingCompanyDto, userDto);
    }

    // pendingCompany 수정
    // 조회 쿼리 1번, 업데이트 쿼리 1번
    @Transactional
    public PendingCompanyDto updatePendingCompany(Long id, PendingCompanyDto dto){
        PendingCompany pendingCompany = pendingCompanyRepository.findById(id)
                .orElseThrow(() -> new PendingCompanyNotFoundException(ErrorCode.PENDING_COMPANY_NOT_FOUND));
        pendingCompany.updatePendingCompany(dto);
        return pendingCompanyMapper.entityToDto(pendingCompany, null);
    }

    @Transactional
    // pendingCompany 삭제(거절)
    // 조회 쿼리 1번, 삭제 쿼리 1번
    public void deletePendingCompany(Long id){
        pendingCompanyRepository.findById(id)
                .orElseThrow(() -> new PendingCompanyNotFoundException(ErrorCode.PENDING_COMPANY_NOT_FOUND));
        pendingCompanyRepository.deleteById(id);
    }

    // pendingCompany 승인
    @Transactional
    public CompanyDto approvalPendingCompany(Long id){
        // pendingCompany 지우고, Company에 등록, 해당 User의 role 변경.

        // pendingCompany 조회
        // 조회 쿼리 1번
        PendingCompany pendingCompany = pendingCompanyRepository.findById(id)
                .orElseThrow(() -> new PendingCompanyNotFoundException(ErrorCode.PENDING_COMPANY_NOT_FOUND));

        // User 조회, Role 바꿔 주기
        // 조회 쿼리 1번
        User user = pendingCompany.getUser();
        user.updateUserRole(UserRole.ROLE_STUDYROOM_ADMIN);

        // Company 등록
        // Company에 정하는 쿼리 1번
        PendingCompanyDto pendingCompanyDto = pendingCompanyMapper.entityToDto(pendingCompany, null);
        CompanyDto companyDto = companyService.approvalPendingCompany(pendingCompanyDto, user);

        // pendingCompany 삭제
        // 삭제 쿼리 1번 (1차 캐시에서 가져와서 조회 쿼리 xx)
        pendingCompanyRepository.deleteById(id);
        return companyDto;
    }

}
