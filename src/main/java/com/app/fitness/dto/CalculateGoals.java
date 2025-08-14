package com.app.fitness.dto;


import lombok.Data;

@Data
public class CalculateGoals {
    private int age;
    private int height;
    private int weight;
    private String gender;
    private String activity; //None, light, moderate, active

}
