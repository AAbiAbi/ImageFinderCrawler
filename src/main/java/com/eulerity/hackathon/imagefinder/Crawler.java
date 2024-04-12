package com.eulerity.hackathon.imagefinder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.eulerity.hackathon.imagefinder.UrlUtils;

public class Crawler {

    // private ImageAnalysis imageAnalysis = new ImageAnalysis();

    private Set<String> peopleImages = ConcurrentHashMap.newKeySet(); // Store images containing people
    private static final long REQUEST_DELAY_MS = 1000; // Delay between requests, e.g., 1000 milliseconds for 1 second

    private Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private Set<String> images = ConcurrentHashMap.newKeySet();
    private Set<String> logoImages = ConcurrentHashMap.newKeySet();
    private String domain;
    private final ExecutorService executor;
    private AtomicInteger activeTasks = new AtomicInteger(0);

    public Crawler(String startUrl, int numberOfThreads) {
        this.domain = UrlUtils.getDomainName(startUrl);
        this.executor = Executors.newFixedThreadPool(numberOfThreads);
        submitUrl(startUrl);
        waitForCompletionAndShutdown();
    }

    private void submitUrl(String url) {
        activeTasks.incrementAndGet(); // Increment active tasks count
        executor.submit(() -> {
            try {
                crawl(url);
            } finally {
                if (activeTasks.decrementAndGet() == 0) { // Decrement and check if this was the last task
                    executor.shutdown(); // Initiates shutdown when no more active tasks
                }
            }
        });
    }

    private void waitForCompletionAndShutdown() {
        try {
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private void crawl(String url) {
        // Normalize URL before processing
        String normalizedUrl = UrlUtils.normalizeUrl(url);

        if (!isSameDomain(normalizedUrl) || visitedUrls.contains(normalizedUrl)) {
            System.err.println("Skipping URL: " + normalizedUrl);
            return;
        }

        try {
            System.err.println("Crawling URL: " + normalizedUrl);
            Thread.sleep(REQUEST_DELAY_MS); 
            Connection.Response response = Jsoup.connect(normalizedUrl).followRedirects(true).execute(); // Use the normalized URL
            String finalUrl = response.url().toString();
            if (visitedUrls.contains(finalUrl)) {
                return;
            }
            Document doc = response.parse();
            visitedUrls.add(normalizedUrl);

            System.err.println("Visited URLs: " + visitedUrls);
            // Process images
            Elements media = doc.select("[src]");
            for (Element src : media) {
                if (src.tagName().equals("img")) {
                    String imgUrl = src.attr("abs:src");
                    if (!imgUrl.isEmpty()) { // Ensure the URL is not empty
                        if (imgUrl.contains("/logo") || src.attr("alt").toLowerCase().contains("logo")) {
                            // Special handling for logo images
                            // images.add(imgUrl);
                            handleLogoImage(imgUrl);
                        } else {
                            // Add other images normally
                            images.add(imgUrl);
                        }
                    }
                }
            }

            // Recursively crawl links found on the page
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String linkUrl = link.attr("abs:href");
                submitUrl(linkUrl); // No need to normalize here; normalization happens at the start of `crawl`
            }
            
            collectFavicons(doc);

        } catch (IOException | InterruptedException e) {
            System.err.println("For '" + url + "': " + e.getMessage());
        }

        
    }

    private void handleLogoImage(String imgUrl) {
        // Add the logo URL to a specific set or list
        // You could also download the logo, analyze it, etc.
        logoImages.add(imgUrl);
        System.out.println("Found a logo image: " + imgUrl);
        // Add to a specific collection if needed
    }

    public Set<String> getImages() {
        return images;
    }

    public Set<String> getLogoImages() {
        return logoImages;
    }

    
    private boolean isSameDomain(String url) {
        String baseDomain = UrlUtils.extractBaseDomain(this.domain);
        String targetDomain = UrlUtils.extractBaseDomain(UrlUtils.normalizeUrl(url));
        return targetDomain.equals(baseDomain);
    }

    // Call this method to wait for all tasks to finish and shut down the executor
    public void awaitTermination() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
    

    private void collectFavicons(Document doc) {
        Elements faviconLinks = doc.head().select("link[href][rel~=icon]");
        for (Element link : faviconLinks) {
            String faviconUrl = link.attr("abs:href");
            if (!faviconUrl.isEmpty() && !logoImages.contains(faviconUrl)) { // Check to avoid duplicates
                logoImages.add(faviconUrl);
                System.out.println("Favicon found: " + faviconUrl);
            }
        }
    }

    // public boolean containsPeople(String imageUrl) {
    //     // Pseudocode for calling an image recognition API
    //     ImageRecognitionApiResponse response = callImageRecognitionApi(imageUrl);
    //     return response.containsPeople();
    // }
    
}
