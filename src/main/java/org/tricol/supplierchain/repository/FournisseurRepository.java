package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tricol.supplierchain.entity.Fournisseur;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
}
