package com.jeong.studyroomreservation.domain.service;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
import com.jeong.studyroomreservation.domain.dto.PendingCompanyWithUserDto;
import com.jeong.studyroomreservation.domain.entity.compnay.Company;
import com.jeong.studyroomreservation.domain.entity.compnay.CompanyMapper;
import com.jeong.studyroomreservation.domain.entity.pendingcompany.PendingCompany;
import com.jeong.studyroomreservation.domain.entity.user.User;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.CompanyNotFoundException;
import com.jeong.studyroomreservation.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "[CompanyService]")
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    // 페이징으로 Company들 조회
    // 조회 쿼리 1번
    public Page<CompanyDto> getCompanies(Pageable pageable){
        Page<Company> page = companyRepository.findAll(pageable); //조회 쿼리 1 번
        return page.map(p -> new CompanyDto(
                p.getId(),
                null,
                p.getName(),
                p.getDescription(),
                p.getLocation(),
                p.getPhoneNumber())
        );
    }

    // Company 단건 조회
    // 조회 쿼리 1번
    public CompanyDto getCompany(Long id){
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        return companyMapper.entityToDto(company, null);
    }

    // Company 업데이트
    // company 조회 쿼리 1번
    // update 쿼리 1번
    @Transactional
    public CompanyDto updateCompany(Long id, CompanyDto updateDto){
        // company 조회 쿼리 1번
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        company.updateCompany(updateDto);
        return companyMapper.entityToDto(company, null);
        // update 쿼리 1번
    }


    // PendingCompany 승인 로직
    @Transactional
    public CompanyDto approvalPendingCompany(PendingCompanyDto pendingCompanyDto, User user){
        CompanyDto companyDto = new CompanyDto(
                pendingCompanyDto.getName(),
                pendingCompanyDto.getDescription(),
                pendingCompanyDto.getLocation(),
                pendingCompanyDto.getPhoneNumber());

        Company company = Company.createCompany(companyDto, user);

        //Company 저장
        // 저장 쿼리 1번
        Company savedCompany = companyRepository.save(company);
        return companyMapper.entityToDto(savedCompany, null);
    }

    @Transactional
    public void deleteCompany(Long id){
        companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
        companyRepository.deleteById(id);
    }

    public Company findById(Long id){
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
    }


}
