package com.app.fitness.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class NutritionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long nutritionId;

    private String foodLabel;
    private String usdaName;
    private double calories;
    private double protein;
}
