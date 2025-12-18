package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.UserPermissionRequest;
import org.tricol.supplierchain.entity.Permission;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.dto.response.UserPermissionResponse;
import org.tricol.supplierchain.entity.RoleApp;
import org.tricol.supplierchain.entity.UserPermission;
import org.tricol.supplierchain.exception.DuplicateResourceException;
import org.tricol.supplierchain.exception.OperationNotAllowedException;
import org.tricol.supplierchain.exception.ResourceNotFoundException;
import org.tricol.supplierchain.mapper.UserPermissionMapper;
import org.tricol.supplierchain.repository.PermissionRepository;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserPermissionRepository;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.service.inter.UserManagementService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserPermissionMapper userPermissionMapper;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserPermissionResponse assignPermissionToUser(UserPermissionRequest request, Long adminId) {
        UserApp user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Permission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        if (userPermissionRepository.findByUserIdAndPermissionId(request.getUserId(), request.getPermissionId()).isPresent()) {
            throw new DuplicateResourceException("Permission already assigned to user");
        }

        UserPermission userPermission = UserPermission.builder()
                .user(user)
                .permission(permission)
                .active(true)
                .grantedBy(adminId)
                .build();

        userPermission = userPermissionRepository.save(userPermission);
        return userPermissionMapper.toResponse(userPermission);
    }

    @Override
    @Transactional
    public void removePermissionFromUser(Long userId, Long permissionId) {
        UserPermission userPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("User permission not found"));

        userPermissionRepository.delete(userPermission);
    }

    @Override
    @Transactional
    public void activatePermission(Long userId, Long permissionId) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        UserPermission userPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId)
                .orElseGet(() -> UserPermission.builder()
                        .user(user)
                        .permission(permission)
                        .build());

        if (userPermission.getId() != null && userPermission.isActive()) {
            throw new OperationNotAllowedException("Permission is already active");
        }

        userPermission.setActive(true);
        userPermission.setRevokedAt(null);
        userPermissionRepository.save(userPermission);
    }

    @Override
    @Transactional
    public void deactivatePermission(Long userId, Long permissionId) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        UserPermission userPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId)
                .orElseGet(() -> UserPermission.builder()
                        .user(user)
                        .permission(permission)
                        .build());

        if (userPermission.getId() != null && !userPermission.isActive()) {
            throw new OperationNotAllowedException("Permission is already deactivated");
        }

        userPermission.setActive(false);
        userPermission.setRevokedAt(LocalDateTime.now());
        userPermissionRepository.save(userPermission);
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != null) {
            throw new DuplicateResourceException("User already has a role");
        }

        RoleApp role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setRole(role);
        userRepository.save(user);
    }
}
