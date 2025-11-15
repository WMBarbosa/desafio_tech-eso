package com.barbosa.desafio_tech.controller;

import com.barbosa.desafio_tech.domain.dto.UserCosmeticDTO;
import com.barbosa.desafio_tech.domain.service.UserCosmeticService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/cosmetics")
@RequiredArgsConstructor
@Tag(name = "Cosméticos do Usuário", description = "API para gerenciamento de cosméticos possuídos pelos usuários")
public class UserCosmeticController {

    private final UserCosmeticService userCosmeticService;

    @Operation(summary = "Listar cosméticos do usuário", description = "Retorna uma lista paginada de todos os cosméticos possuídos por um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cosméticos retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping
    public ResponseEntity<Page<UserCosmeticDTO>> getAllUserCosmetics(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Parâmetros de paginação (page, size, sort)") Pageable pageable) {
        Page<UserCosmeticDTO> cosmetics = userCosmeticService.findAllByUserId(userId, pageable);
        return ResponseEntity.ok(cosmetics);
    }

    @Operation(summary = "Buscar cosmético do usuário por ID", description = "Retorna um cosmético específico do usuário pelo ID do registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cosmético encontrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCosmeticDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cosmético ou usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserCosmeticDTO> getUserCosmeticById(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "ID do registro do cosmético", required = true, example = "1")
            @PathVariable Long id) {
        UserCosmeticDTO cosmetic = userCosmeticService.findById(id);
        return ResponseEntity.ok(cosmetic);
    }

    @Operation(summary = "Buscar cosmético por ID do cosmético", description = "Retorna um cosmético do usuário pelo ID do cosmético (Fortnite ID)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cosmético encontrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCosmeticDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cosmético ou usuário não encontrado")
    })
    @GetMapping("/cosmetic/{cosmeticId}")
    public ResponseEntity<UserCosmeticDTO> getUserCosmeticByCosmeticId(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "ID do cosmético (Fortnite ID)", required = true, example = "cosmetic-123")
            @PathVariable String cosmeticId) {
        UserCosmeticDTO cosmetic = userCosmeticService.findByUserIdAndCosmeticId(userId, cosmeticId);
        return ResponseEntity.ok(cosmetic);
    }

    @Operation(summary = "Buscar cosmético por nome", description = "Busca um cosmético do usuário pelo nome (busca parcial)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cosmético encontrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCosmeticDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cosmético não encontrado")
    })
    @GetMapping("/search")
    public ResponseEntity<UserCosmeticDTO> findNameLike(
            @Parameter(description = "Nome do cosmético para busca (busca parcial)", required = true, example = "skin")
            @RequestParam String name,
            @Parameter(description = "Parâmetros de paginação (page, size, sort)") Pageable pageable) {
        Page<UserCosmeticDTO> cosmetics = userCosmeticService.findByFirstnameLike(name, pageable);
        return ResponseEntity.ok().body(cosmetics.getContent()
                .isEmpty() ? null : cosmetics.getContent()
                .get(0));
    }
}

