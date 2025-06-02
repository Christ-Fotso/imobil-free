package com.realestate.gestion_immobiliere.repository;

import com.realestate.gestion_immobiliere.model.Transaction;
import com.realestate.gestion_immobiliere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    @Query("SELECT t FROM Transaction t JOIN t.bien b WHERE b.user.id = :userId")
    List<Transaction> findByBienUserId(@Param("userId") Long userId);

}
