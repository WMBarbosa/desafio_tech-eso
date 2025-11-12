package com.barbosa.desafio_tech.domain.service;

import com.barbosa.desafio_tech.domain.dto.ComesticDTO;
import com.barbosa.desafio_tech.domain.dto.UserDTO;
import com.barbosa.desafio_tech.domain.entities.Transaction;
import com.barbosa.desafio_tech.domain.entities.User;
import com.barbosa.desafio_tech.domain.entities.UserCosmetic;
import com.barbosa.desafio_tech.domain.entities.enums.Type;
import com.barbosa.desafio_tech.domain.mappers.UserMapper;
import com.barbosa.desafio_tech.domain.repository.TransactionRepository;
import com.barbosa.desafio_tech.domain.repository.UserComesticRepository;
import com.barbosa.desafio_tech.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final UserComesticRepository userComesticRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO purchaseCosmetic(Long userId, ComesticDTO cosmetic) {
        User user = loadUser(userId);

        Integer price = cosmetic.getPrice();
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Preço inválido para o item");
        }
        if (userComesticRepository.existsByUserIdAndCosmeticIdAndIsActiveTrue(userId, cosmetic.getId())) {
            throw new IllegalStateException("Usuário já possui esse item ativo");
        }
        if (user.getVbucks() < price) {
            throw new IllegalStateException("Saldo insuficiente");
        }

        user.setVbucks(user.getVbucks() - price);

        UserCosmetic userCosmetic = new UserCosmetic();
        userCosmetic.setCosmeticId(cosmetic.getId());
        userCosmetic.setCosmeticName(cosmetic.getName());
        userCosmetic.setPrice(price);
        userCosmetic.setRarity(cosmetic.getRarity());
        userCosmetic.setUser(user);

        userComesticRepository.save(userCosmetic);

        recordTransaction(user, Type.PURCHASE, -price, cosmetic.getId());

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDTO refundCosmetic(Long userId, String cosmeticId, Integer amount) {
        User user = loadUser(userId);
        UserCosmetic cosmetic = userComesticRepository.findByUserIdAndCosmeticIdAndIsActiveTrue(userId, cosmeticId)
                .orElseThrow(() -> new EntityNotFoundException("Cosmético não encontrado para o usuário"));

        cosmetic.setIsActive(false);
        userComesticRepository.save(cosmetic);

        int refundAmount = amount != null ? amount : cosmetic.getPrice();
        user.setVbucks(user.getVbucks() + refundAmount);

        recordTransaction(user, Type.REFUND, refundAmount, cosmeticId);

        return userMapper.toDto(user);
    }

    public List<Transaction> listTransactions(Long userId) {
        loadUser(userId);
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Integer getBalance(Long userId) {
        return loadUser(userId).getVbucks();
    }

    private User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public void recordTransaction(User user, Type type, int amount, String referenceId) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(user.getVbucks());
        transaction.setReferenceId(referenceId);

        transactionRepository.save(transaction);
    }

}
