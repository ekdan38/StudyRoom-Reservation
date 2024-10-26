package com.jeong.studyroomreservation.web.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank
    @Size(min = 6, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "loginId는 영어 대소문자와 숫자만 허용됩니다.") // 영어 대소문자, 숫자만 허용
    private String loginId;

    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(
            regexp = "^(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "password는 특수문자가 1개 이상 포함되어야 합니다."
    ) // 대문자, 소문자 허용 + 특수문자 필수
    private String password;

    @NotBlank
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름은 한글 또는 영문만 가능합니다.")
    private String name;


    @NotBlank
    @Email(message = "이메일 주소의 형식이어야 합니다.")
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^\\d{3}-\\d{3,4}-\\d{4}$",  // 한국식 전화번호 형식 (예: 010-1234-5678)
            message = "전화번호는 010-1234-5678 형식이어야 합니다."
    )  // 전화번호 형식 제약 추가
    private String phoneNumber;

    private boolean manager = false;

    private boolean admin = false;
}
