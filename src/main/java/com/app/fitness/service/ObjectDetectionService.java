package com.app.fitness.service;

import com.app.fitness.repo.NutritionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Service
public class ObjectDetectionService {


    public List<String> detectObjects(MultipartFile file) throws IOException {

        if(file.isEmpty()){
            throw new IllegalArgumentException("file is empty");
        }

        List<String> detectedObjects = new ArrayList<>();
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(file.getInputStream());

        Image image = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(image).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    throw new IOException("Vision API Error: " + res.getError().getMessage());
                }

                for (LocalizedObjectAnnotation object : res.getLocalizedObjectAnnotationsList()) {
                    if (object.getScore() > 0.6){
                        detectedObjects.add(object.getName());
                    }
                    //detectedObjects.add(object.getName());
                }
            }
        }
        return detectedObjects;
    }

}
