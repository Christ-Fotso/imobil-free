package com.realestate.gestion_immobiliere.repository;

import com.realestate.gestion_immobiliere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
}
