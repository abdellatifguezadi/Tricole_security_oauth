package org.tricol.supplierchain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tricol.supplierchain.dto.request.ProduitRequestDTO;
import org.tricol.supplierchain.dto.response.ProduitResponseDTO;
import org.tricol.supplierchain.exception.GlobalHandler;
import org.tricol.supplierchain.service.inter.GestionStockService;
import org.tricol.supplierchain.service.inter.Produitservice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - Autorisation API Produits")
class ProduitControllerUnitTest {

    @Mock
    private Produitservice produitService;

    @Mock
    private GestionStockService stockService;

    @InjectMocks
    private ProduitController produitController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ProduitRequestDTO produitRequestDTO;
    private ProduitResponseDTO produitResponseDTO;
    private List<ProduitResponseDTO> produitsList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(produitController)
                .setControllerAdvice(new GlobalHandler())
                .build();
        objectMapper = new ObjectMapper();

        produitRequestDTO = new ProduitRequestDTO();
        produitRequestDTO.setReference("NEW001");
        produitRequestDTO.setNom("Nouveau Produit");
        produitRequestDTO.setDescription("Nouveau produit test");
        produitRequestDTO.setCategorie("Nouvelle Catégorie");
        produitRequestDTO.setStockActuel(BigDecimal.valueOf(50));
        produitRequestDTO.setPointCommande(BigDecimal.valueOf(5));
        produitRequestDTO.setUniteMesure("kg");

        produitResponseDTO = new ProduitResponseDTO();
        produitResponseDTO.setId(1L);
        produitResponseDTO.setReference("TEST001");
        produitResponseDTO.setNom("Produit Test");
        produitResponseDTO.setDescription("Description test");
        produitResponseDTO.setCategorie("Test");
        produitResponseDTO.setStockActuel(BigDecimal.valueOf(100));
        produitResponseDTO.setPointCommande(BigDecimal.valueOf(10));
        produitResponseDTO.setUniteMesure("pcs");

        produitsList = Arrays.asList(produitResponseDTO);
    }

    @Test
    @DisplayName("Test 3: testListProductWithPermissionRead - Liste des produits avec permission read")
    void testListProductWithPermissionRead() throws Exception {
        when(produitService.getAllProduits()).thenReturn(produitsList);

        mockMvc.perform(get("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].reference").value("TEST001"));
    }

    @Test
    @DisplayName("Test 4: testProductWithPermissionRead - Lecture d'un produit spécifique avec permission read")
    void testProductWithPermissionRead() throws Exception {
        when(produitService.getProduitById(1L)).thenReturn(produitResponseDTO);

        mockMvc.perform(get("/api/v1/produits/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reference").value("TEST001"))
                .andExpect(jsonPath("$.nom").value("Produit Test"));
    }

    @Test
    @DisplayName("Test 5: testAddProductWithPermissionWrite - Ajout de produit avec permission create")
    void testAddProductWithPermissionWrite() throws Exception {
        ProduitResponseDTO newProductResponse = new ProduitResponseDTO();
        newProductResponse.setId(2L);
        newProductResponse.setReference("NEW001");
        newProductResponse.setNom("Nouveau Produit");

        when(produitService.createProduit(any(ProduitRequestDTO.class))).thenReturn(newProductResponse);

        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produitRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("NEW001"))
                .andExpect(jsonPath("$.nom").value("Nouveau Produit"));
    }

    @Test
    @DisplayName("Test 6: testAddProductWithPermissionRead - Refus d'ajout avec seulement permission read")
    void testAddProductWithPermissionRead() throws Exception {
        when(produitService.createProduit(any(ProduitRequestDTO.class)))
                .thenThrow(new AccessDeniedException("Access Denied"));

        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produitRequestDTO)))
                .andExpect(status().isForbidden());
    }
}