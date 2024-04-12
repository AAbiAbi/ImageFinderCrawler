package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

public class ImageFinderTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter sw;
    private PrintWriter pw;
    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        sw = new StringWriter();
        pw = new PrintWriter(sw);
        Mockito.when(response.getWriter()).thenReturn(pw);
        session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);
    }

    @Test
	public void testDoPostWithValidUrl() throws IOException, ServletException {
		// Setup
		String testUrl = "http://example.com";
		Mockito.when(request.getParameter("url")).thenReturn(testUrl);
		Mockito.when(request.getServletPath()).thenReturn("/main");

		// Execute
		new ImageFinder().doPost(request, response);

		// Verify that the method writes the expected JSON output
		// This is what we expect based on the servlet's implementation
		String expectedOutput = "{\"images\":[],\"logoImages\":[]}"; // Update this as needed based on actual implementation
		Assert.assertEquals(expectedOutput, sw.toString().trim());
	}

    @Test
    public void testDoPostWithoutUrl() throws IOException, ServletException {
        // Setup
        Mockito.when(request.getParameter("url")).thenReturn(null);
        Mockito.when(request.getServletPath()).thenReturn("/main");

        // Execute
        new ImageFinder().doPost(request, response);

        // Verify that the servlet responds with an error message
        String expectedOutput = "URL parameter is required.";
        Assert.assertEquals(expectedOutput, sw.toString().trim());
    }
}
