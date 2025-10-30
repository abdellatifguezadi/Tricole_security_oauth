package org.tricol.supplierchain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tricol.supplierchain.dto.request.CommandeFournisseurRequestDTO;
import org.tricol.supplierchain.dto.response.CommandeFournisseurResponseDTO;
import org.tricol.supplierchain.entity.CommandeFournisseur;

@Mapper(componentModel = "spring", uses = {LigneCommandeMapper.class})
public interface CommandeFournisseurMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCommande", ignore = true)
    @Mapping(target = "dateLivraisonEffective", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "fournisseurId", target = "fournisseur.id")
    @Mapping(source = "lignes", target = "lignesCommande")
    CommandeFournisseur toEntity(CommandeFournisseurRequestDTO dto);

    @Mapping(source = "fournisseur.id", target = "fournisseurId")
    @Mapping(source = "fournisseur.raisonSociale", target = "fournisseurNom")
    @Mapping(source = "lignesCommande", target = "lignesCommande")
    CommandeFournisseurResponseDTO toDto(CommandeFournisseur entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCommande", ignore = true)
    @Mapping(target = "dateLivraisonEffective", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "fournisseurId", target = "fournisseur.id")
    @Mapping(source = "lignes", target = "lignesCommande")
    void updateEntityFromDto(CommandeFournisseurRequestDTO dto, @MappingTarget CommandeFournisseur entity);
}