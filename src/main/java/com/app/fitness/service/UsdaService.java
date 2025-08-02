package com.app.fitness.service;


import com.app.fitness.repo.NutritionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UsdaService {


    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${USDA_API_KEY}")
    private String apiKey;

    public Map<String, Object> getNutritionList(String foodName){
        try{
            //Search for the food in USDA table
            String url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + foodName + "&api_key=" + apiKey;
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            List<?> food =  (List<?>) response.get("foods");
            if(food == null || food.isEmpty()){
                return null;
            }

            //Gets the first food item from the Map
            Map<?, ?> firstFood = (Map<?, ?>) food.get(0);
            int fdcID = (int) firstFood.get("fdcId");

            //Gets the nutrition info
            String detailURL ="https://api.nal.usda.gov/fdc/v1/food/" + fdcID + "?api_key=" + apiKey;
            Map<?, ?> detailResponse =  (Map<?, ?>) restTemplate.getForObject(detailURL, Map.class);

            //Extract nutrition data
            List<?> nutrients =  (List<?>) detailResponse.get("foodNutrients");
            if(nutrients == null || nutrients.isEmpty()){
                return null;
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("foodName", firstFood.get("description"));


            Set<String> targetNutrients = Set.of("Energy", "Protein");

            for (Object obj : nutrients) {

                if (!(obj instanceof Map)) {
                    continue;
                }
                Map<?, ?> nutrientEntry = (Map<?, ?>) obj;

                Map<?, ?> nutrient = (Map<?, ?>) nutrientEntry.get("nutrient");
                //Object nutrientInfo = nutrientEntry.get("nutrient");

                if (nutrient == null) {
                    continue;
                }

                String name = (String) nutrient.get("name");
                String unit = (String) nutrient.get("unitName");
                Object amount = nutrientEntry.get("amount");

                if (targetNutrients.contains(name) && amount instanceof Number) {
                    String key = name.equals("Energy") ? "Energy" : name;
                    result.put(key, ((Number) amount).toString() + " " + unit);
                }
            }

            return result;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }


}
