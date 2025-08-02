package com.app.fitness.repo;

import com.app.fitness.model.NutritionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionRepo extends JpaRepository<NutritionRecord, Long> {
}
