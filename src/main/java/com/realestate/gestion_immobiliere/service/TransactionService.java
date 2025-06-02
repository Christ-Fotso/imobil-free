package com.realestate.gestion_immobiliere.service;

import com.realestate.gestion_immobiliere.model.Bien;
import com.realestate.gestion_immobiliere.model.Transaction;
import com.realestate.gestion_immobiliere.model.User;
import com.realestate.gestion_immobiliere.repository.BienRepository;
import com.realestate.gestion_immobiliere.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BienRepository bienRepository;

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> findByUser(User user) {
        return transactionRepository.findByUserId(user.getId());
    }


    public List<Transaction> findByBienUserId(Long userId) {
        return transactionRepository.findByBienUserId(userId);
    }
}
