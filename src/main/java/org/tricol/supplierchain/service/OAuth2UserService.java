//package org.tricol.supplierchain.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.tricol.supplierchain.entity.UserApp;
//import org.tricol.supplierchain.enums.RoleName;
//import org.tricol.supplierchain.repository.RoleRepository;
//import org.tricol.supplierchain.repository.UserRepository;
//import org.tricol.supplierchain.security.CustomOAuth2User;
//
//@Service
//@RequiredArgsConstructor
//public class OAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//
//
//    @Override
//    @Transactional
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oauth2User = super.loadUser(userRequest);
//
//        String email = oauth2User.getAttribute("email");
//        String username = oauth2User.getAttribute("preferred_username");
//        String fullName = oauth2User.getAttribute("name");
//
//        UserApp user = userRepository.findByEmail(email)
//                .orElseGet(() -> createNewUser(email, username, fullName));
//
//        return new CustomOAuth2User(oauth2User, user);
//    }
//
//    private UserApp createNewUser(String email, String username, String fullName) {
//        UserApp user = UserApp.builder()
//                .email(email)
//                .username(username != null ? username : email)
//                .fullName(fullName)
//                .password("")
//                .enabled(true)
//                .locked(false)
//                .role(null)
//                .build();
//
//        return userRepository.save(user);
//    }
//}