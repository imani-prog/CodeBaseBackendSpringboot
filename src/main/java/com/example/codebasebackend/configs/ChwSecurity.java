package com.example.codebasebackend.configs;

import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("chwSecurity")
@RequiredArgsConstructor
public class ChwSecurity {

    private final CommunityHealthWorkersRepository communityHealthWorkersRepository;

    public boolean isOwner(Long chwId, Authentication authentication) {
        if (chwId == null || authentication == null || authentication.getName() == null) {
            return false;
        }

        return communityHealthWorkersRepository.findByUserUsername(authentication.getName())
                .map(chw -> chwId.equals(chw.getId()))
                .orElse(false);
    }
}

