package org.tricol.supplierchain.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.tricol.supplierchain.entity.UserApp;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final UserApp userApp;

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userApp.getRole() != null) {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userApp.getRole().getName().name())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return userApp.getUsername();
    }

    public UserApp getUserApp() {
        return userApp;
    }
}