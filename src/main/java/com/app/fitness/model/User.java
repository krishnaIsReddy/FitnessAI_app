package com.app.fitness.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")

public class User {

    @Id
    private Integer id;

    private String username;
    private int proteinGoal;
    private int calorieGoal;
    private int carbGoal;
    private int fatGoal;
    private int height;
    private int weight;
    private String gender;
    private int age;
    private String activityLevel; //None, light, moderate, active
}
