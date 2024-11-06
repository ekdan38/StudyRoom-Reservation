package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.UserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

@Service
@Slf4j(topic = "[PendingCompanyService]")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PendingCompanyService {

    private final CompanyService companyService;
    private final UserService userService;
    private final PendingCompanyRepository pendingCompanyRepository;
    private final PendingCompanyMapper pendingCompanyMapper;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;


    @Transactional
    public PendingCompanyDto save(PendingCompanyDto dto) {
        User user = userMapper.userDtoToEntity(dto.getUserDto());
        PendingCompany savedPendingCompany = pendingCompanyRepository.save(PendingCompany.createPendingCompany(dto, user));
        log.info("Save PendingCompany = {}", savedPendingCompany.getName());
        return pendingCompanyMapper.entityToDto(savedPendingCompany);
    }

    public Page<PendingCompanyDto> getPendingCompanies(Pageable pageable) {
        Page<PendingCompany> page = pendingCompanyRepository.findAll(pageable);
        return page.map(pendingCompanyMapper::entityToDto);
    }

    public PendingCompanyDto getPendingCompany(Long id) {
        //GloabalExceptionHandler에서 처리.
        PendingCompany foundPendingCompany = pendingCompanyRepository.findById(id)
                .orElseThrow(() -> new PendingCompanyNotFoundException(ErrorCode.PENDING_COMPANY_NOT_FOUND));
        return pendingCompanyMapper.entityToDto(foundPendingCompany);
    }

    // 해당하는 pendingComanpy 삭제
    // 해당하는 pendingCompany => Company에 등록
    // 같은 트랜잭션에서
    @Transactional
    public CompanyDto approvalPendingCompany(Long id, PendingCompanyDto pendingCompanyDto) {
        log.info("==Start Transaction==");
        deletePendingCompany(id);
        Long userId = pendingCompanyDto.getUserDto().getId();
        UserDto updatedUserDto = userService.updateRole(userId, UserRole.ROLE_STUDYROOM_ADMIN);
        pendingCompanyDto.setUserDto(updatedUserDto);
        return companyService.save(modelMapper.map(pendingCompanyDto, CompanyDto.class));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deletePendingCompany(Long id) {
        pendingCompanyRepository.deleteById(id);
        log.info("Delete PendingCompany id = {}", id);
    }

    @Transactional
    public PendingCompanyDto updatePendingCompany(Long id, PendingCompanyDto dto) {
        PendingCompany foundPendingCompany = pendingCompanyRepository.findById(id)
                .orElseThrow(() -> new PendingCompanyNotFoundException(ErrorCode.PENDING_COMPANY_NOT_FOUND));
        foundPendingCompany.updatePendingCompany(dto);
        return pendingCompanyMapper.entityToDto(foundPendingCompany);
    }
}
