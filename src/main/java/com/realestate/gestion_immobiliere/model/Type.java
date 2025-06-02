package com.realestate.gestion_immobiliere.model;

import jakarta.persistence.*;

@Entity
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Getters and Setters
}
