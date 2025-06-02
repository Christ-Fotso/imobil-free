package com.realestate.gestion_immobiliere.service;

import com.realestate.gestion_immobiliere.model.TypeBien;
import com.realestate.gestion_immobiliere.repository.TypeBienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeBienService {

    @Autowired
    private TypeBienRepository typeBienRepository;

    public List<TypeBien> findAll() {
        return typeBienRepository.findAll();
    }
}
