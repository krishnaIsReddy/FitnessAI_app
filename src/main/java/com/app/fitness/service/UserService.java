package com.app.fitness.service;

import com.app.fitness.model.Food;
import com.app.fitness.model.User;
import com.app.fitness.repo.FoodRepo;
import com.app.fitness.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FoodRepo foodRepo;


    //Lets a user log a good item based on the user id
    public void logFood(Integer userId, Food food) {
        User user = userRepo.findById(userId).orElseThrow();
        food.setUser(user);
        foodRepo.save(food);
    }

    //Creates a new user
    public User createUser(User user) {
        userRepo.save(user);
        return user;
    }

    //Retrieves the food in a list logged by a specific user ID
    public List<Food> getFoodHistory(Integer userId){
        return foodRepo.findByUserId(userId);
    }


    //Gets the remaining protein and calorie goals using a hash map
    public Map<String, Double> getRemainingGoals(Integer userId){
        User user =  userRepo.findById(userId).orElseThrow();

        List<Food> foods = foodRepo.findByUserId(userId);

        double proteinDay = 0;
        double calorieDay = 0;

        for (Food food : foods) {
            proteinDay += food.getProtein();
            calorieDay += food.getCalories();
        }

        double proteinGoal = user.getProteinGoal() - proteinDay;
        double calorieGoal = user.getCalorieGoal() - calorieDay;

        Map<String, Double> result = new HashMap<>();
        result.put("proteinGoal", proteinGoal);
        result.put("calorieGoal", calorieGoal);

        return result;
    }


    public String updateGoals(Integer userId, int proteinGoal, int  calorieGoal) {
        User user =  userRepo.findById(userId).orElseThrow();

        user.setProteinGoal(proteinGoal);
        user.setCalorieGoal(calorieGoal);
        userRepo.save(user);
        return "Your goals have been updated!";
    }


    public Optional<User> getUser(Integer userId) {
        return userRepo.findById(userId);
    }



    public String calculateGoals(Integer id, int age, int height, int weight, String gender, String activity) {
        User user = userRepo.findById(id).orElseThrow();

        double bmr;
        //double heightCm = height * 2.54;
        //double weightKg = weight * 0.453592;

        if (gender.equals("male")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        }else{
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        double multiplier = switch (activity.toLowerCase()) {
            case "none" -> 1.2;
            case "light" -> 1.375;
            case "moderate" -> 1.55;
            case "active" -> 1.725;
            default -> 0;
        };

        int dailyCalories = (int) Math.round(bmr * multiplier);
        int protein = (int) Math.round((dailyCalories * 0.30) / 4);
        //int carbs = (int) Math.round((dailyCalories * 0.40) / 4);
        //int fat = (int) Math.round((dailyCalories * 0.30) / 9);

        user.setCalorieGoal(dailyCalories);
        user.setProteinGoal(protein);
        userRepo.save(user);
        //user.setCarbGoal(carbs);
        //user.setFatGoal(fat);

        return "Your goals have been successfully calculated and updated!";
    }



}
