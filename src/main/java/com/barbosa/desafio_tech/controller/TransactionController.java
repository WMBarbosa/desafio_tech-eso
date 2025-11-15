// java
package com.barbosa.desafio_tech.controller;

import com.barbosa.desafio_tech.domain.dto.ComesticDTO;
import com.barbosa.desafio_tech.domain.dto.UserDTO;
import com.barbosa.desafio_tech.domain.entities.Transaction;
import com.barbosa.desafio_tech.domain.service.TransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionsService transactionsService;

    @PostMapping("/purchase")
    public ResponseEntity<UserDTO> purchaseCosmetic(
            @PathVariable Long userId,
            @RequestBody ComesticDTO cosmetic) {
        UserDTO result = transactionsService.purchaseCosmetic(userId, cosmetic);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{cosmeticId}/refund")
    public ResponseEntity<UserDTO> refundCosmetic(@PathVariable Long userId, @PathVariable String cosmeticId,
            @RequestParam(required = false) Integer amount) {
        UserDTO result = transactionsService.refundCosmetic(userId, cosmeticId, amount);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> listTransactions(@PathVariable Long userId) {
        List<Transaction> transactions = transactionsService.listTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance(@PathVariable Long userId) {
        Integer balance = transactionsService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }
}
