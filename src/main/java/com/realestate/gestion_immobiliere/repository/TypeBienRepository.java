package com.realestate.gestion_immobiliere.repository;

import com.realestate.gestion_immobiliere.model.TypeBien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeBienRepository extends JpaRepository<TypeBien, Long> {
}
