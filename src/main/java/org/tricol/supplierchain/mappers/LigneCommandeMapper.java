package org.tricol.supplierchain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tricol.supplierchain.dto.request.LigneCommandeRequestDTO;
import org.tricol.supplierchain.dto.response.LigneCommandeResponseDTO;
import org.tricol.supplierchain.entity.LigneCommande;
import org.tricol.supplierchain.entity.Produit;

@Mapper(componentModel = "spring")
public interface LigneCommandeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande", ignore = true)
    @Mapping(source = "produitId", target = "produit.id")
    LigneCommande toEntity(LigneCommandeRequestDTO dto);

    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "produitNom")
    LigneCommandeResponseDTO toDto(LigneCommande entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande", ignore = true)
    @Mapping(source = "produitId", target = "produit.id")
    void updateEntityFromDto(LigneCommandeRequestDTO dto, @MappingTarget LigneCommande entity);
}