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
import com.barbosa.desafio_tech.domain.service.serviceException.DatabaseException;
import com.barbosa.desafio_tech.domain.service.serviceException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TransactionsService transactionsService;

    private static final int INITIAL_CREDITS = 10_000;

    @Transactional(readOnly = true)
    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Transactional
    public UserDTO create(UserDTO payload) {
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        user.setPassword(payload.getPassword());
        user.setVbucks(INITIAL_CREDITS);

        User saved = userRepository.save(user);
        transactionsService.recordTransaction(saved, Type.CREDIT_INITIAL, INITIAL_CREDITS, null);

        return userMapper.toDto(saved);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        updateData(user, payload);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    "Não é possível excluir o usuario porque está o " + id + " associado a um pedido ");
        }
    }


    public void updateData(User user, UserDTO userDTO) {
        if (userDTO.getName() != null && !userDTO.getName().isBlank()) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isBlank()) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(userDTO.getPassword());
        }
    }

}
