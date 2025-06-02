package com.realestate.gestion_immobiliere.service;

import com.realestate.gestion_immobiliere.model.Bien;
import com.realestate.gestion_immobiliere.repository.BienJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realestate.gestion_immobiliere.repository.BienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BienService {
    @Autowired
    private BienRepository bienRepository;
    private static final Logger log = LoggerFactory.getLogger(BienService.class);

    public List<Bien> findAll() {
        return bienRepository.findAll();
    }

    public Bien save(Bien bien) {
        return bienRepository.save(bien);
    }

    public void delete(Long id) {
        bienRepository.deleteById(id);
    }
    public List<Bien> findAllByUserId(Long userId) {
        return bienRepository.findByUserId(userId);
    }

    public Optional<Bien> findById(Long id) {
        return bienRepository.findById(id);
    }

    @Autowired
    private BienJdbcRepository bienJdbcRepository;

    public List<Bien> search(String ville, String type, Integer minSuperficie, Integer maxSuperficie, Double minPrix, Double maxPrix) {
        return bienJdbcRepository.findByCriteria(ville, type, minSuperficie, maxSuperficie, minPrix, maxPrix);
    }
}
