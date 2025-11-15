package com.barbosa.desafio_tech.controller;

import com.barbosa.desafio_tech.domain.dto.ComesticDTO;
import com.barbosa.desafio_tech.domain.dto.UserDTO;
import com.barbosa.desafio_tech.domain.entities.Transaction;
import com.barbosa.desafio_tech.domain.service.TransactionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "API para gerenciamento de transações de compra e reembolso de cosméticos")
public class TransactionController {

    private final TransactionsService transactionsService;

    @Operation(summary = "Comprar cosmético", description = "Realiza a compra de um cosmético para o usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra realizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou cosmético inválido")
    })
    @PostMapping("/purchase")
    public ResponseEntity<UserDTO> purchaseCosmetic(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Dados do cosmético a ser comprado", required = true)
            @RequestBody ComesticDTO cosmetic) {
        UserDTO result = transactionsService.purchaseCosmetic(userId, cosmetic);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Reembolsar cosmético", description = "Realiza o reembolso de um cosmético comprado pelo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reembolso realizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário ou cosmético não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cosmético não pode ser reembolsado ou quantidade inválida")
    })
    @PostMapping("/{cosmeticId}/refund")
    public ResponseEntity<UserDTO> refundCosmetic(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "ID do cosmético a ser reembolsado", required = true, example = "cosmetic-123")
            @PathVariable String cosmeticId,
            @Parameter(description = "Quantidade a ser reembolsada (opcional, padrão: 1)", example = "1")
            @RequestParam(required = false) Integer amount) {
        UserDTO result = transactionsService.refundCosmetic(userId, cosmeticId, amount);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Listar transações", description = "Retorna todas as transações de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping
    public ResponseEntity<List<Transaction>> listTransactions(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long userId) {
        List<Transaction> transactions = transactionsService.listTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Consultar saldo", description = "Retorna o saldo atual de V-Bucks do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long userId) {
        Integer balance = transactionsService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }
}
