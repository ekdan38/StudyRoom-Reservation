package com.jeong.studyroomreservation.domain.entity.post.company;

import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostResponseDto;
import com.jeong.studyroomreservation.domain.dto.post.company.CompanyPostUpdateResponseDto;
import com.jeong.studyroomreservation.web.dto.companypost.CompanyPostRequestDto;
import com.jeong.studyroomreservation.web.dto.companypost.CompanyPostUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyPostMapper {

    // Entity => Response
    public CompanyPostResponseDto entityToResponse(CompanyPost entity){
        return new CompanyPostResponseDto(entity.getId(), entity.getTitle(), entity.getContent());
    }

    // Request => Dto
    public CompanyPostDto requestToDto(CompanyPostRequestDto requestDto){
        return new CompanyPostDto(requestDto.getTitle(), requestDto.getContent());
    }

    // Entity => Dto
    public CompanyPostDto entityToDto(CompanyPost entity){
        return new CompanyPostDto(entity.getId(), entity.getTitle(), entity.getContent());
    }

    // response => updateResponse
    public CompanyPostUpdateResponseDto responseToUpdateResponse(CompanyPostResponseDto responseDto){
        CompanyPostUpdateResponseDto updateResponseDto =
                new CompanyPostUpdateResponseDto(responseDto.getId(), responseDto.getTitle(), responseDto.getContent());

        List<String> images = responseDto.getImages();
        for (String image : images) {
            updateResponseDto.getNewImages().add(image);
        }
        return updateResponseDto;
    }

    // updateRequest => Dto
    public CompanyPostDto updateToDto(CompanyPostUpdateRequestDto requestDto){
        return new CompanyPostDto(requestDto.getTitle(), requestDto.getContent());

    }
}
