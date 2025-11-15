package com.barbosa.desafio_tech.controller;

import com.barbosa.desafio_tech.domain.dto.ComesticDTO;
import com.barbosa.desafio_tech.domain.dto.ComesticFilterDTO;
import com.barbosa.desafio_tech.domain.service.ComesticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cosmetics")
@RequiredArgsConstructor
@Tag(name = "Cosméticos", description = "API para consulta de cosméticos do Fortnite")
public class ComesticController {

    private final ComesticService comesticService;

    @Operation(summary = "Listar todos os cosméticos", description = "Retorna uma lista paginada de cosméticos com filtros opcionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cosméticos retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<Page<ComesticDTO>> allCosmetics(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)") Pageable pageable,
            @Parameter(description = "Filtros opcionais (name, type, rarity, isNew, isOnSale)") ComesticFilterDTO filterDTO) {
        Page<ComesticDTO> cosmetics = comesticService.listAll(pageable, filterDTO);
        return ResponseEntity.ok(cosmetics);
    }

    @Operation(summary = "Listar cosméticos novos", description = "Retorna uma lista paginada de cosméticos recém-lançados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cosméticos novos retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/new")
    public ResponseEntity<Page<ComesticDTO>> newCosmetics(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)") Pageable pageable) {
        Page<ComesticDTO> cosmetics = comesticService.listNew(pageable);
        return ResponseEntity.ok(cosmetics);
    }

    @Operation(summary = "Listar cosméticos da loja", description = "Retorna uma lista paginada de cosméticos disponíveis na loja do Fortnite")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cosméticos da loja retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/shop")
    public ResponseEntity<Page<ComesticDTO>> shopCosmetics(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)") Pageable pageable) {
        Page<ComesticDTO> cosmetics = comesticService.listShop(pageable);
        return ResponseEntity.ok(cosmetics);
    }

}
