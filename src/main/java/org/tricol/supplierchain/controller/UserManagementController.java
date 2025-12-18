package org.tricol.supplierchain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.UserPermissionRequest;
import org.tricol.supplierchain.dto.response.UserPermissionResponse;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.service.inter.UserManagementService;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<UserPermissionResponse> assignPermission(@RequestBody UserPermissionRequest request, Authentication authentication) {
        UserApp admin = (UserApp) authentication.getPrincipal();
        UserPermissionResponse response = userManagementService.assignPermissionToUser(request, admin.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<String> removePermission(@PathVariable Long userId, @PathVariable Long permissionId) {
        userManagementService.removePermissionFromUser(userId, permissionId);
        return ResponseEntity.ok("Permission removed successfully");
    }

    @PatchMapping("/{userId}/permissions/{permissionId}/toggle")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<String> togglePermission(@PathVariable Long userId, @PathVariable Long permissionId, @RequestParam boolean active) {
        userManagementService.togglePermissionStatus(userId, permissionId, active);
        return ResponseEntity.ok("Permission status updated successfully");
    }
}
