package com.app.fitness.repo;

import com.app.fitness.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepo extends JpaRepository<Food, Integer> {
    List<Food> findByUserId(Integer userId);
}

