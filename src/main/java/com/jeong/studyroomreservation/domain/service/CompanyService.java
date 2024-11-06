package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.compnay.CompanyMapper;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.entity.user.UserMapper;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.CompanyNotFoundException;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "[CompanyService]")
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserMapper userMapper;

    @Transactional
    public CompanyDto save(CompanyDto dto) {
        User user = userMapper.userDtoToEntity(dto.getUserDto());
        Company savedCompany = companyRepository.save(Company.createCompany(dto, user));
        return companyMapper.entityToDto(savedCompany);
    }

    public Company getCompany(Long adminId) {
        return companyRepository.findByUserId(adminId)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
    }

}
