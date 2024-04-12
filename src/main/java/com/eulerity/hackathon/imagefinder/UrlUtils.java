package com.eulerity.hackathon.imagefinder;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {
    /**
     * Normalizes a URL by ensuring it has a protocol prefix and does not contain 'www.'.
     * @param url The URL to normalize.
     * @return The normalized URL.
     */
    public static String normalizeUrl(String url) {
        // Ensure the URL starts with a protocol
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        try {
            URL netUrl = new URL(url);
            
            // String host = netUrl.getHost().toLowerCase();
            
            // Remove "www." if present
            // if (host.startsWith("www.")) {
            //     host = host.substring(4);
            // }
            String protocol = netUrl.getProtocol();
            String host = netUrl.getHost().toLowerCase();
            System.err.println("Host: " + host);
            return protocol + "://" + host;
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL in normalization: " + url);
            return ""; // Return empty string to indicate malformed URL
        }
    }

    /**
     * Extracts the domain name from a URL. This method assumes the URL is already normalized.
     * @param url The normalized URL from which to extract the domain name.
     * @return The domain name, excluding subdomains.
     */

    public static String getDomainName(String url) {
        // Ensure the URL starts with a protocol
        url = UrlUtils.normalizeUrl(url);
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url; // Default to http if no protocol is specified
        }
        try {
            URL netUrl = new URL(url);
            String host = netUrl.getHost();
            
            // This will return the domain name without subdomains.
            // If you want to keep subdomains, you can directly return the host.
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            System.err.println("Received URL: " + url);
            return host;
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + url);
            return null; // Or handle this case as you see fit.
        }
    }

    /**
     * Extracts the base domain from a URL, excluding subdomains.
     * @param url The URL from which to extract the base domain.
     * @return The base domain.
     */

    public static String extractBaseDomain(String url) {
        String[] parts = url.split("\\.");
        if (parts.length > 2) {
            return parts[parts.length - 2] + "." + parts[parts.length - 1];
        }
        return url;
    }

    public static String canonicalizeUrl(String url) {
        try {
            URL u = new URL(url);
            String path = u.getPath().endsWith("/") ? u.getPath() : u.getPath() + "/";
            return new URL(u.getProtocol(), u.getHost(), u.getPort(), path).toString();
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + url);
            return url;
        }
    }

    public static String stripIrrelevantParameters(String url) {
        try {
            URL u = new URL(url);
            return new URL(u.getProtocol(), u.getHost(), u.getPort(), u.getPath()).toString();
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + url);
            return url;
        }
    }
}
