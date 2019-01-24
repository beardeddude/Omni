package com.omni.omni.service.security;

import com.omni.omni.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthenticationService implements AuthenticationProvider {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!(authentication.getCredentials() instanceof String)) {
            return null;
        }

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        //TODO actually authenticate
        //TODO roles

        return new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
