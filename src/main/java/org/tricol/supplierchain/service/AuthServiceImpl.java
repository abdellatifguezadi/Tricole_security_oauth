package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.LoginRequest;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.AuthResponse;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.enums.RoleName;
import org.tricol.supplierchain.exception.DuplicateResourceException;
import org.tricol.supplierchain.exception.OperationNotAllowedException;
import org.tricol.supplierchain.mapper.UserMapper;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.security.JwtService;
import org.tricol.supplierchain.service.inter.AuthService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        UserApp user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        if (userRepository.count() == 0) {
            roleRepository.findByName(RoleName.ADMIN)
                    .ifPresent(user::setRole);
        }
        
        user = userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse response = userMapper.toAuthResponse(user);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserApp user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        if (user.getRole() == null) {
            throw new OperationNotAllowedException("User does not have an assigned role");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthResponse response = userMapper.toAuthResponse(user);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }
}
