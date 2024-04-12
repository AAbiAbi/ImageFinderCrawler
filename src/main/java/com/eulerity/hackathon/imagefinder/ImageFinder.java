package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet(
    name = "ImageFinder",
    urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet{
	private static final long serialVersionUID = 1L;

	protected static final Gson GSON = new GsonBuilder().create();

	//This is just a test array
	public static final String[] testImages = {
			"https://images.pexels.com/photos/545063/pexels-photo-545063.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/464664/pexels-photo-464664.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&format=tiny"
  };

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	  resp.setContentType("application/json");
	  String url = req.getParameter("url");

	  // Check if URL parameter is provided
	  if (url == null || url.isEmpty()) {
		  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		  resp.getWriter().write("URL parameter is required.");
		  return;
	  }

	  // Use the Crawler class to fetch image URLs
	  try {
		  Crawler crawler = new Crawler(url,10);
		  Set<String> images = crawler.getImages();
		  Set<String> logoImages = crawler.getLogoImages();
		  
		  
		  Map<String, Object> responseMap = new HashMap<>();
        	responseMap.put("images", images);
        	responseMap.put("logoImages", logoImages);
		  // Send the fetched image URLs as a JSON response
		  resp.setCharacterEncoding("UTF-8");
		  resp.getWriter().write(GSON.toJson(responseMap));
	  } catch (Exception e) {
		  // Handle potential errors gracefully
		  resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		  resp.getWriter().write("An error occurred while processing the URL: " + e.getMessage());
		  e.printStackTrace();
	  }
  }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getParameter("url");
        if (url != null && !url.isEmpty()) {
            Crawler crawler = new Crawler(url,10);
            Set<String> images = crawler.getImages();
            String json = new Gson().toJson(images);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } else {
            response.getWriter().write("URL parameter is required.");
        }
    }
}
