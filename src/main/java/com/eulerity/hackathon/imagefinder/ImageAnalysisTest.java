package com.eulerity.hackathon.imagefinder;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.util.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ImageAnalysisTest {

    private AmazonRekognition rekognitionClient;

    public ImageAnalysisTest() {
        // Initialize the Amazon Rekognition client
        rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
    }

    public boolean analyzeImage(String imageUrl) {
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            DetectLabelsRequest request = new DetectLabelsRequest()
                    .withImage(new Image().withBytes(java.nio.ByteBuffer.wrap(bytes)))
                    .withMaxLabels(10)
                    .withMinConfidence(75F);

            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            return containsPeople(result.getLabels());
        } catch (Exception e) {
            System.err.println("Rekognition API call or image processing failed: " + e.getMessage());
            return false;
        }
    }

    private boolean containsPeople(List<Label> labels) {
        for (Label label : labels) {
            if ("Person".equalsIgnoreCase(label.getName()) && label.getConfidence() > 75) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        ImageAnalysisTest imageAnalysis = new ImageAnalysisTest();
        String imageUrl = "https://storage.googleapis.com/platform-data-figma/uploads/ggzy2lbamaaqaje_1.jpg";
        boolean containsPeople = imageAnalysis.analyzeImage(imageUrl);
        System.out.println("Image contains people: " + containsPeople);
    }
}
