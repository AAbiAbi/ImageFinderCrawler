package com.eulerity.hackathon.imagefinder;

import com.amazonaws.services.rekognition.model.Label;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "ImageAnalysisServlet", urlPatterns = {"/analyzeImages"})
public class ImageAnalysisServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String data = request.getReader().lines().collect(Collectors.joining());
        Gson gson = new Gson();
        ImageAnalysisRequest req = gson.fromJson(data, ImageAnalysisRequest.class);

        // Assume ImageAnalysis class exists and can analyze images
        ImageAnalysisTest analysis = new ImageAnalysisTest();

        // Placeholder for analysis results
        Map<String, Boolean> results = new HashMap<>();

        List<String> imageUrls = req.images;

        System.out.println("Received image URLs: " + imageUrls);

        for (int i = 0; i < imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);
            boolean containsPeople = analysis.analyzeImage(imageUrl);
            results.put(imageUrl, containsPeople);
        }

        // Send the analysis results back
        String jsonResponse = gson.toJson(results);
        response.getWriter().write(jsonResponse);
    }

    // Helper class for GSON
    static class ImageAnalysisRequest {
        List<String> images;
    }
}
