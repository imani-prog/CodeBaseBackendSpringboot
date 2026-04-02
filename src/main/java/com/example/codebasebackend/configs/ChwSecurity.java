package com.example.codebasebackend.configs;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
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

        return communityHealthWorkersRepository.findById(chwId)
                .map(CommunityHealthWorkers::getUser)
                .map(user -> user.getUsername() != null && user.getUsername().equals(authentication.getName()))
                .orElse(false);
    }
}

