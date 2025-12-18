package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tricol.supplierchain.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
