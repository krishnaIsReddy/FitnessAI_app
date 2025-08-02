package com.app.fitness.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Food {

    @Id
    private Integer foodId;

    private String name;
    private int protein;
    private int calories;

    @ManyToOne
    //@JoinColumn(name="user_id")
    private User user;
}
