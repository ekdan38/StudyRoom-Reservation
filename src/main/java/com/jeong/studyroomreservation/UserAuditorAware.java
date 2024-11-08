package com.jeong.studyroomreservation;

import com.jeong.studyroomreservation.domain.dto.user.UserDto;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            return Optional.empty();
        }

        if(authentication.getPrincipal().equals("anonymousUser")){
            return Optional.of("Anonymous");
        }

        UserDto principal = (UserDto) authentication.getPrincipal();
        return Optional.of(principal.getUsername());
    }
}
