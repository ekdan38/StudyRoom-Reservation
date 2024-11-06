package com.jeong.studyroomreservation.domain.entity.compnay;

import com.jeong.studyroomreservation.domain.dto.CompanyDto;
import com.jeong.studyroomreservation.domain.entity.base.BaseEntity;
import com.jeong.studyroomreservation.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Company extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_admin_id")
    private User user;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    private Company(User user, String name, String description, String location, String phoneNumber) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }

    //==생성 메서드==//
    public static Company createCompany(CompanyDto dto, User user){
        return new Company(user, dto.getName(), dto.getDescription(), dto.getLocation(), dto.getPhoneNumber());
    }

    static Company dtoToEntity(CompanyDto dto, User user){
        return new Company(dto.getId(), user, dto.getName(), dto.getDescription(), dto.getLocation(), dto.getPhoneNumber());
    }


}
