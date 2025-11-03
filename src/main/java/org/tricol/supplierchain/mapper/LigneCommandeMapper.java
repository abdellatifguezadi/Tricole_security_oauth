package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.tricol.supplierchain.dto.request.LigneCommandeRequestDTO;
import org.tricol.supplierchain.dto.response.LigneCommandeResponseDTO;
import org.tricol.supplierchain.entity.LigneCommande;

@Mapper(componentModel = "spring", uses = {ProduitMapper.class})
public interface LigneCommandeMapper {

    LigneCommande toEntity(LigneCommandeRequestDTO dto);

    LigneCommandeResponseDTO toDto(LigneCommande entity);

}