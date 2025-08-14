package com.app.fitness.controller;


import com.app.fitness.dto.CalculateGoals;
import com.app.fitness.dto.UpdatedGoalRequest;
import com.app.fitness.model.Food;
import com.app.fitness.model.NutritionRecord;
import com.app.fitness.model.User;
import com.app.fitness.repo.NutritionRepo;
import com.app.fitness.service.ObjectDetectionService;
import com.app.fitness.service.UsdaService;
import com.app.fitness.service.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;



@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private NutritionRepo nutritionRepo;


    @Autowired
    private UserService service;

    private UpdatedGoalRequest request;
    private CalculateGoals goals;

    @Autowired
    private ObjectDetectionService detection;

    @Autowired
    private UsdaService usda;

    @RequestMapping("/")
    public String hello(){
        return "hello";
    }


    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user){
        return ResponseEntity.ok(service.createUser(user));
    }




    @PutMapping("/users/{userId}/goals")
    public ResponseEntity<String> updateGoals(@PathVariable Integer userId,
                                            @RequestBody UpdatedGoalRequest request)
    {
        int protein = request.getProteinGoal();
        int calorie = request.getCalorieGoal();

        return ResponseEntity.ok(service.updateGoals(userId, protein, calorie));
    }

    @PutMapping("/users/{userId}/goals/update")
    public ResponseEntity<String> calculateGoals(@PathVariable Integer userId,
                                                 @RequestBody CalculateGoals goals){
        int age = goals.getAge();
        int height = goals.getHeight();
        int weight = goals.getWeight();
        String gender = goals.getGender();
        String activity = goals.getActivity();

        return ResponseEntity.ok(service.calculateGoals(userId, age, height, weight, gender, activity));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Optional<User>> getUser(@PathVariable Integer userId){
        return ResponseEntity.ok(service.getUser(userId));
    }


    @PostMapping("/food/{userId}/log")
    public ResponseEntity logFood(@PathVariable Integer userId,
                                  @RequestBody Food food){
        service.logFood(userId, food);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/food/{userId}/history")
    public List<Food> getFoodHistory(@PathVariable Integer userId){
        return service.getFoodHistory(userId);
    }


    @GetMapping("/food/{userId}/remainingGoals")
    public Map<String, Double> getRemainingGoals(@PathVariable Integer userId){
        return service.getRemainingGoals(userId);
    }


    //Receive an image input

    @PostMapping("/images/upload")
    public ResponseEntity<String> imageUpload(@RequestParam("image") MultipartFile file){
        if(file == null || file.isEmpty()){
            return ResponseEntity.badRequest().body("Image is empty or not uploaded");
        }

        String filename = file.getOriginalFilename();
        long size = file.getSize();



        return ResponseEntity.ok("Image Received: " + filename + ", size: " + size);
    }


    @PostMapping("/images/detect")
    public ResponseEntity<?> detectImage(@RequestParam("image") MultipartFile file) throws IOException {
        List<String> object = detection.detectObjects(file);
        try{
            return ResponseEntity.ok(object);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("Image could not be processed: " + e.getMessage());
        }
    }


    @PostMapping("/images/nutrition")
    public ResponseEntity<?> getNutrition(@RequestParam String food){
        return ResponseEntity.ok(usda.getNutritionList(food));
    }


    @PostMapping("/images/nutrition/information")
    public ResponseEntity<?> getImageAndFood(@RequestParam("image") MultipartFile file) throws IOException {
        List<String> detectedFoods = detection.detectObjects(file);

        if(detectedFoods.isEmpty()){
            return ResponseEntity.badRequest().body("Image could not be processed: " + detectedFoods);
        }

        Set<String> uniqueFoods = new HashSet<>();
        for(String food : detectedFoods){
            if(food != null && !food.trim().isEmpty()){
                uniqueFoods.add(food.trim().toLowerCase());
            }
        }

        List<Map<String, Object>> allNutrition = new ArrayList<>();

        for(String food : uniqueFoods){
            Map<String, Object> nutrition = usda.getNutritionList(food);
            if(nutrition != null && !nutrition.isEmpty()){
                Map<String, Object> foodEntry = new HashMap<>();
                foodEntry.put("Food", capitalize(food));
                foodEntry.put("Nutrition info", nutrition);
                allNutrition.add(foodEntry);


                String usdaName = (String) nutrition.get("foodName");
                double calories = (nutrition.get("Energy") instanceof Number)
                        ? ((Number) nutrition.get("Energy")).doubleValue()
                        : 0.0;
                double protein = (nutrition.get("Protein") instanceof Number)
                        ? ((Number) nutrition.get("Protein")).doubleValue()
                        : 0.0;
                //double calories = ((Number) nutrition.getOrDefault("Calories", 0.0)).doubleValue();
                //double protein = ((Number) nutrition.getOrDefault("Protein", 0.0)).doubleValue();
                NutritionRecord record = NutritionRecord.builder()
                        .foodLabel(food)
                        .usdaName(usdaName)
                        .calories(calories)
                        .protein(protein)
                        .build();

                nutritionRepo.save(record);
            }
        }



        if(allNutrition.isEmpty()){
            return ResponseEntity.ok("No nutritional info could be found for the detected image: " + detectedFoods);
        }




//        System.out.println("Detected food: " + foods);
//        System.out.println("Nutrition map: " + allNutrition);

        return ResponseEntity.ok(allNutrition);

    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()){
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
