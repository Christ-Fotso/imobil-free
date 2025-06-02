package com.realestate.gestion_immobiliere.model;

public class FinancialSummary {
    private double monthly;
    private double total;
    private double annual;

    public FinancialSummary(double monthly, double total, double annual) {
        this.monthly = monthly;
        this.total = total;
        this.annual = annual;
    }

    // Getters and setters
}
