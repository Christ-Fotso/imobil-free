package com.realestate.gestion_immobiliere.service;

import com.realestate.gestion_immobiliere.model.Type;
import com.realestate.gestion_immobiliere.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeService {

    @Autowired
    private TypeRepository typeRepository;

    public List<Type> findAll() {
        return typeRepository.findAll();
    }
}
