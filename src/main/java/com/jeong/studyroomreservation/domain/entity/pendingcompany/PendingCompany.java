package com.jeong.studyroomreservation.domain.entity.pendingcompany;

import com.jeong.studyroomreservation.domain.dto.PendingCompanyDto;
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
public class PendingCompany {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //unique 생김
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String description;

    private String location;

    private String phoneNumber;

    private PendingCompany(User user, String name, String description, String location, String phoneNumber) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }

    //==생성 메서드==//
    public static PendingCompany createPendingCompany(PendingCompanyDto dto, User user){
        return new PendingCompany(user, dto.getName(), dto.getDescription(), dto.getLocation(), dto.getPhoneNumber());
    }

    static PendingCompany dtoToEntity(PendingCompanyDto dto, User user){
        return new PendingCompany(dto.getId(), user, dto.getName(), dto.getDescription(), dto.getLocation(), dto.getPhoneNumber());
    }

    //==수정 메서드==//
    public void updatePendingCompany(PendingCompanyDto dto){
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.location = dto.getLocation();
        this.phoneNumber = dto.getPhoneNumber();
    }
}
