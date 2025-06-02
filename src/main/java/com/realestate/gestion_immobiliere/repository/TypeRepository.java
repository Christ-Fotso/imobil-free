package com.realestate.gestion_immobiliere.repository;

import com.realestate.gestion_immobiliere.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
}
